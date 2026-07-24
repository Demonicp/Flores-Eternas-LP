<template>
    <h2 class="text-[#7A4E2D] text-2xl font-radley mb-10">Selecciona los tipos de flor</h2>

    <div v-if="loading" class="flex gap-4 sm:gap-5 px-2 sm:px-6">
      <div v-for="i in 4" :key="i" class="flex-shrink-0 w-44 sm:w-52 h-64 rounded-xl bg-[#FFEDE3] animate-pulse" />
    </div>

    <div v-else class="relative group mb-16 px-2 sm:px-0">
      <button
        class="arrow-btn absolute -left-2 sm:-left-8 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-white/90 shadow-md flex items-center justify-center text-[#7A4E2D] opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-white active:scale-90"
        :class="{ 'opacity-100': scrollLeft > 0 }"
        @click="scrollSection(-1)"
        aria-label="Anterior"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
      </button>

      <div
        ref="scrollContainer"
        class="flex gap-4 sm:gap-5 overflow-x-auto scroll-smooth pt-4 pb-2 scrollbar-hide snap-x snap-mandatory"
        @scroll="onScroll"
      >
        <div
          v-for="flor in tiposFlor"
          :key="flor.id"
          :class="['flex-shrink-0 w-44 sm:w-52 rounded-xl shadow-md transition-all duration-200 flex flex-col items-center justify-center gap-2 sm:gap-3 p-4 sm:p-6 snap-start', store.isSelected(flor.id) ? 'bg-[#FFDCC8] ring-2 ring-[#7A4E2D]' : store.floresSeleccionadas.length >= 2 ? 'bg-[#FFEDE3] opacity-50 cursor-not-allowed' : 'bg-[#FFEDE3] hover:bg-[#FFE8DD] cursor-pointer']"
          @click="store.floresSeleccionadas.length < 2 || store.isSelected(flor.id) ? store.toggleFlor(flor) : undefined"
        >
          <div v-if="store.isSelected(flor.id)" class="w-8 h-8 rounded-full bg-[#7A4E2D] text-white flex items-center justify-center text-sm font-bold">✓</div>
          <div v-else class="w-8 h-8 rounded-full border-2 border-[#7A4E2D]/30"></div>
          <Icon :icon="flor.icono || 'mdi:flower-tulip-outline'" class="text-3xl sm:text-4xl" :style="flor.iconoColor ? { color: flor.iconoColor } : {}" />
          <span class="text-[#7A4E2D] text-base sm:text-lg font-radley text-center">{{ flor.descripcionFlor }}</span>
          <span class="text-[#7A4E2D] text-xs sm:text-sm font-lora">${{ flor.precioUnidad?.toFixed(2) }} c/u</span>
        </div>
      </div>

      <button
        class="arrow-btn absolute -right-2 sm:-right-8 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-white/90 shadow-md flex items-center justify-center text-[#7A4E2D] opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-white active:scale-90"
        @click="scrollSection(1)"
        aria-label="Siguiente"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
      </button>
    </div>

    <div class="flex items-center gap-4 justify-center">
      <button
        @click="irSiguiente"
        :disabled="store.floresSeleccionadas.length === 0"
        class="px-10 py-3 rounded-full font-serif transition text-lg"
        :class="store.floresSeleccionadas.length > 0 ? 'bg-[#FFEDE3] text-[#7A4E2D] hover:bg-[#FFDCC8]' : 'bg-gray-200 text-gray-400 cursor-not-allowed'"
      >
        Siguiente →
      </button>
    </div>
    <p v-if="store.floresSeleccionadas.length === 0" class="text-red-500 text-sm mt-2 text-center font-radley italic">Máximo 2 tipos de flor. Selecciona al menos uno para continuar</p>
    <p v-if="store.floresSeleccionadas.length === 2" class="text-amber-600 text-sm mt-2 text-center font-radley italic">Has alcanzado el máximo de 2 tipos de flor</p>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'flor' })
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { floresApi } from '~/services/api-client'

const store = useRamoPersonalizadoStore()
const router = useRouter()

const tiposFlor = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    tiposFlor.value = await floresApi.getTipos()
  } catch (e) {
    console.error('Error al cargar tipos de flor:', e)
  } finally {
    loading.value = false
  }
})

const scrollContainer = ref<HTMLElement | null>(null)
const scrollLeft = ref(0)

function scrollSection(direction: number) {
  const el = scrollContainer.value
  if (!el) return
  const card = el.querySelector('.snap-start') as HTMLElement | null
  const cardWidth = card ? card.offsetWidth + 16 : 208
  el.scrollBy({ left: direction * cardWidth, behavior: 'smooth' })
}

function onScroll() {
  const el = scrollContainer.value
  if (el) {
    scrollLeft.value = el.scrollLeft
  }
}

function irSiguiente() {
  if (store.floresSeleccionadas.length > 0) {
    router.push('/flor/seleccion-apartados')
  }
}
</script>

<style scoped>
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.snap-x {
  scroll-snap-type: x mandatory;
}
.snap-start {
  scroll-snap-align: start;
}
.arrow-btn {
  -webkit-tap-highlight-color: transparent;
}
</style>
