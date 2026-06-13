<template>
  <div class="min-h-screen bg-[#FFFAF5] flex flex-col items-center">
    <nav class="w-full flex justify-between items-center py-5 px-16 bg-[#FFEDE3] text-[#7A4E2D] font-medium">
      <div class="flex gap-10">
        <a href="#" class="hover:underline">Entrega inmediata</a>
        <a href="#" class="font-semibold underline">Personalizado</a>
      </div>
      <NuxtLink to="/"><img src="/assets/images/flplogoblack.png" alt="Flores Eternas LP" class="h-14 w-auto cursor-pointer" /></NuxtLink>
      <div class="flex gap-6 items-center">
        <button><Icon name="ph:user" size="26" /></button>
        <button><Icon name="ph:shopping-cart" size="26" /></button>
      </div>
    </nav>

    <main class="flex flex-col items-center mt-10 w-full max-w-5xl px-8">
      <h2 class="text-[#7A4E2D] text-2xl font-serif mb-8">Adiciones para tu ramo</h2>

      <div class="flex gap-8 w-full">
        <div
          class="w-56 h-72 rounded-xl shadow-md flex flex-col items-center justify-center gap-3 flex-shrink-0 bg-[#FFEDE3]"
        >
          <div class="text-5xl">🌸</div>
          <span class="text-[#7A4E2D] text-lg font-serif">{{ store.tipoFlor?.descripcionFlor }}</span>
          <span class="text-[#7A4E2D] text-sm">${{ store.tipoFlor?.precioUnidad?.toFixed(2) }} c/u</span>
        </div>

        <div v-if="loading" class="flex-1 text-[#7A4E2D] text-lg text-center py-20">Cargando adiciones...</div>

        <div v-else class="bg-white rounded-2xl shadow-lg p-8 flex-1">
          <div v-if="adicionesDisponibles.length === 0" class="text-center text-gray-400 text-lg py-8">
            No hay adiciones disponibles en este momento
          </div>

          <div v-for="item in adicionesDisponibles" :key="item.id"
            class="flex items-center justify-between py-4 border-b border-[#FFEDE3] last:border-0"
          >
            <div class="flex-1">
              <span class="text-[#7A4E2D] font-serif text-lg">{{ item.nombre }}</span>
              <p class="text-gray-500 text-sm">{{ item.descripcion }}</p>
              <span class="text-[#7A4E2D] text-sm">${{ item.precioCosto?.toFixed(2) }}</span>
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

          <div class="flex justify-center mt-8">
            <button
              @click="router.push('/flor/seleccion-apartados')"
              class="bg-[#FFEDE3] text-[#7A4E2D] font-serif px-8 py-3 rounded-full hover:bg-[#FFDCC8] transition"
            >
              Volver
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { floresApi } from '~/services/api-client'

const store = useRamoPersonalizadoStore()
const router = useRouter()

if (!store.tipoFlor) {
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
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display&display=swap');
</style>
