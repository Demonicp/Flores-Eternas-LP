<template>
  <div>
    <h2 class="text-xl font-serif text-text-primary mb-4">Gestiona las opciones personalizadas</h2>

    <!-- Selector -->
    <div class="mb-4">
      <label class="block text-sm text-text-primary font-medium mb-1">Seleccione qué desea gestionar</label>
      <select
        v-model="store.opcionSeleccionada"
        class="w-full max-w-xs rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
      >
        <option :value="null" disabled>Seleccione antes de visualizar</option>
        <option value="color">Color</option>
        <option value="flor">Flores</option>
      </select>
    </div>

    <!-- Modo Color -->
    <div v-if="store.opcionSeleccionada === 'color'">
      <div class="flex items-end gap-2 mb-1">
        <div class="flex-1 max-w-md">
          <label class="block text-sm text-text-primary font-medium mb-1">Nombre del color</label>
          <input
            v-model="store.colFormNombre"
            type="text"
            maxlength="50"
            :class="campoInvalidoColor('nombre')"
            placeholder="Nombre del color"
          />
        </div>
        <button
          type="button"
          @click="validarYGuardarColor"
          :disabled="store.colLoading"
          class="px-6 py-2 rounded-lg text-sm font-medium transition-colors"
          :class="store.colLoading ? 'opacity-50 cursor-not-allowed bg-gray-300' : 'bg-btn-primary text-btn-primary-text hover:opacity-90'"
        >
          {{ store.colLoading ? 'Guardando...' : store.esEdicionCol ? 'Guardar' : 'Añadir' }}
        </button>
        <button
          v-if="store.esEdicionCol"
          type="button"
          @click="cancelarEdicionColor"
          class="px-6 py-2 rounded-lg text-sm font-medium bg-gray-200 text-gray-600 hover:bg-gray-300"
        >
          Cancelar
        </button>
      </div>
      <ul v-if="erroresVisiblesCol.length > 0" class="space-y-1 mb-4">
        <li v-for="err in erroresVisiblesCol" :key="err" class="text-red-600 text-sm flex items-start gap-1.5">
          <span class="mt-0.5 shrink-0">•</span>
          <span>{{ err }}</span>
        </li>
      </ul>
      <p v-if="store.colError && erroresVisiblesCol.length === 0" class="text-red-600 text-sm mb-4">{{ store.colError }}</p>

      <hr class="my-6 border-border-soft" />

      <div>
        <div class="mb-4 max-w-xs">
          <input
            v-model="colorSearch"
            type="text"
            placeholder="Buscar color..."
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
          />
        </div>

        <div class="bg-bg-card rounded-xl overflow-hidden">
          <table v-if="filteredColores.length > 0" class="w-full text-sm text-text-primary">
            <thead>
              <tr class="text-left border-b border-border-soft bg-bg-card/80">
                <th class="p-3 font-medium">Nombre</th>
                <th class="p-3"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="color in filteredColores" :key="color.id" class="border-b border-border-soft/50 hover:bg-bg-input/50 transition-colors" :class="store.colEditandoId === color.id ? 'bg-btn-primary/50' : ''">
                <td class="p-3">{{ color.descripcionColor }}</td>
                <td class="p-3 flex gap-1">
                  <button
                    type="button"
                    @click="store.editarColor(color)"
                    class="px-2 py-1 text-sm text-text-primary hover:opacity-80"
                    title="Editar"
                  >
                    <Icon icon="mdi:pencil-outline" class="text-lg" />
                  </button>
                  <button
                    type="button"
                    @click="confirmarEliminarColor(color)"
                    class="px-2 py-1 text-sm text-red-500 hover:text-red-700"
                    title="Eliminar"
                  >
                    <Icon icon="mdi:delete-outline" class="text-lg" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="text-sm text-text-primary text-center py-8">
            {{ store.colores.length === 0 ? 'No hay colores añadidos aún.' : 'No se encontraron colores.' }}
          </p>
        </div>
      </div>
    </div>

    <!-- Modo Flores -->
    <div v-if="store.opcionSeleccionada === 'flor'">
      <div class="grid grid-cols-1 sm:grid-cols-4 gap-3">
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Nombre de la flor</label>
          <input
            v-model="store.florFormDescripcion"
            type="text"
            maxlength="100"
            :class="campoInvalidoFlor('nombre')"
            placeholder="Nombre de la flor"
          />
        </div>
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Precio unidad (COP)</label>
          <input
            v-model.number="store.florFormPrecio"
            type="number"
            min="0"
            @wheel.prevent
            :class="campoInvalidoFlor('precio')"
            placeholder="0"
          />
        </div>
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">% por mayor</label>
          <input
            v-model.number="store.florFormPorcentaje"
            type="number"
            min="0"
            max="100"
            @wheel.prevent
            :class="campoInvalidoFlor('porcentaje')"
            placeholder="0"
          />
        </div>
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Icono</label>
          <button
            type="button"
            @click="store.florFormIcono = null"
            class="text-xs text-text-primary/60 hover:text-text-primary mb-1"
            :class="!store.florFormIcono ? 'font-semibold text-btn-primary' : ''"
          >Sin icono</button>
          <div class="grid grid-cols-5 gap-1">
            <button
              v-for="ico in iconosDisponibles"
              :key="ico"
              type="button"
              @click="store.florFormIcono = ico"
              class="w-9 h-9 rounded-lg flex items-center justify-center transition-all"
              :class="store.florFormIcono === ico ? 'ring-2 ring-btn-primary bg-btn-primary/10' : 'hover:bg-bg-input border border-border-soft'"
            >
              <Icon :icon="ico" class="text-lg" :style="iconoStyleVivo" />
            </button>
          </div>
        </div>
      </div>

      <div class="flex items-end gap-3 mt-2">
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Color del icono</label>
          <button
            type="button"
            @click="store.florFormIconoColor = null"
            class="text-xs text-text-primary/60 hover:text-text-primary mb-1"
            :class="!store.florFormIconoColor ? 'font-semibold text-btn-primary' : ''"
          >Por defecto</button>
          <div class="flex flex-wrap gap-1.5">
            <button
              v-for="c in coloresPastel"
              :key="c"
              type="button"
              @click="store.florFormIconoColor = c"
              class="w-7 h-7 rounded-full transition-all"
              :style="{ backgroundColor: c }"
              :class="store.florFormIconoColor === c ? 'ring-2 ring-btn-primary ring-offset-1' : 'hover:scale-110'"
            />
          </div>
        </div>
        <div v-if="store.florFormIcono" class="flex items-center gap-2 pb-1">
          <Icon :icon="store.florFormIcono" class="text-3xl" :style="iconoStyleVivo" />
          <span class="text-xs text-text-primary/50">Vista previa</span>
        </div>
      </div>

      <div class="flex gap-2 mt-3">
        <button
          type="button"
          @click="validarYGuardarFlor"
          :disabled="store.florLoading"
          class="px-6 py-2 rounded-lg text-sm font-medium transition-colors"
          :class="store.florLoading ? 'opacity-50 cursor-not-allowed bg-gray-300' : 'bg-btn-primary text-btn-primary-text hover:opacity-90'"
        >
          {{ store.florLoading ? 'Guardando...' : store.esEdicionFlor ? 'Guardar' : 'Añadir' }}
        </button>
        <button
          v-if="store.esEdicionFlor"
          type="button"
          @click="cancelarEdicionFlor"
          class="px-6 py-2 rounded-lg text-sm font-medium bg-gray-200 text-gray-600 hover:bg-gray-300"
        >
          Cancelar
        </button>
      </div>

      <ul v-if="erroresVisiblesFlor.length > 0" class="space-y-1 mt-2">
        <li v-for="err in erroresVisiblesFlor" :key="err" class="text-red-600 text-sm flex items-start gap-1.5">
          <span class="mt-0.5 shrink-0">•</span>
          <span>{{ err }}</span>
        </li>
      </ul>
      <p v-if="store.florError && erroresVisiblesFlor.length === 0" class="text-red-600 text-sm mt-2">{{ store.florError }}</p>

      <hr class="my-6 border-border-soft" />

      <div>
        <div class="flex flex-col sm:flex-row gap-3 mb-4">
          <div class="flex-1">
            <input
              v-model="florSearch"
              type="text"
              placeholder="Buscar flor..."
              class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
            />
          </div>
          <div class="flex items-center gap-2 max-w-xs">
            <span class="text-sm text-text-primary/70">Ordenar:</span>
            <button
              type="button"
              @click="toggleOrdenPrecio"
              class="text-sm px-3 py-1.5 rounded-lg border border-border-soft bg-bg-input hover:border-btn-primary transition-colors flex items-center gap-1"
            >
              Precio
              <Icon v-if="ordenPrecio === 'asc'" icon="mdi:arrow-up" class="text-base" />
              <Icon v-else-if="ordenPrecio === 'desc'" icon="mdi:arrow-down" class="text-base" />
              <Icon v-else icon="mdi:swap-vertical" class="text-base text-text-primary/40" />
            </button>
          </div>
        </div>

        <div class="bg-bg-card rounded-xl overflow-hidden">
          <table v-if="filteredFlores.length > 0" class="w-full text-sm text-text-primary">
            <thead>
              <tr class="text-left border-b border-border-soft bg-bg-card/80">
                <th class="p-3 font-medium">Icono</th>
                <th class="p-3 font-medium">Nombre</th>
                <th class="p-3 font-medium cursor-pointer select-none" @click="toggleOrdenPrecio">
                  Precio ud.
                  <Icon v-if="ordenPrecio === 'asc'" icon="mdi:arrow-up" class="inline text-base align-text-bottom" />
                  <Icon v-else-if="ordenPrecio === 'desc'" icon="mdi:arrow-down" class="inline text-base align-text-bottom" />
                </th>
                <th class="p-3 font-medium">% Mayor</th>
                <th class="p-3"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="flor in filteredFlores" :key="flor.id" class="border-b border-border-soft/50 hover:bg-bg-input/50 transition-colors" :class="store.florEditandoId === flor.id ? 'bg-btn-primary/50' : ''">
                <td class="p-3">
                  <Icon :icon="flor.icono || 'mdi:flower-tulip-outline'" class="text-xl text-text-primary" />
                </td>
                <td class="p-3">{{ flor.descripcionFlor }}</td>
                <td class="p-3">${{ formatoPrecio(flor.precioUnidad) }}</td>
                <td class="p-3">{{ Number(flor.porcentajePorMayor).toFixed(1) }}%</td>
                <td class="p-3 flex gap-1">
                  <button
                    type="button"
                    @click="store.editarFlor(flor)"
                    class="px-2 py-1 text-sm text-text-primary hover:opacity-80"
                    title="Editar"
                  >
                    <Icon icon="mdi:pencil-outline" class="text-lg" />
                  </button>
                  <button
                    type="button"
                    @click="confirmarEliminarFlor(flor)"
                    class="px-2 py-1 text-sm text-red-500 hover:text-red-700"
                    title="Eliminar"
                  >
                    <Icon icon="mdi:delete-outline" class="text-lg" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="text-sm text-text-primary text-center py-8">
            {{ store.flores.length === 0 ? 'No hay flores añadidas aún.' : 'No se encontraron flores.' }}
          </p>
        </div>
      </div>
    </div>

    <p v-if="!store.opcionSeleccionada" class="text-sm text-text-primary text-center py-8">
      Seleccione antes de visualizar
    </p>

    <!-- Modal eliminar color -->
    <div
      v-if="modalEliminarColor"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
      @click.self="modalEliminarColor = null"
    >
      <div class="bg-white rounded-xl p-6 max-w-sm mx-4 shadow-xl">
        <template v-if="ramosConColor.length > 0">
          <div class="flex items-center gap-2 mb-3">
            <Icon icon="mdi:alert-circle-outline" class="text-xl text-amber-500" />
            <p class="font-medium text-text-primary">No se puede eliminar</p>
          </div>
          <p class="text-text-primary mb-4">
            El color <strong>{{ modalEliminarColor.descripcionColor }}</strong> está siendo usado en
            <strong>{{ ramosConColor.length }} ramo{{ ramosConColor.length !== 1 ? 's' : '' }}</strong>.
            Elimina o edita esos ramos primero.
          </p>
          <button
            type="button"
            @click="modalEliminarColor = null"
            class="px-4 py-2 rounded-lg text-sm bg-btn-primary text-btn-primary-text hover:opacity-90"
          >
            Entendido
          </button>
        </template>
        <template v-else>
          <p class="text-text-primary mb-4">
            ¿Estás seguro de que deseas eliminar el color <strong>{{ modalEliminarColor.descripcionColor }}</strong>?
          </p>
          <div class="flex justify-end gap-2">
            <button
              type="button"
              @click="modalEliminarColor = null"
              class="px-4 py-2 rounded-lg text-sm bg-gray-200 text-gray-600 hover:bg-gray-300"
            >
              Cancelar
            </button>
            <button
              type="button"
              @click="ejecutarEliminarColor"
              class="px-4 py-2 rounded-lg text-sm bg-red-500 text-white hover:bg-red-600"
            >
              Eliminar
            </button>
          </div>
        </template>
      </div>
    </div>

    <!-- Modal eliminar flor -->
    <div
      v-if="modalEliminarFlor"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
      @click.self="modalEliminarFlor = null"
    >
      <div class="bg-white rounded-xl p-6 max-w-sm mx-4 shadow-xl">
        <template v-if="ramosConFlor.length > 0">
          <div class="flex items-center gap-2 mb-3">
            <Icon icon="mdi:alert-circle-outline" class="text-xl text-amber-500" />
            <p class="font-medium text-text-primary">No se puede eliminar</p>
          </div>
          <p class="text-text-primary mb-4">
            La flor <strong>{{ modalEliminarFlor.descripcionFlor }}</strong> está siendo usada en
            <strong>{{ ramosConFlor.length }} ramo{{ ramosConFlor.length !== 1 ? 's' : '' }}</strong>.
            Elimina o edita esos ramos primero.
          </p>
          <button
            type="button"
            @click="modalEliminarFlor = null"
            class="px-4 py-2 rounded-lg text-sm bg-btn-primary text-btn-primary-text hover:opacity-90"
          >
            Entendido
          </button>
        </template>
        <template v-else>
          <p class="text-text-primary mb-4">
            ¿Estás seguro de que deseas eliminar la flor <strong>{{ modalEliminarFlor.descripcionFlor }}</strong>?
          </p>
          <div class="flex justify-end gap-2">
            <button
              type="button"
              @click="modalEliminarFlor = null"
              class="px-4 py-2 rounded-lg text-sm bg-gray-200 text-gray-600 hover:bg-gray-300"
            >
              Cancelar
            </button>
            <button
              type="button"
              @click="ejecutarEliminarFlor"
              class="px-4 py-2 rounded-lg text-sm bg-red-500 text-white hover:bg-red-600"
            >
              Eliminar
            </button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useNegocioStore } from '../../stores/negocio.store'
import type { ColorFlor } from '../../models/color-flor.model'
import type { TipoFlor } from '../../models/tipo-flor.model'
import { useToast } from '~/composables/useToast'
import { formatoPrecio } from '~/utils/formatters'

const store = useNegocioStore()
const toast = useToast()

/* ========= Color ========= */
const colorSearch = ref('')
const modalEliminarColor = ref<ColorFlor | null>(null)
const erroresVisiblesCol = ref<string[]>([])

const camposInvalidosCol = computed(() => ({
  nombre: !store.colFormNombre.trim()
}))

const erroresValidacionCol = computed(() => {
  const e: string[] = []
  if (camposInvalidosCol.value.nombre) e.push('Falta el nombre del color')
  return e
})

function campoInvalidoColor(nombre: string) {
  return camposInvalidosCol.value[nombre] && erroresVisiblesCol.value.length > 0
    ? 'w-full rounded-lg border border-red-400 bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-red-400'
    : 'w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary'
}

watch(() => store.colEditandoId, () => { erroresVisiblesCol.value = [] })

const filteredColores = computed(() => {
  let items = store.colores
  if (colorSearch.value.trim()) {
    const q = colorSearch.value.trim().toLowerCase()
    items = items.filter(c => c.descripcionColor.toLowerCase().includes(q))
  }
  return items
})

const ramosConColor = computed(() => {
  if (!modalEliminarColor.value) return []
  const id = modalEliminarColor.value.id
  return store.ramos.filter(r => r.disponible !== false && r.detallesRamo.some(d => d.colorFlor.id === id))
})

function cancelarEdicionColor() {
  store.resetColForm()
  erroresVisiblesCol.value = []
}

function validarYGuardarColor() {
  erroresVisiblesCol.value = erroresValidacionCol.value
  if (erroresVisiblesCol.value.length > 0) return
  guardarColor()
}

async function guardarColor() {
  const esEdicion = store.esEdicionCol
  await store.guardarColor()
  if (!store.colError) {
    toast.success(esEdicion ? 'Color actualizado con éxito' : 'Color creado con éxito')
    erroresVisiblesCol.value = []
  }
}

function confirmarEliminarColor(color: ColorFlor) {
  modalEliminarColor.value = color
}

async function ejecutarEliminarColor() {
  if (modalEliminarColor.value) {
    await store.eliminarColor(modalEliminarColor.value.id)
    if (!store.colError) {
      toast.success('Color eliminado con éxito')
    }
    modalEliminarColor.value = null
  }
}

/* ========= Flor ========= */
const iconosDisponibles = [
  'mdi:flower-tulip',
  'mdi:flower-tulip-outline',
  'mdi:nature',
  'mdi:nature-outline',
  'mdi:flower',
  'mdi:flower-outline',
  'mdi:flower-poppy',
  'mdi:flower-lotus',
  'mdi:flower-lotus-outline',
  'mdi:sprout',
  'mdi:sprout-outline',
  'mdi:leaf',
  'mdi:leaf-maple',
  'mdi:plant',
  'mdi:plant-outline',
  'mdi:cactus',
  'mdi:grass',
  'mdi:flower-pollen',
  'mdi:flower-pollen-outline',
]

const coloresPastel = [
  '#FF0000', '#FF4500', '#FF8C00', '#FFD700',
  '#ADFF2F', '#00FF00', '#00CED1', '#0000FF',
  '#4B0082', '#8B008B', '#FF1493', '#FF69B4',
  '#FFA07A', '#F0E68C', '#98FB98', '#87CEEB',
  '#DDA0DD', '#FFB6C1', '#C0C0C0', '#808080',
  '#8B4513', '#2F4F4F', '#191970', '#800000',
]

const iconoStyleVivo = computed(() => {
  if (store.florFormIconoColor) {
    return { color: store.florFormIconoColor }
  }
  return {}
})

const florSearch = ref('')
type OrdenPrecio = 'asc' | 'desc' | null
const ordenPrecio = ref<OrdenPrecio>(null)
const modalEliminarFlor = ref<TipoFlor | null>(null)
const erroresVisiblesFlor = ref<string[]>([])

const camposInvalidosFlor = computed(() => ({
  nombre: !store.florFormDescripcion.trim(),
  precio: store.florFormPrecio === null || store.florFormPrecio <= 0,
  porcentaje: store.florFormPorcentaje === null || store.florFormPorcentaje < 0 || store.florFormPorcentaje > 100
}))

const erroresValidacionFlor = computed(() => {
  const e: string[] = []
  if (camposInvalidosFlor.value.nombre) e.push('Falta el nombre de la flor')
  if (camposInvalidosFlor.value.precio) e.push('El precio debe ser mayor a 0')
  if (camposInvalidosFlor.value.porcentaje) e.push('El porcentaje debe estar entre 0 y 100')
  return e
})

function campoInvalidoFlor(nombre: string) {
  return camposInvalidosFlor.value[nombre] && erroresVisiblesFlor.value.length > 0
    ? 'w-full rounded-lg border border-red-400 bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-red-400'
    : 'w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary'
}

watch(() => store.florEditandoId, () => { erroresVisiblesFlor.value = [] })

function toggleOrdenPrecio() {
  if (ordenPrecio.value === null) ordenPrecio.value = 'asc'
  else if (ordenPrecio.value === 'asc') ordenPrecio.value = 'desc'
  else ordenPrecio.value = null
}

const filteredFlores = computed(() => {
  let items = store.flores
  if (florSearch.value.trim()) {
    const q = florSearch.value.trim().toLowerCase()
    items = items.filter(f => f.descripcionFlor.toLowerCase().includes(q))
  }
  if (ordenPrecio.value === 'asc') {
    items = [...items].sort((a, b) => a.precioUnidad - b.precioUnidad)
  } else if (ordenPrecio.value === 'desc') {
    items = [...items].sort((a, b) => b.precioUnidad - a.precioUnidad)
  }
  return items
})

const ramosConFlor = computed(() => {
  if (!modalEliminarFlor.value) return []
  const id = modalEliminarFlor.value.id
  return store.ramos.filter(r => r.disponible !== false && r.detallesRamo.some(d => d.tipoFlor.id === id))
})

function cancelarEdicionFlor() {
  store.resetFlorForm()
  erroresVisiblesFlor.value = []
}

function validarYGuardarFlor() {
  erroresVisiblesFlor.value = erroresValidacionFlor.value
  if (erroresVisiblesFlor.value.length > 0) return
  guardarFlor()
}

async function guardarFlor() {
  const esEdicion = store.esEdicionFlor
  await store.guardarFlor()
  if (!store.florError) {
    toast.success(esEdicion ? 'Flor actualizada con éxito' : 'Flor creada con éxito')
    erroresVisiblesFlor.value = []
  }
}

function confirmarEliminarFlor(flor: TipoFlor) {
  modalEliminarFlor.value = flor
}

async function ejecutarEliminarFlor() {
  if (modalEliminarFlor.value) {
    await store.eliminarFlor(modalEliminarFlor.value.id)
    if (!store.florError) {
      toast.success('Flor eliminada con éxito')
    }
    modalEliminarFlor.value = null
  }
}
</script>
