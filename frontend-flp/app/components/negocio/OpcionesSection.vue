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
      <div class="flex flex-col lg:flex-row gap-6">
        <div class="flex-1">
          <div class="flex items-end gap-2">
            <div class="flex-1">
              <label class="block text-sm text-text-primary font-medium mb-1">Nombre del color</label>
              <input
                v-model="store.colFormNombre"
                type="text"
                class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
                placeholder="Nombre del color"
              />
            </div>
            <button
              type="button"
              @click="store.guardarColor()"
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
          <p v-if="store.colError" class="text-red-600 text-sm mt-2">{{ store.colError }}</p>
        </div>

        <div class="w-full lg:w-80">
          <h3 class="text-base font-serif text-text-primary mb-2">Tus Colores Añadidos</h3>
          <div class="bg-bg-card rounded-xl p-3 max-h-64 overflow-y-auto">
            <table v-if="store.colores.length > 0" class="w-full text-sm text-text-primary">
              <thead>
                <tr class="text-left border-b border-border-soft">
                  <th class="pb-2 pr-2 font-medium">Nombre</th>
                  <th class="pb-2"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="color in store.colores" :key="color.id" class="border-b border-border-soft/50">
                  <td class="py-2 pr-2 truncate max-w-[200px]">{{ color.descripcionColor }}</td>
                  <td class="py-2 flex gap-1">
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
                      @click="store.eliminarColor(color.id)"
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
              No hay colores añadidos aún.
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Modo Flores -->
    <div v-if="store.opcionSeleccionada === 'flor'">
      <div class="flex flex-col lg:flex-row gap-6">
        <div class="flex-1 space-y-3">
          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">Nombre de la flor</label>
            <input
              v-model="store.florFormDescripcion"
              type="text"
              class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
              placeholder="Nombre de la flor"
            />
          </div>
          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">Precio unidad</label>
            <input
              v-model.number="store.florFormPrecio"
              type="number"
              step="0.01"
              min="0"
              class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
              placeholder="0.00"
            />
          </div>
          <div>
            <label class="block text-sm text-text-primary font-medium mb-1">Porcentaje por mayor</label>
            <input
              v-model.number="store.florFormPorcentaje"
              type="number"
              step="0.01"
              min="0"
              max="100"
              class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
              placeholder="0"
            />
          </div>
          <div class="flex gap-2">
            <button
              type="button"
              @click="store.guardarFlor()"
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
          <p v-if="store.florError" class="text-red-600 text-sm">{{ store.florError }}</p>
        </div>

        <div class="w-full lg:w-96">
          <h3 class="text-base font-serif text-text-primary mb-2">Tus Flores o Colores Añadidos</h3>
          <div class="bg-bg-card rounded-xl p-3 max-h-80 overflow-y-auto">
            <table v-if="store.flores.length > 0" class="w-full text-sm text-text-primary">
              <thead>
                <tr class="text-left border-b border-border-soft">
                  <th class="pb-2 pr-2 font-medium">Nombre</th>
                  <th class="pb-2 pr-2 font-medium">Precio ud.</th>
                  <th class="pb-2 pr-2 font-medium">% Mayor</th>
                  <th class="pb-2"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="flor in store.flores" :key="flor.id" class="border-b border-border-soft/50">
                  <td class="py-2 pr-2 truncate max-w-[140px]">{{ flor.descripcionFlor }}</td>
                  <td class="py-2 pr-2">${{ Number(flor.precioUnidad).toFixed(2) }}</td>
                  <td class="py-2 pr-2">{{ Number(flor.porcentajePorMayor).toFixed(1) }}%</td>
                  <td class="py-2 flex gap-1">
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
                      @click="store.eliminarFlor(flor.id)"
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
              No hay flores añadidas aún.
            </p>
          </div>
        </div>
      </div>
    </div>

    <p v-if="!store.opcionSeleccionada" class="text-sm text-text-primary text-center py-8">
      Seleccione antes de visualizar
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useNegocioStore } from '../../stores/negocio.store'

const store = useNegocioStore()

const florFormValido = computed(() =>
  store.florFormDescripcion.trim()
  && store.florFormPrecio !== null && store.florFormPrecio > 0
  && store.florFormPorcentaje !== null && store.florFormPorcentaje >= 0
)
</script>
