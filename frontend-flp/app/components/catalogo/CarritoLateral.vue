<template>
  <div v-if="esSidebar" class="w-full lg:w-80 bg-white border-l border-border-soft flex flex-col h-full">
    <div class="flex items-center justify-between px-4 py-3 border-b border-border-soft">
      <h2 class="font-serif text-base text-text-primary font-medium">{{ titulo }}</h2>
      <button class="text-text-primary/60 hover:text-text-primary p-1 lg:hidden" @click="store.cerrarOverlay()">
        <Icon icon="mdi:close" class="text-xl" />
      </button>
    </div>
    <ContCartInterno @realizar-pedido="handleRealizarPedido" @cerrar-y-limpiar="cerrarYLimpiar" />
  </div>

  <Teleport v-if="esOverlay" to="body">
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
import { computed } from 'vue'
import { useCartStore } from '../../stores/cart.store'
import ContCartInterno from './ContCartInterno.vue'

const store = useCartStore()

const titulo = computed(() => {
  if (store.modoCheckout === 'confirm') return 'Pedido Confirmado'
  if (store.modoCheckout === 'checkout') return 'Datos de Entrega'
  return 'Tu Carrito'
})

const esSidebar = computed(() => store.vista === 'sidebar')
const esOverlay = computed(() => store.vista === 'overlay')

async function handleRealizarPedido() {
  store.errorMsg = ''
  const f = store.checkoutForm
  if (!f.nombre || !f.email || !f.direccion || !f.fechaEntrega) {
    store.errorMsg = 'Todos los campos son obligatorios.'
    return
  }
  if (store.tienePersonalizados && (!f.cedula || !f.telefono)) {
    store.errorMsg = 'Cédula y teléfono son obligatorios para pedidos personalizados.'
    return
  }

  try {
    await store.realizarPedido({
      nombreCliente: f.nombre,
      emailCliente: f.email,
      direccionEntrega: f.direccion,
      fechaEntrega: f.fechaEntrega,
      tipoPedido: 'RAPIDO',
      tipoPago: 'COMPLETO',
      items: store.items.map(i => ({
        idRamo: i.ramo.id,
        cantidad: i.cantidad,
      })),
    })
    store.modoCheckout = 'confirm'
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
