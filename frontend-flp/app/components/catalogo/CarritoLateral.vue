<template>
  <div v-if="esSidebar" class="w-full min-h-[50vh] max-h-[85vh] bg-white border border-border-soft flex flex-col">
    <div class="flex items-center justify-between px-4 py-3 border-b border-border-soft">
      <h2 class="font-serif text-base text-text-primary font-medium">{{ titulo }}</h2>
      <button class="text-text-primary/60 hover:text-text-primary p-1 lg:hidden" @click="store.cerrarOverlay()">
        <Icon icon="mdi:close" class="text-xl" />
      </button>
    </div>
    <ContCartInterno @realizar-pedido="handleRealizarPedido" @cerrar-y-limpiar="cerrarYLimpiar" />
  </div>

  <Teleport v-if="esOverlay && !soloSidebar" to="body">
    <Transition name="cart-slide">
      <div v-if="store.abierto" class="fixed inset-0 z-50 flex justify-end">
        <div class="absolute inset-0 bg-black/30" @click="store.cerrarOverlay()" />
        <div class="relative w-full max-w-md bg-white h-full flex flex-col shadow-2xl">
          <div class="flex items-center justify-between px-5 py-4 border-b border-border-soft">
            <h2 class="font-serif text-lg text-text-primary font-medium">{{ titulo }}</h2>
            <button class="text-text-primary/60 hover:text-text-primary p-1" @click="store.cerrarOverlay()">
              <Icon icon="mdi:close" class="text-xl" />
            </button>
          </div>
          <ContCartInterno @realizar-pedido="handleRealizarPedido" @cerrar-y-limpiar="cerrarYLimpiar" />
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useCartStore } from '../../stores/cart.store'
import ContCartInterno from './ContCartInterno.vue'
import { apiClient } from '~/services/api-client'
import { localService } from '~/services/local.service'
import type { Local } from '~/models/local.model'

const store = useCartStore()

const props = defineProps<{ soloSidebar?: boolean }>()

const locales = ref<Local[]>([])

onMounted(async () => {
  try {
    locales.value = await localService.listarActivos()
  } catch {
    locales.value = []
  }
})

const titulo = computed(() => {
  if (store.modoCheckout === 'confirm') return 'Pedido Confirmado'
  if (store.modoCheckout === 'checkout') return 'Datos de Entrega'
  return 'Tu Carrito'
})

const esSidebar = computed(() => store.vista === 'sidebar')
const esOverlay = computed(() => store.vista === 'overlay')

/**
 * Normaliza el teléfono agregando el prefijo nacional +57 por defecto cuando
 * el número es local de 10 dígitos (Colombia).
 *
 * @author santiago
 */
function normalizarTelefono(tel: string): string {
  if (!tel) return tel
  const digitos = tel.replace(/\D/g, '')
  if (digitos.startsWith('57') && digitos.length === 12) return '+57 ' + digitos.slice(2)
  if (digitos.length === 10) return '+57 ' + digitos
  return tel.trim()
}

/**
 * En modo retiro resuelve la direccion/ciudad/region del local elegido para
 * enviarla al backend (que la requiere), manteniendo intactas las validaciones.
 *
 * @author santiago (sesion 05/08/2026 - retiro en local)
 */
function datosEntrega() {
  const f = store.checkoutForm
  const local = locales.value.find(l => l.id === f.localSeleccionadoId) || null
  return {
    direccion: local?.direccion ?? f.direccion,
    ciudad: local?.ciudad ?? f.ciudad,
    region: local?.region ?? f.region,
  }
}

async function handleRealizarPedido() {
  store.errorMsg = ''
  const f = store.checkoutForm
  const esRetiro = f.modoEntrega === 'retiro'
  if (!f.nombre || !f.email || !f.fechaEntrega || !f.telefono) {
    store.errorMsg = 'Todos los campos obligatorios deben estar llenos.'
    return
  }
  if (store.tienePersonalizados && !f.cedula) {
    store.errorMsg = 'Cédula es obligatoria para pedidos personalizados.'
    return
  }
  if (esRetiro && !locales.value.some(l => l.id === f.localSeleccionadoId)) {
    store.errorMsg = 'Debes elegir un local de retiro.'
    return
  }
  const datos = datosEntrega()

  try {
    const res = await apiClient.post('/api/pagos/wompi/iniciar-rapido', {
      nombreCliente: f.nombre,
      emailCliente: f.email,
      direccionEntrega: datos.direccion,
      ciudad: datos.ciudad,
      region: datos.region,
      fechaEntrega: f.fechaEntrega,
      tipoPedido: 'CATALOGO',
      tipoPago: 'COMPLETO',
      items: store.items.map(i => ({
        idRamo: i.ramo.id,
        cantidad: i.cantidad,
      })),
      floresPersonalizadas: store.personalizados.length > 0
        ? store.personalizados.flatMap(p => p.flores.map(f2 => ({
          tipoFlorId: f2.tipoFlorId,
          colorFlorId: f2.colorFlorId,
          cantidad: f2.cantidad,
        })))
        : undefined,
      adicionesPersonalizadas: store.personalizados.length > 0
        ? store.personalizados.flatMap(p => p.adiciones.map(a2 => ({
            inventarioId: a2.inventarioId,
            cantidad: a2.cantidad,
          })))
        : undefined,
      cedulaCliente: f.cedula || undefined,
      telefonoCliente: normalizarTelefono(f.telefono) || undefined,
      responseUrl: window.location.origin + '/pago/resultado',
    })

    if (res.signature) {
      store.limpiarCarrito()
      const formEl = document.createElement('form')
      formEl.method = 'GET'
      formEl.action = 'https://checkout.wompi.co/p/'
      const campos = [
        ['public-key', res.publicKey],
        ['currency', res.currency],
        ['amount-in-cents', String(res.amountInCents)],
        ['reference', res.reference],
        ['signature:integrity', res.signature],
        ['redirect-url', res.redirectUrl || (window.location.origin + '/pago/resultado')],
        ['customer-data:email', f.email],
        ['customer-data:full-name', f.nombre],
        ['shipping-address:address-line-1', datos.direccion],
        ['shipping-address:country', 'CO'],
        ['shipping-address:city', datos.ciudad],
        ['shipping-address:region', datos.region],
        ['shipping-address:phone-number', normalizarTelefono(f.telefono)],
      ]
      for (const [name, val] of campos) {
        if (!val) continue
        const input = document.createElement('input')
        input.type = 'hidden'
        input.name = name
        input.value = val
        formEl.appendChild(input)
      }
      document.body.appendChild(formEl)
      formEl.submit()
    } else {
      window.location.href = '/pago/resultado?estado=APROBADO&ref=' + res.pedidoId
    }
  } catch (e) {
    store.errorMsg = e instanceof Error ? e.message : 'Error al procesar el pedido.'
  }
}

function cerrarYLimpiar() {
  store.cerrarOverlay()
  store.limpiarCarrito()
  store.modoCheckout = 'cart'
}
</script>

<style scoped>
.cart-slide-enter-active,
.cart-slide-leave-active {
  transition: opacity 0.3s ease;
}
.cart-slide-enter-active > div:last-child,
.cart-slide-leave-active > div:last-child {
  transition: transform 0.3s ease;
}
.cart-slide-enter-from,
.cart-slide-leave-to {
  opacity: 0;
}
.cart-slide-enter-from > div:last-child,
.cart-slide-leave-to > div:last-child {
  transform: translateX(100%);
}
</style>
