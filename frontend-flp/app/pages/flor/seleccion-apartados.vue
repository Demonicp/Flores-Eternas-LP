<template>
  <div class="min-h-screen bg-[#FFFAF5] flex flex-col items-center">
    <nav class="w-full flex justify-between items-center py-5 px-16 bg-[#FFEDE3] text-[#7A4E2D] font-medium">
      <div class="flex gap-10">
        <a href="#" class="hover:underline">Entrega inmediata</a>
        <a href="#" class="font-semibold underline">Personalizado</a>
      </div>
      <div class="text-center font-serif text-2xl tracking-wide">
        <span class="font-bold">LP</span> FLORES ETERNAS
      </div>
      <div class="flex gap-6 items-center">
        <button><Icon name="ph:user" size="26" /></button>
        <button><Icon name="ph:shopping-cart" size="26" /></button>
      </div>
    </nav>

    <main class="flex flex-col items-center mt-10 w-full max-w-5xl px-8">
      <h2 class="text-[#7A4E2D] text-2xl font-serif mb-8">
        Configura tu ramo de {{ store.tipoFlor?.descripcion }}
      </h2>

      <div class="flex gap-8 w-full">
        <div
          class="w-56 h-72 rounded-xl shadow-md flex flex-col items-center justify-center gap-3 flex-shrink-0 bg-[#FFEDE3]"
        >
          <div class="text-5xl">🌸</div>
          <span class="text-[#7A4E2D] text-lg font-serif">{{ store.tipoFlor?.descripcion }}</span>
          <span class="text-[#7A4E2D] text-sm">${{ store.tipoFlor?.precioUnidad?.toFixed(2) }} c/u</span>
        </div>

        <div class="bg-white rounded-2xl shadow-lg p-8 flex-1">
          <div class="flex gap-8 items-end">
            <div class="flex-1">
              <label class="block text-[#7A4E2D] font-serif mb-2 text-lg">Cantidad</label>
              <input
                type="number"
                min="1"
                v-model.number="cantidadLocal"
                @input="store.setCantidad(cantidadLocal)"
                placeholder="Ej: 5"
                class="w-full border-2 border-[#FFEDE3] rounded-lg px-4 py-3 text-lg text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]"
              />
            </div>

            <div class="flex-1">
              <label class="block text-[#7A4E2D] font-serif mb-2 text-lg">Color</label>
              <select
                v-model="colorSeleccionado"
                @change="store.seleccionarColor(colorSeleccionado)"
                class="w-full border-2 border-[#FFEDE3] rounded-lg px-4 py-3 text-lg text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D] bg-white"
              >
                <option :value="null" disabled>Selecciona un color</option>
                <option v-for="color in colores" :key="color.id" :value="color">
                  {{ color.descripcion }}
                </option>
              </select>
            </div>
          </div>

          <div class="flex gap-6 justify-center mt-10">
            <button
              @click="volverSeleccion"
              class="bg-white text-[#7A4E2D] font-serif px-6 py-3 rounded-full border-2 border-[#FFEDE3] hover:bg-[#FFEDE3] transition"
            >
              ← Volver a seleccionar flor
            </button>
            <button
              @click="irAdiciones"
              class="bg-[#FFEDE3] text-[#7A4E2D] font-serif px-8 py-3 rounded-full hover:bg-[#FFDCC8] transition"
            >
              Adiciones
            </button>
            <button
              @click="irResumen"
              :disabled="!colorSeleccionado || cantidadLocal < 1"
              class="font-serif px-8 py-3 rounded-full transition"
              :class="colorSeleccionado && cantidadLocal >= 1 ? 'bg-[#7A4E2D] text-white hover:bg-[#5E3A1F]' : 'bg-gray-200 text-gray-400 cursor-not-allowed'"
            >
              Siguiente →
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

const config = useRuntimeConfig()
const store = useRamoPersonalizadoStore()
const router = useRouter()

if (!store.tipoFlor) {
  router.replace('/flor/SeleccionFlor')
}

const colores = ref([])
const colorSeleccionado = ref(store.colorFlor)
const cantidadLocal = ref(store.cantidad)

onMounted(async () => {
  try {
    const base = config.public.apiBase || 'http://localhost:8080'
    const res = await fetch(`${base}/api/flores/colores`)
    colores.value = await res.json()
  } catch (e) {
    console.error('Error al cargar colores:', e)
  }
})

function volverSeleccion() {
  store.resetear()
  router.push('/flor/SeleccionFlor')
}

function irAdiciones() {
  store.setCantidad(cantidadLocal.value)
  if (colorSeleccionado.value) {
    store.seleccionarColor(colorSeleccionado.value)
  }
  router.push('/flor/adiciones')
}

function irResumen() {
  store.setCantidad(cantidadLocal.value)
  store.seleccionarColor(colorSeleccionado.value)
  router.push('/flor/resumen-pedido')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display&display=swap');
</style>
