<template>
  <Teleport to="body">
    <Transition name="detail-fade">
      <div v-if="abierto" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="cerrar">
        <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" />
        <div class="relative z-10 w-full max-w-3xl max-h-[90vh] bg-white rounded-3xl shadow-2xl overflow-hidden flex flex-col md:flex-row">
          <!-- Close button -->
          <button class="absolute top-3 right-3 z-20 w-9 h-9 rounded-full bg-white/80 backdrop-blur flex items-center justify-center text-text-primary/60 hover:text-text-primary shadow-sm transition-colors" @click="cerrar">
            <Icon icon="mdi:close" class="text-xl" />
          </button>

          <!-- Loading -->
          <div v-if="cargando" class="flex-1 flex items-center justify-center py-20">
            <Icon icon="mdi:loading" class="text-4xl text-btn-primary animate-spin" />
          </div>

          <!-- Error -->
          <div v-else-if="error" class="flex-1 flex flex-col items-center justify-center py-16 text-text-primary/50 gap-3">
            <Icon icon="mdi:alert-circle-outline" class="text-5xl" />
            <p class="text-sm">{{ error }}</p>
            <button class="text-sm text-btn-primary font-medium hover:underline" @click="cerrar">Cerrar</button>
          </div>

          <!-- Content -->
          <template v-if="producto">
            <!-- Image -->
            <div class="w-full md:w-1/2 bg-bg-card flex items-center justify-center overflow-hidden md:max-h-[90vh]">
              <img
                v-if="producto.fotoRamo && !imgError"
                :src="producto.fotoRamo"
                :alt="producto.nombreRamo"
                class="w-full h-full object-cover"
                @error="imgError = true"
              />
              <div v-else class="flex flex-col items-center justify-center py-20 text-text-primary/30">
                <Icon icon="mdi:flower-tulip-outline" class="text-7xl" />
                <p class="text-sm mt-2">Sin imagen</p>
              </div>
              <div v-if="!producto.disponible" class="absolute top-4 left-4 bg-red-100 text-red-700 text-xs font-medium px-3 py-1 rounded-full">
                Agotado
              </div>
            </div>

            <!-- Info -->
            <div class="w-full md:w-1/2 flex flex-col overflow-y-auto">
              <div class="flex-1 p-6 md:p-8 space-y-5">
                <!-- Categoria -->
                <p v-if="producto.categoria" class="text-xs uppercase tracking-wider text-text-primary/50 font-medium">
                  {{ producto.categoria.descripcionCategoriaRamo }}
                </p>

                <!-- Nombre -->
                <h2 class="font-serif text-2xl md:text-3xl text-text-primary font-medium leading-tight">
                  {{ producto.nombreRamo }}
                </h2>

                <!-- Precio -->
                <p class="text-2xl font-bold text-text-primary">
                  ${{ formatoPrecio(producto.precioRamo) }} <span class="text-sm font-normal text-text-primary/50">COP</span>
                </p>

                <!-- Descripción -->
                <div v-if="producto.descripcionRamo" class="text-[15px] text-text-primary/70 leading-relaxed">
                  {{ producto.descripcionRamo }}
                </div>

                <!-- Composición -->
                <div v-if="producto.detallesRamo && producto.detallesRamo.length > 0">
                  <p class="text-sm font-medium text-text-primary mb-2 flex items-center gap-1.5">
                    <Icon icon="mdi:flower-outline" class="text-base text-text-primary/50" />
                    Composición
                  </p>
                  <div class="flex flex-wrap gap-2">
                    <span
                      v-for="det in producto.detallesRamo"
                      :key="det.id"
                      class="inline-flex items-center gap-1.5 bg-bg-card/60 px-3 py-1.5 rounded-full text-xs text-text-primary/70"
                    >
                      <span class="w-2.5 h-2.5 rounded-full" :class="colorDotClass(det.colorFlor?.descripcionColor)" />
                      {{ det.cantidad }}x {{ det.tipoFlor?.descripcionFlor }}
                    </span>
                  </div>
                </div>

                <!-- Stock -->
                <p v-if="producto.stock != null" class="text-xs text-text-primary/40">
                  {{ producto.stock > 0 ? `${producto.stock} en stock` : 'Sin stock' }}
                </p>
              </div>

              <!-- Add to cart -->
              <div class="border-t border-border-soft/60 px-6 md:px-8 py-4">
                <button
                  class="w-full flex items-center justify-center gap-2.5 bg-btn-primary text-btn-primary-text px-5 py-3 rounded-xl text-[15px] font-semibold hover:opacity-90 transition-all duration-200 disabled:opacity-40 disabled:cursor-not-allowed shadow-sm hover:shadow-md active:scale-[0.98]"
                  :disabled="!producto.disponible"
                  @click="agregarAlCarrito"
                >
                  <Icon icon="mdi:cart-outline" class="text-lg" />
                  {{ producto.disponible ? 'Añadir al carrito' : 'No disponible' }}
                </button>
              </div>
            </div>
          </template>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ramoService } from '../../services/ramo.service'
import { useCartStore } from '../../stores/cart.store'
import type { RamoResponse } from '../../models/ramo.model'

const props = defineProps<{
  productoId: number | null
}>()

const emit = defineEmits<{
  close: []
}>()

const store = useCartStore()
const abierto = ref(false)
const cargando = ref(false)
const error = ref('')
const producto = ref<RamoResponse | null>(null)
const imgError = ref(false)

watch(() => props.productoId, async (id) => {
  if (id == null) {
    abierto.value = false
    return
  }
  abierto.value = true
  cargando.value = true
  error.value = ''
  producto.value = null
  imgError.value = false
  try {
    producto.value = await ramoService.obtener(id)
  } catch (e) {
    error.value = 'Error al cargar el producto.'
  } finally {
    cargando.value = false
  }
})

function cerrar() {
  abierto.value = false
  emit('close')
}

function agregarAlCarrito() {
  if (!producto.value) return
  store.agregarItem({
    id: producto.value.id,
    nombre: producto.value.nombreRamo,
    foto: producto.value.fotoRamo,
    precio: producto.value.precioRamo,
  })
  cerrar()
}

function formatoPrecio(valor: number): string {
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(valor)
}

const colorPalette: Record<string, string> = {
  rojo: 'bg-red-400',
  azul: 'bg-blue-400',
  verde: 'bg-green-400',
  amarillo: 'bg-yellow-400',
  blanco: 'bg-gray-100 border border-gray-300',
  rosado: 'bg-pink-400',
  morado: 'bg-purple-400',
  naranja: 'bg-orange-400',
  cafe: 'bg-amber-700',
  negro: 'bg-gray-800',
}

function colorDotClass(color?: string): string {
  if (!color) return 'bg-gray-300'
  const c = color.toLowerCase()
  for (const [key, cls] of Object.entries(colorPalette)) {
    if (c.includes(key)) return cls
  }
  return 'bg-gray-300'
}
</script>

<style scoped>
.detail-fade-enter-active,
.detail-fade-leave-active {
  transition: opacity 0.25s ease;
}
.detail-fade-enter-from,
.detail-fade-leave-to {
  opacity: 0;
}
.detail-fade-enter-active > div:last-child,
.detail-fade-leave-active > div:last-child {
  transition: transform 0.25s ease, opacity 0.25s ease;
}
.detail-fade-enter-from > div:last-child,
.detail-fade-leave-to > div:last-child {
  transform: scale(0.95);
  opacity: 0;
}
</style>
