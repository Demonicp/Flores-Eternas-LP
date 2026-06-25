<template>
    <h2 class="text-[#7A4E2D] text-2xl font-radley mb-10">Selecciona los tipos de flor</h2>

    <div v-if="loading" class="flex gap-5 px-6">
      <div v-for="i in 4" :key="i" class="flex-shrink-0 w-52 h-64 rounded-xl bg-[#FFEDE3] animate-pulse" />
    </div>

    <div v-else class="relative group mb-16">
      <button
        class="absolute -left-8 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-white/90 shadow-md flex items-center justify-center text-[#7A4E2D] opacity-0 group-hover:opacity-100 transition-opacity duration-300 hover:bg-white"
        :class="{ 'opacity-100': scrollLeft > 0 }"
        @click="scrollSection(-1)"
        aria-label="Anterior"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
      </button>

      <div
        ref="scrollContainer"
        class="flex gap-5 overflow-x-auto scroll-smooth pt-4 pb-2 scrollbar-hide px-6"
        @scroll="onScroll"
      >
        <div
          v-for="flor in tiposFlor"
          :key="flor.id"
          @click="store.toggleFlor(flor)"
          class="flex-shrink-0 w-52 rounded-xl shadow-md transition-all cursor-pointer flex flex-col items-center justify-center gap-3 p-6"
          :class="store.isSelected(flor.id) ? 'bg-[#FFDCC8] ring-2 ring-[#7A4E2D]' : 'bg-[#FFEDE3] hover:bg-[#FFE8DD]'"
        >
          <div v-if="store.isSelected(flor.id)" class="w-8 h-8 rounded-full bg-[#7A4E2D] text-white flex items-center justify-center text-sm font-bold">✓</div>
          <div v-else class="w-8 h-8 rounded-full border-2 border-[#7A4E2D]/30"></div>
          <Icon :icon="flor.icono || 'mdi:flower-tulip-outline'" class="text-4xl" :style="flor.iconoColor ? { color: flor.iconoColor } : {}" />
          <span class="text-[#7A4E2D] text-lg font-radley">{{ flor.descripcionFlor }}</span>
          <span class="text-[#7A4E2D] text-sm font-lora">${{ flor.precioUnidad?.toFixed(2) }} c/u</span>
        </div>
      </div>

      <button
        class="absolute -right-8 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-white/90 shadow-md flex items-center justify-center text-[#7A4E2D] opacity-0 group-hover:opacity-100 transition-opacity duration-300 hover:bg-white"
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
    <p v-if="store.floresSeleccionadas.length === 0" class="text-red-500 text-sm mt-2 text-center font-radley italic">Selecciona al menos un tipo de flor para continuar</p>
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
  const cardWidth = 224
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
</style>
