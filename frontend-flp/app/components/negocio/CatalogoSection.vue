<template>
  <div>
    <h2 class="text-xl font-serif text-text-primary mb-4">Gestiona tu catálogo</h2>

    <!-- Formulario -->
    <div class="space-y-4">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div class="space-y-4">
          <div
            class="flex flex-col items-center justify-center border-2 border-dashed border-border-soft rounded-xl p-6 bg-bg-input cursor-pointer hover:border-btn-primary transition-colors"
            @click="triggerUpload"
          >
            <template v-if="store.ramoFormFoto">
              <img :src="store.ramoFormFoto" alt="Preview" class="max-h-40 rounded-lg object-contain" />
            </template>
            <template v-else>
              <Icon icon="mdi:cloud-upload-outline" class="text-3xl text-text-primary mb-2" />
              <p class="text-sm text-text-primary">Ingresa La Imagen De Tu Ramo</p>
            </template>
            <input ref="fileInput" type="file" accept=".png,.jpg,.jpeg" class="hidden" @change="onFileSelected" />
          </div>

          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">Nombre</label>
            <input
              v-model="store.ramoFormNombre"
              type="text"
              maxlength="150"
              :class="campoInvalido('nombre')"
              placeholder="Nombre del ramo"
            />
          </div>

          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">Elige Su Categoría</label>
            <select
              v-model="store.ramoFormIdCategoria"
              :class="campoInvalido('categoria')"
            >
              <option :value="null" disabled>Seleccione...</option>
              <option v-for="cat in store.categoriasDisponiblesVisibles" :key="cat.id" :value="cat.id">
                {{ cat.descripcionCategoriaRamo }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">Precio De Tu Ramo (COP)</label>
            <input
              v-model.number="store.ramoFormPrecio"
              type="number"
              min="0"
              @wheel.prevent
              :class="campoInvalido('precio')"
              placeholder="0"
            />
          </div>
        </div>

        <div class="space-y-4">
          <div :class="camposInvalidos.detalles && erroresVisibles.length > 0 ? 'rounded-lg border border-red-400 p-3' : ''">
            <label class="block text-sm text-text-primary font-medium mb-1">Flores del ramo</label>
            <LineaDetalle
              v-for="(linea, idx) in store.ramoFormDetalles"
              :key="idx"
              v-model="store.ramoFormDetalles[idx]"
              :tipos-flor="store.tiposFlorDisponibles"
              :colores="store.coloresDisponibles"
              @eliminar="store.eliminarLineaDetalle(idx)"
            />
            <button
              type="button"
              @click="store.agregarLineaDetalle()"
              class="text-sm text-btn-primary-text underline hover:opacity-80"
            >
              + Añadir flor
            </button>
          </div>

          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">
              Stock
              <span class="text-xs text-text-primary/60 font-normal">(dejar vacío = sin control)</span>
            </label>
            <input
              type="number"
              min="0"
              @wheel.prevent
              :value="store.ramoFormStock"
              @input="store.ramoFormStock = ($event.target as HTMLInputElement).value ? Number(($event.target as HTMLInputElement).value) : null"
              class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
              placeholder="Sin control"
            />
          </div>

          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">Descripción De Tu Ramo</label>
            <textarea
              v-model="store.ramoFormDescripcion"
              rows="3"
              maxlength="500"
              class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary resize-none"
              placeholder="Descripción del ramo"
            />
          </div>

          <div class="flex items-center gap-3">
            <label class="block text-sm text-text-primary font-medium">Activo</label>
            <button
              type="button"
              @click="store.ramoFormDisponible = !store.ramoFormDisponible"
              class="relative w-11 h-6 rounded-full transition-colors duration-200"
              :class="store.ramoFormDisponible ? 'bg-btn-primary' : 'bg-gray-300'"
            >
              <span
                class="absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow-sm transition-transform duration-200"
                :class="store.ramoFormDisponible ? 'translate-x-5' : ''"
              />
            </button>
            <span class="text-xs text-text-primary/60">
              {{ store.ramoFormDisponible ? 'Visible en catálogo' : 'Oculto en catálogo' }}
            </span>
          </div>

          <div class="flex gap-2">
            <button
              type="button"
              @click="validarYGuardar"
              :disabled="store.ramoLoading"
              class="px-6 py-2 rounded-lg text-sm font-medium transition-colors"
              :class="store.ramoLoading ? 'opacity-50 cursor-not-allowed bg-gray-300' : 'bg-btn-primary text-btn-primary-text hover:opacity-90'"
            >
              {{ store.ramoLoading ? 'Guardando...' : store.esEdicionRamo ? 'Guardar' : 'Aceptar' }}
            </button>
            <button
              v-if="store.esEdicionRamo"
              type="button"
              @click="cancelarEdicion"
              class="px-6 py-2 rounded-lg text-sm font-medium bg-gray-200 text-gray-600 hover:bg-gray-300 transition-colors"
            >
              Cancelar
            </button>
          </div>

          <ul v-if="erroresVisibles.length > 0" class="space-y-1">
            <li v-for="err in erroresVisibles" :key="err" class="text-red-600 text-sm flex items-start gap-1.5">
              <span class="mt-0.5 shrink-0">•</span>
              <span>{{ err }}</span>
            </li>
          </ul>
          <p v-if="store.ramoError && erroresVisibles.length === 0" class="text-red-600 text-sm">{{ store.ramoError }}</p>
        </div>
      </div>
    </div>

    <!-- Separador -->
    <hr class="my-6 border-border-soft" />

    <!-- Filtros + Tabla -->
    <div>
      <div class="flex flex-col sm:flex-row gap-3 mb-4">
        <div class="flex-1">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar por nombre..."
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
          />
        </div>
        <div class="w-full sm:w-56">
          <select
            v-model="filterCategoria"
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
          >
            <option value="">Todas las categorías</option>
            <option v-for="cat in store.categoriasDisponiblesVisibles" :key="cat.id" :value="cat.id">
              {{ cat.descripcionCategoriaRamo }}
            </option>
          </select>
        </div>
        <button
          type="button"
          @click="vistaLista = !vistaLista"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium bg-text-primary text-white hover:opacity-90 transition"
          :title="vistaLista ? 'Cambiar a cuadrícula' : 'Cambiar a lista'"
        >
          <Icon :icon="vistaLista ? 'mdi:view-grid-outline' : 'mdi:view-agenda-outline'" class="text-base" />
          {{ vistaLista ? 'Cuadrícula' : 'Lista' }}
        </button>
      </div>

      <div class="bg-bg-card rounded-xl overflow-hidden">
        <div v-if="filteredRamos.length > 0 && vistaLista" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3 p-3">
          <div
            v-for="ramo in filteredRamos"
            :key="ramo.id"
            class="bg-white border border-border-soft rounded-xl p-3 shadow-sm flex flex-col gap-2"
            :class="store.ramoEditandoId === ramo.id ? 'bg-btn-primary/50' : ''"
          >
            <p class="text-sm font-semibold text-text-primary truncate" :title="ramo.nombreRamo">{{ ramo.nombreRamo }}</p>
            <p class="text-xs text-text-primary/70">{{ ramo.categoria?.descripcionCategoriaRamo || '—' }}</p>
            <div class="text-xs text-text-primary/80 space-y-0.5">
              <div class="flex justify-between"><span class="text-text-primary/60">Precio</span><span class="font-medium">${{ formatoPrecio(ramo.precioRamo) }}</span></div>
              <div class="flex justify-between">
                <span class="text-text-primary/60">Stock</span>
                <span><span v-if="ramo.stock != null">{{ ramo.stock }}</span><span v-else class="text-text-primary/40">∞</span></span>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <span
                v-if="ramo.stock != null && ramo.stock <= 0"
                class="inline-block px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-700"
              >Agotado</span>
              <span
                v-else-if="ramo.disponible === false"
                class="inline-block px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-500"
              >Inactivo</span>
              <span
                v-else
                class="inline-block px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700"
              >Activo</span>
              <span class="flex gap-1">
                <button
                  type="button"
                  @click="store.editarRamo(ramo)"
                  class="px-2 py-1 text-sm text-text-primary hover:opacity-80"
                  title="Editar"
                >
                  <Icon icon="mdi:pencil-outline" class="text-lg" />
                </button>
                <button
                  type="button"
                  @click="confirmarEliminar(ramo)"
                  class="px-2 py-1 text-sm text-[#A52A2A] hover:text-[#7C1D1B]"
                  title="Eliminar"
                >
                  <Icon icon="mdi:delete-outline" class="text-lg" />
                </button>
              </span>
            </div>
          </div>
        </div>
        <div v-else-if="filteredRamos.length > 0" class="flex flex-col gap-2 p-3">
          <div
            v-for="ramo in filteredRamos"
            :key="ramo.id"
            class="bg-white border border-border-soft rounded-xl px-3 py-2 shadow-sm flex items-center gap-3"
            :class="store.ramoEditandoId === ramo.id ? 'bg-btn-primary/50' : ''"
          >
            <span class="text-sm font-medium text-text-primary flex-1 truncate min-w-0" :title="ramo.nombreRamo">{{ ramo.nombreRamo }}</span>
            <span class="text-xs text-text-primary/70 flex-shrink-0 hidden md:inline">{{ ramo.categoria?.descripcionCategoriaRamo || '—' }}</span>
            <span class="text-xs text-text-primary/80 flex-shrink-0">${{ formatoPrecio(ramo.precioRamo) }}</span>
            <span class="text-xs text-text-primary/60 flex-shrink-0 hidden sm:inline"><span v-if="ramo.stock != null">{{ ramo.stock }}</span><span v-else>∞</span></span>
            <span
              v-if="ramo.stock != null && ramo.stock <= 0"
              class="inline-block px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-700 flex-shrink-0"
            >Agotado</span>
            <span
              v-else-if="ramo.disponible === false"
              class="inline-block px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-500 flex-shrink-0"
            >Inactivo</span>
            <span
              v-else
              class="inline-block px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700 flex-shrink-0"
            >Activo</span>
            <span class="flex gap-1 flex-shrink-0">
              <button
                type="button"
                @click="store.editarRamo(ramo)"
                class="px-2 py-1 text-sm text-text-primary hover:opacity-80"
                title="Editar"
              >
                <Icon icon="mdi:pencil-outline" class="text-lg" />
              </button>
              <button
                type="button"
                @click="confirmarEliminar(ramo)"
                class="px-2 py-1 text-sm text-[#A52A2A] hover:text-[#7C1D1B]"
                title="Eliminar"
              >
                <Icon icon="mdi:delete-outline" class="text-lg" />
              </button>
            </span>
          </div>
        </div>
        <p v-else class="text-sm text-text-primary text-center py-8">
          {{ store.ramosVisibles.length === 0 ? 'No hay ramos publicados aún.' : 'No se encontraron ramos con esos filtros.' }}
        </p>
      </div>
    </div>

    <!-- Modal eliminar -->
    <div
      v-if="modalEliminar"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
      @click.self="modalEliminar = null"
    >
      <div class="bg-white rounded-xl p-6 max-w-sm mx-4 shadow-xl">
        <p class="text-text-primary mb-4">¿Estás seguro de que deseas eliminar este ramo?</p>
        <div class="flex justify-end gap-2">
          <button
            type="button"
            @click="modalEliminar = null"
            class="px-4 py-2 rounded-lg text-sm bg-gray-200 text-gray-600 hover:bg-gray-300"
          >
            Cancelar
          </button>
          <button
            type="button"
            @click="ejecutarEliminar"
            class="px-4 py-2 rounded-lg text-sm bg-red-500 text-white hover:bg-red-600"
          >
            Eliminar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useNegocioStore } from '../../stores/negocio.store'
import type { RamoResponse } from '../../models/ramo.model'
import LineaDetalle from '~/components/negocio/LineaDetalle.vue'
import { useToast } from '~/composables/useToast'
import { formatoPrecio } from '~/utils/formatters'

const store = useNegocioStore()
const toast = useToast()
const fileInput = ref<HTMLInputElement | null>(null)
const modalEliminar = ref<RamoResponse | null>(null)

watch(() => store.ramoEditandoId, () => {
  erroresVisibles.value = []
})

const searchQuery = ref('')
const filterCategoria = ref('')
const vistaLista = ref(false)

const filteredRamos = computed(() => {
  let items = store.ramosVisibles
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    items = items.filter(r => r.nombreRamo.toLowerCase().includes(q))
  }
  if (filterCategoria.value) {
    const catId = Number(filterCategoria.value)
    items = items.filter(r => r.categoria?.id === catId)
  }
  return items
})

const erroresVisibles = ref<string[]>([])

const camposInvalidos = computed(() => {
  const map: Record<string, boolean> = {}
  if (!store.ramoFormNombre.trim()) map.nombre = true
  if (store.ramoFormPrecio === null || store.ramoFormPrecio <= 0) map.precio = true
  if (store.ramoFormIdCategoria === null) map.categoria = true
  if (!store.ramoFormDetalles.some(d => d.idTipoFlor && d.idColorFlor && d.cantidad > 0)) map.detalles = true
  return map
})

const erroresValidacion = computed(() => {
  const errores: string[] = []
  if (camposInvalidos.value.nombre) errores.push('Falta el nombre del ramo')
  if (camposInvalidos.value.precio) errores.push('El precio debe ser mayor a 0')
  if (camposInvalidos.value.categoria) errores.push('Selecciona una categoría')
  if (camposInvalidos.value.detalles) errores.push('Agrega al menos una flor con tipo, color y cantidad')
  return errores
})

function campoInvalido(nombre: string) {
  return camposInvalidos.value[nombre] && erroresVisibles.value.length > 0
    ? 'w-full rounded-lg border border-red-400 bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-red-400'
    : 'w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary'
}

function triggerUpload() {
  fileInput.value?.click()
}

function onFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = () => {
    store.ramoFormFoto = reader.result as string
  }
  reader.readAsDataURL(file)
  input.value = ''
}

function cancelarEdicion() {
  store.resetRamoForm()
  erroresVisibles.value = []
}

function validarYGuardar() {
  erroresVisibles.value = erroresValidacion.value
  if (erroresVisibles.value.length > 0) return
  guardarRamo()
}

async function guardarRamo() {
  const esEdicion = store.esEdicionRamo
  await store.guardarRamo()
  if (!store.ramoError) {
    toast.success(esEdicion ? 'Ramo actualizado con éxito' : 'Ramo creado con éxito')
    erroresVisibles.value = []
  }
}

function confirmarEliminar(ramo: RamoResponse) {
  modalEliminar.value = ramo
}

async function ejecutarEliminar() {
  if (modalEliminar.value) {
    await store.eliminarRamo(modalEliminar.value.id)
    if (!store.ramoError) {
      toast.success('Ramo eliminado con éxito')
    }
    modalEliminar.value = null
  }
}
</script>
