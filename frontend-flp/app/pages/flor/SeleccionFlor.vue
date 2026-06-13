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

      <main class="flex flex-col items-center mt-16">
        <h2 class="text-[#7A4E2D] text-2xl font-serif mb-10">Selecciona el tipo de flor</h2>

        <div v-if="loading" class="text-[#7A4E2D] text-lg">Cargando flores...</div>

        <div v-else class="grid grid-cols-4 gap-10 mb-16">
          <div
            v-for="flor in tiposFlor"
            :key="flor.id"
            @click="seleccionar(flor)"
            class="w-64 h-96 rounded-xl shadow-md transition-all cursor-pointer flex flex-col items-center justify-center gap-4"
            :class="store.tipoFlor?.id === flor.id ? 'bg-[#FFDCC8] ring-2 ring-[#7A4E2D] scale-105' : 'bg-[#FFEDE3] hover:scale-105'"
          >
            <div class="text-6xl">🌸</div>
            <span class="text-[#7A4E2D] text-xl font-serif">{{ flor.descripcionFlor }}</span>
            <span class="text-[#7A4E2D] text-sm">${{ flor.precioUnidad?.toFixed(2) }} c/u</span>
          </div>
        </div>

        <button
          @click="irSiguiente"
          :disabled="!store.tipoFlor"
          class="px-10 py-3 rounded-full font-serif transition text-lg"
          :class="store.tipoFlor ? 'bg-[#FFEDE3] text-[#7A4E2D] hover:bg-[#FFDCC8]' : 'bg-gray-200 text-gray-400 cursor-not-allowed'"
        >
          Siguiente →
        </button>
        <p v-if="!store.tipoFlor" class="text-red-500 text-sm mt-2">Selecciona un tipo de flor para continuar</p>
      </main>

      <footer class="absolute bottom-6 right-10 text-[#7A4E2D] text-sm flex items-center gap-1">
        <span>¿Necesitas ayuda?</span>
        <Icon name="ph:question" size="18" />
      </footer>
    </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'
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

function seleccionar(flor) {
  store.seleccionarTipoFlor(flor)
}

function irSiguiente() {
  if (store.tipoFlor) {
    router.push('/flor/seleccion-apartados')
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display&display=swap');
</style>
