<template>
    <h2 class="text-[#7A4E2D] text-2xl font-radley mb-8">Adiciones para tu ramo</h2>

    <div class="flex gap-8 w-full">
      <div class="w-64 bg-white rounded-2xl shadow-lg p-6 flex-shrink-0 h-fit">
        <h3 class="text-[#7A4E2D] font-radley text-lg mb-4">Resumen de flores</h3>
        <div v-for="(item, idx) in store.floresSeleccionadas" :key="idx"
          class="flex items-center justify-between py-2 border-b border-[#FFEDE3] last:border-0"
        >
          <div class="flex items-center gap-2">
            <span class="text-lg">🌸</span>
            <span class="text-[#7A4E2D] text-sm font-radley">{{ item.tipoFlor.descripcionFlor }}</span>
          </div>
          <span class="text-[#7A4E2D] text-sm">×{{ item.cantidad }}</span>
        </div>
        <div class="flex justify-between pt-3 text-[#7A4E2D] font-lora">
          <span>Subtotal</span>
          <span>${{ subtotalFlores.toFixed(2) }}</span>
        </div>
      </div>

        <div v-if="loading" class="flex-1 space-y-4 py-10">
          <div v-for="i in 3" :key="i" class="h-16 bg-[#FFEDE3] animate-pulse rounded-xl" />
        </div>

      <div v-else class="bg-white rounded-2xl shadow-lg p-8 flex-1">
        <div v-if="adicionesDisponibles.length === 0" class="text-center text-gray-400 text-lg py-8">
          No hay adiciones disponibles en este momento
        </div>

        <div v-for="item in adicionesDisponibles" :key="item.id"
          class="flex items-center justify-between py-4 border-b border-[#FFEDE3] last:border-0"
        >
          <div class="flex-1">
            <span class="text-[#7A4E2D] font-radley text-lg">{{ item.nombre }}</span>
            <p class="text-gray-500 text-sm">{{ item.descripcion }}</p>
            <span class="text-[#7A4E2D] text-sm font-lora">${{ item.precioCosto?.toFixed(2) }}</span>
          </div>

          <div class="flex items-center gap-3">
            <button
              @click="cambiarCantidad(item, -1)"
              class="w-8 h-8 rounded-full bg-[#FFEDE3] text-[#7A4E2D] font-bold hover:bg-[#FFDCC8]"
            >−</button>
            <span class="text-[#7A4E2D] font-semibold text-lg w-8 text-center">
              {{ obtenerCantidad(item) }}
            </span>
            <button
              @click="cambiarCantidad(item, 1)"
              class="w-8 h-8 rounded-full bg-[#FFEDE3] text-[#7A4E2D] font-bold hover:bg-[#FFDCC8]"
            >+</button>
          </div>
        </div>

      </div>
    </div>

    <div class="flex gap-4 justify-center mt-8">
      <button
        @click="cancelar"
        class="bg-white text-[#7A4E2D] font-radley px-8 py-3 rounded-full border-2 border-[#FFEDE3] hover:bg-[#FFEDE3] transition"
      >
        Cancelar
      </button>
      <button
        @click="guardar"
        class="bg-[#7A4E2D] text-white font-radley px-8 py-3 rounded-full hover:bg-[#5E3A1F] transition"
      >
        Guardar
      </button>
    </div>
</template>

<script setup>
definePageMeta({ layout: 'flor' })
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { floresApi } from '~/services/api-client'

const store = useRamoPersonalizadoStore()
const router = useRouter()

if (store.floresSeleccionadas.length === 0) {
  router.replace('/flor/SeleccionFlor')
}

const adicionesDisponibles = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    adicionesDisponibles.value = await floresApi.getAdiciones()
  } catch (e) {
    console.error('Error al cargar adiciones:', e)
  } finally {
    loading.value = false
  }
})

function obtenerCantidad(item) {
  const existente = store.adiciones.find(a => a.id === item.id)
  return existente ? existente.cantidad : 0
}

function cambiarCantidad(item, delta) {
  const existente = store.adiciones.find(a => a.id === item.id)
  if (existente) {
    const nueva = existente.cantidad + delta
    if (nueva <= 0) {
      store.quitarAdicion(item.id)
    } else {
      existente.cantidad = nueva
    }
  } else if (delta > 0) {
    store.agregarAdicion(item, 1)
  }
}

const subtotalFlores = computed(() =>
  store.floresSeleccionadas.reduce((sum, f) => sum + (f.tipoFlor.precioUnidad || 0) * f.cantidad, 0)
)

function cancelar() {
  store.adiciones = []
  router.push('/flor/seleccion-apartados')
}

function guardar() {
  router.push('/flor/seleccion-apartados')
}
</script>

<style scoped>
</style>
