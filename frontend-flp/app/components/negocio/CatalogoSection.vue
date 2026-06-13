<template>
  <div>
    <h2 class="text-xl font-serif text-text-primary mb-4">Gestiona tu catálogo</h2>

    <div class="flex flex-col lg:flex-row gap-6">
      <!-- Formulario izquierdo -->
      <div class="flex-1 space-y-4">
        <!-- Upload imagen -->
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
          <input
            ref="fileInput"
            type="file"
            accept=".png,.jpg,.jpeg"
            class="hidden"
            @change="onFileSelected"
          />
        </div>

        <!-- Nombre -->
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Nombre</label>
          <input
            v-model="store.ramoFormNombre"
            type="text"
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
            placeholder="Nombre del ramo"
          />
        </div>

        <!-- Líneas de detalle -->
        <div class="space-y-2">
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

        <!-- Categoría -->
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Elige Su Categoría</label>
          <select
            v-model="store.ramoFormIdCategoria"
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
          >
            <option :value="null" disabled>Seleccione...</option>
            <option v-for="cat in store.categoriasDisponiblesVisibles" :key="cat.id" :value="cat.id">
              {{ cat.descripcionCategoriaRamo }}
            </option>
          </select>
        </div>

        <!-- Precio -->
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Precio De Tu Ramo</label>
          <input
            v-model.number="store.ramoFormPrecio"
            type="number"
            step="0.01"
            min="0"
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
            placeholder="0.00"
          />
        </div>

        <!-- Descripción -->
        <div>
          <label class="block text-sm text-text-primary font-medium mb-1">Descripción De Tu Ramo</label>
          <textarea
            v-model="store.ramoFormDescripcion"
            rows="3"
            class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary resize-none"
            placeholder="Descripción del ramo"
          />
        </div>

        <!-- Botones -->
        <div class="flex gap-2">
          <button
            type="button"
            @click="store.guardarRamo()"
            :disabled="!formValido || store.ramoLoading"
            class="px-6 py-2 rounded-lg text-sm font-medium transition-colors"
            :class="store.ramoLoading ? 'opacity-50 cursor-not-allowed bg-gray-300' : 'bg-btn-primary text-btn-primary-text hover:opacity-90'"
          >
            {{ store.ramoLoading ? 'Guardando...' : store.esEdicionRamo ? 'Aceptar' : 'Aceptar' }}
          </button>
          <button
            v-if="store.esEdicionRamo"
            type="button"
            @click="store.resetRamoForm()"
            class="px-6 py-2 rounded-lg text-sm font-medium bg-gray-200 text-gray-600 hover:bg-gray-300 transition-colors"
          >
            Cancelar
          </button>
        </div>

        <p v-if="store.ramoError" class="text-red-600 text-sm">{{ store.ramoError }}</p>
      </div>

      <!-- Lista derecha -->
      <div class="w-full lg:w-80">
        <h3 class="text-base font-serif text-text-primary mb-2">Tus Ramos Publicados</h3>
        <div class="bg-bg-card rounded-xl p-3 max-h-96 overflow-y-auto">
          <table v-if="store.ramosVisibles.length > 0" class="w-full text-sm text-text-primary">
            <thead>
              <tr class="text-left border-b border-border-soft">
                <th class="pb-2 pr-2 font-medium">Nombre</th>
                <th class="pb-2 pr-2 font-medium">Precio</th>
                <th class="pb-2"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="ramo in store.ramosVisibles" :key="ramo.id" class="border-b border-border-soft/50">
                <td class="py-2 pr-2 truncate max-w-[140px]">{{ ramo.nombreRamo }}</td>
                <td class="py-2 pr-2">${{ Number(ramo.precioRamo).toFixed(2) }}</td>
                <td class="py-2 flex gap-1">
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
                    class="px-2 py-1 text-sm text-red-500 hover:text-red-700"
                    title="Eliminar"
                  >
                    <Icon icon="mdi:delete-outline" class="text-lg" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="text-sm text-text-primary text-center py-4">
            No hay ramos publicados aún.
          </p>
        </div>
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
import { ref, computed } from 'vue'
import { useNegocioStore } from '../../stores/negocio.store'
import type { RamoResponse } from '../../models/ramo.model'
import LineaDetalle from '~/components/negocio/LineaDetalle.vue'

const store = useNegocioStore()
const fileInput = ref<HTMLInputElement | null>(null)
const modalEliminar = ref<RamoResponse | null>(null)

const formValido = computed(() =>
  store.ramoFormNombre.trim()
  && store.ramoFormPrecio !== null && store.ramoFormPrecio > 0
  && store.ramoFormIdCategoria !== null
  && store.ramoFormDetalles.some(d => d.idTipoFlor && d.idColorFlor && d.cantidad > 0)
)

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

function confirmarEliminar(ramo: RamoResponse) {
  modalEliminar.value = ramo
}

async function ejecutarEliminar() {
  if (modalEliminar.value) {
    await store.eliminarRamo(modalEliminar.value.id)
    modalEliminar.value = null
  }
}
</script>
