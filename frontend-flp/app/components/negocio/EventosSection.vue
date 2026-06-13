<template>
  <div>
    <h2 class="text-xl font-serif text-text-primary mb-4">Gestiona tus eventos</h2>

    <div class="flex flex-col lg:flex-row gap-6">
      <!-- Formulario -->
      <div class="flex-1">
        <div class="flex items-end gap-2">
          <div class="flex-1">
            <label class="block text-sm text-text-primary font-medium mb-1">Nombre del evento</label>
            <input
              v-model="store.catFormNombre"
              type="text"
              class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
              placeholder="Nombre del evento"
            />
          </div>
          <button
            type="button"
            @click="store.guardarCategoria()"
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
      </div>

      <!-- Lista -->
      <div class="w-full lg:w-80">
        <h3 class="text-base font-serif text-text-primary mb-2">Tus Eventos Creados</h3>
        <div class="bg-bg-card rounded-xl p-3 max-h-64 overflow-y-auto">
          <table v-if="store.categoriasVisibles.length > 0" class="w-full text-sm text-text-primary">
            <thead>
              <tr class="text-left border-b border-border-soft">
                <th class="pb-2 pr-2 font-medium">Nombre</th>
                <th class="pb-2"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="cat in store.categoriasVisibles" :key="cat.id" class="border-b border-border-soft/50">
                <td class="py-2 pr-2 truncate max-w-[200px]">{{ cat.descripcionCategoriaRamo }}</td>
                <td class="py-2 flex gap-1">
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
                    @click="store.eliminarCategoria(cat.id)"
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
            No hay eventos creados aún.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useNegocioStore } from '../../stores/negocio.store'

const store = useNegocioStore()
</script>
