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
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
            placeholder="Nombre del color"
          />
        </div>
        <button
          type="button"
          @click="guardarColor"
          :disabled="!store.colFormNombre.trim() || store.colLoading"
          class="px-6 py-2 rounded-lg text-sm font-medium transition-colors"
          :class="store.colLoading ? 'opacity-50 cursor-not-allowed bg-gray-300' : 'bg-btn-primary text-btn-primary-text hover:opacity-90'"
        >
          {{ store.colLoading ? 'Guardando...' : store.esEdicionCol ? 'Aceptar' : 'Añadir' }}
        </button>
        <button
          v-if="store.esEdicionCol"
          type="button"
          @click="store.resetColForm()"
          class="px-6 py-2 rounded-lg text-sm font-medium bg-gray-200 text-gray-600 hover:bg-gray-300"
        >
          Cancelar
        </button>
      </div>
      <p v-if="store.colError" class="text-red-600 text-sm mb-4">{{ store.colError }}</p>

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
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Nombre de la flor</label>
          <input
            v-model="store.florFormDescripcion"
            type="text"
            maxlength="100"
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
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
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
            placeholder="0"
          />
        </div>
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Porcentaje por mayor</label>
          <input
            v-model.number="store.florFormPorcentaje"
            type="number"
            min="0"
            max="100"
            @wheel.prevent
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
            placeholder="0"
          />
        </div>
      </div>

      <div class="flex gap-2 mt-3">
        <button
          type="button"
          @click="guardarFlor"
          :disabled="!florFormValido || store.florLoading"
          class="px-6 py-2 rounded-lg text-sm font-medium transition-colors"
          :class="store.florLoading ? 'opacity-50 cursor-not-allowed bg-gray-300' : 'bg-btn-primary text-btn-primary-text hover:opacity-90'"
        >
          {{ store.florLoading ? 'Guardando...' : store.esEdicionFlor ? 'Aceptar' : 'Añadir' }}
        </button>
        <button
          v-if="store.esEdicionFlor"
          type="button"
          @click="store.resetFlorForm()"
          class="px-6 py-2 rounded-lg text-sm font-medium bg-gray-200 text-gray-600 hover:bg-gray-300"
        >
          Cancelar
        </button>
      </div>

      <p v-if="store.florError" class="text-red-600 text-sm mt-2">{{ store.florError }}</p>

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
import { ref, computed } from 'vue'
import { useNegocioStore } from '../../stores/negocio.store'
import type { ColorFlor } from '../../models/color-flor.model'
import type { TipoFlor } from '../../models/tipo-flor.model'
import { useToast } from '~/composables/useToast'
import { formatoPrecio } from '~/utils/formatters'

const store = useNegocioStore()
const toast = useToast()

/* Color */
const colorSearch = ref('')
const modalEliminarColor = ref<ColorFlor | null>(null)

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
  return store.ramos.filter(r => r.detallesRamo.some(d => d.colorFlor.id === id))
})

async function guardarColor() {
  const esEdicion = store.esEdicionCol
  await store.guardarColor()
  if (!store.colError) {
    toast.success(esEdicion ? 'Color actualizado con éxito' : 'Color creado con éxito')
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

/* Flor */
const florSearch = ref('')
type OrdenPrecio = 'asc' | 'desc' | null
const ordenPrecio = ref<OrdenPrecio>(null)
const modalEliminarFlor = ref<TipoFlor | null>(null)

function toggleOrdenPrecio() {
  if (ordenPrecio.value === null) ordenPrecio.value = 'asc'
  else if (ordenPrecio.value === 'asc') ordenPrecio.value = 'desc'
  else ordenPrecio.value = null
}

const florFormValido = computed(() =>
  store.florFormDescripcion.trim()
  && store.florFormPrecio !== null && store.florFormPrecio > 0
  && store.florFormPorcentaje !== null && store.florFormPorcentaje >= 0 && store.florFormPorcentaje <= 100
)

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
  return store.ramos.filter(r => r.detallesRamo.some(d => d.tipoFlor.id === id))
})

async function guardarFlor() {
  const esEdicion = store.esEdicionFlor
  await store.guardarFlor()
  if (!store.florError) {
    toast.success(esEdicion ? 'Flor actualizada con éxito' : 'Flor creada con éxito')
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
