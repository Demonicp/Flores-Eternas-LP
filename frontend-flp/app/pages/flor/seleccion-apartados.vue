<template>
    <div class="w-full">
      <h2 class="text-[#7A4E2D] text-2xl font-radley mb-2">Configura tu ramo</h2>
      <p class="text-[#7A4E2D] text-sm mb-8 font-radley italic">{{ store.totalFlores }} flor(es) seleccionadas</p>

      <div class="space-y-6">
        <div v-for="grupo in gruposFlores" :key="grupo.tipoFlor.id" class="bg-white rounded-2xl shadow-lg p-6">
          <div class="flex items-center gap-3 mb-4">
            <div class="text-3xl">🌸</div>
            <div>
              <h3 class="text-[#7A4E2D] text-lg font-radley">{{ grupo.tipoFlor.descripcionFlor }}</h3>
              <p class="text-[#7A4E2D] text-sm font-lora">${{ grupo.tipoFlor.precioUnidad?.toFixed(2) }} c/u</p>
            </div>
          </div>

          <div class="space-y-4">
            <div
              v-for="(item, idx) in grupo.variantes"
              :key="grupo.tipoFlor.id + '-' + idx"
              class="flex items-center gap-4 p-3 rounded-xl bg-[#FFFAF5]"
            >
              <div class="flex-1">
                <label class="block text-xs text-gray-500 mb-1">Color</label>
                <select
                  :value="item.colorFlor?.id ?? ''"
                  @change="onColorChange(item, $event)"
                  class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D] bg-white"
                >
                  <option value="" disabled selected>Selecciona un color</option>
                  <option v-for="color in colores" :key="color.id" :value="color.id">
                    {{ color.descripcionColor }}
                  </option>
                </select>
              </div>

              <div class="w-24">
                <label class="block text-xs text-gray-500 mb-1">Cantidad</label>
                <input
                  type="number"
                  min="1"
                  :value="item.cantidad"
                  @input="onCantidadChange(item, $event)"
                  class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]"
                />
              </div>

              <button
                @click="store.quitarVariante(store.floresSeleccionadas.indexOf(item))"
                class="w-9 h-9 rounded-full bg-[#FFEDE3] text-[#7A4E2D] hover:bg-[#FFDCC8] transition flex items-center justify-center flex-shrink-0"
                title="Eliminar"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2" />
                </svg>
              </button>
            </div>
          </div>

          <button
            @click="store.agregarVariante(grupo.tipoFlor)"
            class="mt-3 text-sm text-[#7A4E2D] hover:text-[#5E3A1F] font-radley underline underline-offset-2"
          >
            + Añadir más {{ grupo.tipoFlor.descripcionFlor.toLowerCase() }}
          </button>
        </div>
      </div>

      <div class="flex flex-wrap gap-4 justify-center mt-10">
        <button
          @click="volverSeleccion"
          class="bg-white text-[#7A4E2D] font-radley px-6 py-3 rounded-full border-2 border-[#FFEDE3] hover:bg-[#FFEDE3] transition"
        >
          ← Volver a seleccionar flores
        </button>
        <button
          @click="irAdiciones"
          class="bg-[#FFEDE3] text-[#7A4E2D] font-radley px-8 py-3 rounded-full hover:bg-[#FFDCC8] transition"
        >
          Adiciones
        </button>
        <button
          @click="irResumen"
          :disabled="!puedeContinuar"
          class="font-radley px-8 py-3 rounded-full transition"
          :class="puedeContinuar ? 'bg-[#7A4E2D] text-white hover:bg-[#5E3A1F]' : 'bg-gray-200 text-gray-400 cursor-not-allowed'"
        >
          Siguiente →
        </button>
      </div>
      <p v-if="!puedeContinuar && store.floresSeleccionadas.length > 0" class="text-red-500 text-sm text-center mt-2">
        Asigna cantidad y color a cada flor para continuar
      </p>
    </div>
</template>

<script setup>
definePageMeta({ layout: 'flor' })
import { computed } from 'vue'
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { floresApi } from '~/services/api-client'

const store = useRamoPersonalizadoStore()
const router = useRouter()

if (store.floresSeleccionadas.length === 0) {
  router.replace('/flor/SeleccionFlor')
}

const colores = ref([])

onMounted(async () => {
  try {
    colores.value = await floresApi.getColores()
  } catch (e) {
    console.error('Error al cargar colores:', e)
  }
})

const gruposFlores = computed(() => {
  const map = new Map()
  for (const item of store.floresSeleccionadas) {
    const id = item.tipoFlor.id
    if (!map.has(id)) {
      map.set(id, { tipoFlor: item.tipoFlor, variantes: [] })
    }
    map.get(id).variantes.push(item)
  }
  return Array.from(map.values())
})

const puedeContinuar = computed(() =>
  store.floresSeleccionadas.length > 0 && store.floresSeleccionadas.every(f => f.cantidad >= 1 && f.colorFlor)
)

function volverSeleccion() {
  store.resetear()
  router.push('/flor/SeleccionFlor')
}

function irAdiciones() {
  router.push('/flor/adiciones')
}

function onColorChange(item, e) {
  const val = e.target.value
  const color = val === '' ? null : colores.value.find(c => c.id === Number(val))
  store.setColor(store.floresSeleccionadas.indexOf(item), color || null)
}

function onCantidadChange(item, e) {
  store.setCantidad(store.floresSeleccionadas.indexOf(item), Number(e.target.value))
}

function irResumen() {
  if (puedeContinuar.value) {
    router.push('/flor/resumen-pedido')
  }
}
</script>

<style scoped>
</style>
