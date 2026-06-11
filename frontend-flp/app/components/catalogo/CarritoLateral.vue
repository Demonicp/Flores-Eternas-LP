<template>
  <Teleport to="body">
    <Transition name="cart-slide">
      <div
        v-if="store.abierto"
        class="fixed inset-0 z-50 flex justify-end"
      >
        <div
          class="absolute inset-0 bg-black/30"
          @click="store.cerrarCarrito()"
        />

        <div class="relative w-full max-w-md bg-white h-full flex flex-col shadow-2xl">
          <div class="flex items-center justify-between px-5 py-4 border-b border-gray-100">
            <h2 class="font-serif text-lg text-text-primary font-medium">
              {{ titulo }}
            </h2>
            <button
              class="text-text-primary/60 hover:text-text-primary p-1"
              @click="store.cerrarCarrito()"
            >
              <Icon icon="mdi:close" class="text-xl" />
            </button>
          </div>

          <div v-if="modo === 'confirm'" class="flex-1 overflow-y-auto px-5 py-6">
            <div class="flex flex-col items-center text-center gap-4 py-8">
              <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center">
                <Icon icon="mdi:check-circle-outline" class="text-3xl text-green-600" />
              </div>
              <p class="text-text-primary font-medium text-lg">
                {{ store.respuestaPedido?.mensaje }}
              </p>
              <div class="w-full text-left space-y-2 text-sm text-text-primary/80 mt-4 bg-bg-card rounded-xl p-4">
                <p><strong>Pedido #{{ store.respuestaPedido?.id }}</strong></p>
                <p>Total: ${{ formatoPrecio(store.respuestaPedido?.total ?? 0) }} COP</p>
                <p>Pagado: ${{ formatoPrecio(store.respuestaPedido?.montoPagado ?? 0) }} COP</p>
                <p v-if="(store.respuestaPedido?.montoPendiente ?? 0) > 0">
                  Pendiente: ${{ formatoPrecio(store.respuestaPedido?.montoPendiente ?? 0) }} COP
                </p>
                <p>Entrega: {{ store.respuestaPedido?.fechaEntrega }}</p>
              </div>
            </div>
            <button
              class="w-full mt-6 bg-btn-primary text-btn-primary-text px-4 py-3 rounded-full text-sm font-medium hover:opacity-90 transition-opacity"
              @click="cerrarYLimpiar"
            >
              Seguir comprando
            </button>
          </div>

          <template v-else-if="store.items.length === 0">
            <div class="flex-1 flex flex-col items-center justify-center text-text-primary/50 gap-3 px-5">
              <Icon icon="mdi:cart-outline" class="text-5xl" />
              <p class="text-sm">Tu carrito está vacío</p>
            </div>
          </template>

          <template v-else-if="modo === 'cart'">
            <div class="flex-1 overflow-y-auto px-5 py-4 space-y-4">
              <div
                v-for="item in store.items"
                :key="item.ramo.id"
                class="flex gap-3 items-center"
              >
                <div class="w-16 h-16 rounded-lg bg-bg-card flex items-center justify-center overflow-hidden flex-shrink-0">
                  <img
                    v-if="item.ramo.foto"
                    :src="item.ramo.foto"
                    :alt="item.ramo.nombre"
                    class="w-full h-full object-cover"
                  />
                  <Icon v-else icon="mdi:flower-tulip-outline" class="text-2xl text-text-primary/30" />
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-text-primary truncate">
                    {{ item.ramo.nombre }}
                  </p>
                  <p class="text-xs text-text-primary/60">
                    ${{ formatoPrecio(item.ramo.precio) }} COP c/u
                  </p>
                </div>
                <div class="flex items-center gap-2">
                  <button
                    class="w-7 h-7 rounded-full border border-gray-200 flex items-center justify-center text-text-primary/60 hover:border-gray-400 transition-colors"
                    @click="store.cambiarCantidad(item.ramo.id, -1)"
                  >
                    <Icon icon="mdi:minus" class="text-sm" />
                  </button>
                  <span class="w-6 text-center text-sm font-medium">{{ item.cantidad }}</span>
                  <button
                    class="w-7 h-7 rounded-full border border-gray-200 flex items-center justify-center text-text-primary/60 hover:border-gray-400 transition-colors"
                    @click="store.cambiarCantidad(item.ramo.id, 1)"
                  >
                    <Icon icon="mdi:plus" class="text-sm" />
                  </button>
                </div>
                <button
                  class="text-text-primary/40 hover:text-red-500 transition-colors p-1"
                  @click="store.eliminarItem(item.ramo.id)"
                >
                  <Icon icon="mdi:delete-outline" class="text-lg" />
                </button>
              </div>
            </div>

            <div class="border-t border-gray-100 px-5 py-4 space-y-3">
              <div class="flex justify-between text-sm font-medium text-text-primary">
                <span>Subtotal</span>
                <span>${{ formatoPrecio(store.subtotal) }} COP</span>
              </div>
              <button
                class="w-full bg-btn-primary text-btn-primary-text px-4 py-3 rounded-full text-sm font-medium hover:opacity-90 transition-opacity"
                @click="modo = 'checkout'"
              >
                Continuar
              </button>
            </div>
          </template>

          <template v-else>
            <div class="flex-1 overflow-y-auto px-5 py-4 space-y-4">
              <div class="text-sm text-text-primary/80 space-y-1">
                <p class="font-medium text-text-primary">Resumen del pedido</p>
                <p>{{ store.totalItems }} producto(s) — ${{ formatoPrecio(store.subtotal) }} COP</p>
              </div>

              <div class="space-y-3">
                <div>
                  <label class="text-xs text-text-primary/60 block mb-1">Nombre completo</label>
                  <input
                    v-model="form.nombre"
                    type="text"
                    class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-btn-primary"
                  />
                </div>
                <div>
                  <label class="text-xs text-text-primary/60 block mb-1">Correo electrónico</label>
                  <input
                    v-model="form.email"
                    type="email"
                    class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-btn-primary"
                  />
                </div>
                <div>
                  <label class="text-xs text-text-primary/60 block mb-1">Dirección de entrega</label>
                  <input
                    v-model="form.direccion"
                    type="text"
                    class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-btn-primary"
                  />
                </div>
                <div>
                  <label class="text-xs text-text-primary/60 block mb-1">Fecha de entrega</label>
                  <input
                    v-model="form.fechaEntrega"
                    type="date"
                    :min="fechaMinima"
                    class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-btn-primary"
                  />
                  <p class="text-xs text-text-primary/40 mt-1">Mínimo 5 días a partir de hoy</p>
                </div>
                <p v-if="errorMsg" class="text-xs text-red-500">{{ errorMsg }}</p>
              </div>
            </div>

            <div class="border-t border-gray-100 px-5 py-4 space-y-2">
              <button
                class="w-full bg-btn-primary text-btn-primary-text px-4 py-3 rounded-full text-sm font-medium hover:opacity-90 transition-opacity disabled:opacity-50"
                :disabled="store.cargando"
                @click="handleRealizarPedido"
              >
                {{ store.cargando ? 'Procesando...' : 'Realizar Pedido' }}
              </button>
              <button
                class="w-full text-sm text-text-primary/60 hover:text-text-primary transition-colors py-1"
                @click="modo = 'cart'"
              >
                Volver al carrito
              </button>
            </div>
          </template>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useCartStore } from '../../stores/cart.store'

const store = useCartStore()

type Modo = 'cart' | 'checkout' | 'confirm'
const modo = ref<Modo>('cart')

const form = ref({
  nombre: '',
  email: '',
  direccion: '',
  fechaEntrega: '',
})

const errorMsg = ref('')

const titulo = computed(() => {
  if (modo.value === 'confirm') return 'Pedido Confirmado'
  if (modo.value === 'checkout') return 'Datos de Entrega'
  return 'Tu Carrito'
})

const fechaMinima = computed(() => {
  const d = new Date()
  d.setDate(d.getDate() + 5)
  return d.toISOString().split('T')[0]
})

function formatoPrecio(valor: number): string {
  return new Intl.NumberFormat('es-CO', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(valor)
}

function cerrarYLimpiar() {
  store.cerrarCarrito()
  store.limpiarCarrito()
  modo.value = 'cart'
}

async function handleRealizarPedido() {
  errorMsg.value = ''
  if (!form.value.nombre || !form.value.email || !form.value.direccion || !form.value.fechaEntrega) {
    errorMsg.value = 'Todos los campos son obligatorios.'
    return
  }

  try {
    await store.realizarPedido({
      nombreCliente: form.value.nombre,
      emailCliente: form.value.email,
      direccionEntrega: form.value.direccion,
      fechaEntrega: form.value.fechaEntrega,
      tipoPedido: 'RAPIDO',
      tipoPago: 'COMPLETO',
      items: store.items.map(i => ({
        idRamo: i.ramo.id,
        cantidad: i.cantidad,
      })),
    })
    modo.value = 'confirm'
  } catch (e) {
    errorMsg.value = e instanceof Error ? e.message : 'Error al procesar el pedido.'
  }
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
