<template>
  <div>
    <h2 class="text-xl font-serif text-text-primary mb-4">Gestiona tus eventos</h2>

    <!-- Formulario -->
    <div class="flex items-end gap-2">
      <div class="flex-1 max-w-md">
        <label class="block text-sm text-text-primary font-medium mb-1">Nombre del evento</label>
        <input
          v-model="store.catFormNombre"
          type="text"
          maxlength="100"
          class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
          placeholder="Nombre del evento"
        />
      </div>
      <button
        type="button"
        @click="guardarCategoria"
        :disabled="!store.catFormNombre.trim() || store.catLoading"
        class="px-6 py-2 rounded-lg text-sm font-medium transition-colors"
        :class="store.catLoading ? 'opacity-50 cursor-not-allowed bg-gray-300' : 'bg-btn-primary text-btn-primary-text hover:opacity-90'"
      >
        {{ store.catLoading ? 'Guardando...' : store.esEdicionCat ? 'Aceptar' : 'Añadir' }}
      </button>
      <button
        v-if="store.esEdicionCat"
        type="button"
        @click="store.resetCatForm()"
        class="px-6 py-2 rounded-lg text-sm font-medium bg-gray-200 text-gray-600 hover:bg-gray-300"
      >
        Cancelar
      </button>
    </div>

    <p v-if="store.catError" class="text-red-600 text-sm mt-2">{{ store.catError }}</p>

    <hr class="my-6 border-border-soft" />

    <!-- Búsqueda + Tabla -->
    <div>
      <div class="mb-4 max-w-xs">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Buscar evento por nombre..."
          class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
        />
      </div>

      <div class="bg-bg-card rounded-xl overflow-hidden">
        <table v-if="filteredCategorias.length > 0" class="w-full text-sm text-text-primary">
          <thead>
            <tr class="text-left border-b border-border-soft bg-bg-card/80">
              <th class="p-3 font-medium">Nombre</th>
              <th class="p-3"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cat in filteredCategorias" :key="cat.id" class="border-b border-border-soft/50 hover:bg-bg-input/50 transition-colors" :class="store.catEditandoId === cat.id ? 'bg-btn-primary/50' : ''">
              <td class="p-3">{{ cat.descripcionCategoriaRamo }}</td>
              <td class="p-3 flex gap-1">
                <button
                  type="button"
                  @click="store.editarCategoria(cat)"
                  class="px-2 py-1 text-sm text-text-primary hover:opacity-80"
                  title="Editar"
                >
                  <Icon icon="mdi:pencil-outline" class="text-lg" />
                </button>
                <button
                  type="button"
                  @click="confirmarEliminar(cat)"
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
          {{ store.categoriasVisibles.length === 0 ? 'No hay eventos creados aún.' : 'No se encontraron eventos.' }}
        </p>
      </div>
    </div>

    <!-- Modal eliminar con verificación -->
    <div
      v-if="modalEliminar"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
      @click.self="modalEliminar = null"
    >
      <div class="bg-white rounded-xl p-6 max-w-sm mx-4 shadow-xl">
        <template v-if="ramosEnCategoria.length > 0">
          <div class="flex items-center gap-2 mb-3">
            <Icon icon="mdi:alert-circle-outline" class="text-xl text-amber-500" />
            <p class="font-medium text-text-primary">No se puede eliminar</p>
          </div>
          <p class="text-text-primary mb-4">
            La categoría <strong>{{ modalEliminar.descripcionCategoriaRamo }}</strong> tiene
            <strong>{{ ramosEnCategoria.length }} ramo{{ ramosEnCategoria.length !== 1 ? 's' : '' }}</strong> asociado{{
              ramosEnCategoria.length !== 1 ? 's' : ''
            }}. Elimina o cambia la categoría de esos ramos primero.
          </p>
          <button
            type="button"
            @click="modalEliminar = null"
            class="px-4 py-2 rounded-lg text-sm bg-btn-primary text-btn-primary-text hover:opacity-90"
          >
            Entendido
          </button>
        </template>
        <template v-else>
          <p class="text-text-primary mb-4">
            ¿Estás seguro de que deseas eliminar el evento <strong>{{ modalEliminar.descripcionCategoriaRamo }}</strong>?
          </p>
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
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useNegocioStore } from '../../stores/negocio.store'
import type { CategoriaRamo } from '../../models/categoria-ramo.model'
import { useToast } from '~/composables/useToast'

const store = useNegocioStore()
const toast = useToast()

const searchQuery = ref('')
const modalEliminar = ref<CategoriaRamo | null>(null)

const ramosEnCategoria = computed(() => {
  if (!modalEliminar.value) return []
  return store.ramos.filter(r => r.categoria?.id === modalEliminar.value!.id)
})

const filteredCategorias = computed(() => {
  if (!searchQuery.value.trim()) return store.categoriasVisibles
  const q = searchQuery.value.trim().toLowerCase()
  return store.categoriasVisibles.filter(c => c.descripcionCategoriaRamo.toLowerCase().includes(q))
})

async function guardarCategoria() {
  const esEdicion = store.esEdicionCat
  await store.guardarCategoria()
  if (!store.catError) {
    toast.success(esEdicion ? 'Evento actualizado con éxito' : 'Evento creado con éxito')
  }
}

function confirmarEliminar(cat: CategoriaRamo) {
  modalEliminar.value = cat
}

async function ejecutarEliminar() {
  if (modalEliminar.value) {
    await store.eliminarCategoria(modalEliminar.value.id)
    if (!store.catError) {
      toast.success('Evento eliminado con éxito')
    }
    modalEliminar.value = null
  }
}
</script>
