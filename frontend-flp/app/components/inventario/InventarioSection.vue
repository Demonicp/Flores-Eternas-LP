<template>
    <div>
  
      <h2 class="text-xl font-serif text-text-primary mb-4">
        Gestiona tu inventario
      </h2>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">

<input
  v-model="store.nombre"
  type="text"
  placeholder="Nombre del producto"
  class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2"
/>

<input
  v-model="store.descripcion"
  type="text"
  placeholder="Descripción"
  class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2"
/>

<input
  v-model.number="store.precioCosto"
  type="number"
  min="0"
  placeholder="Precio costo"
  class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2"
/>

<input
  v-model.number="store.stock"
  type="number"
  min="0"
  placeholder="Stock"
  class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2"
/>

</div>

<div class="mb-6 flex gap-2">

<button
  @click="guardarProducto"
  :disabled="guardando"
  class="px-6 py-2 rounded-lg bg-btn-primary text-btn-primary-text"
>
  {{ guardando ? 'Guardando...' : (store.esEdicion ? 'Actualizar' : 'Guardar') }}
</button>

<button
  v-if="store.esEdicion"
  @click="store.resetForm()"
  class="px-6 py-2 rounded-lg bg-gray-300"
>
  Cancelar
</button>

</div>

<p v-if="store.error" class="text-red-600 text-sm mb-4">{{ store.error }}</p>

      <div class="mb-4 flex flex-col sm:flex-row gap-3 items-start sm:items-center">
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar producto..."
          class="w-full sm:max-w-xs rounded-lg border border-border-soft bg-bg-input px-3 py-2"
        />
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

        <div
          v-if="productosFiltrados.length && vistaLista"
          class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3 p-3"
        >
          <div
            v-for="item in productosFiltrados"
            :key="item.id"
            class="bg-white border border-border-soft rounded-xl p-3 shadow-sm flex flex-col gap-2"
          >
            <p class="text-sm font-semibold text-text-primary truncate">{{ item.nombre }}</p>
            <p class="text-xs text-text-primary/70 truncate">{{ item.descripcion || '—' }}</p>
            <div class="text-xs text-text-primary/80 space-y-0.5">
              <div class="flex justify-between"><span class="text-text-primary/60">Costo</span><span class="font-medium">${{ item.precioCosto }}</span></div>
              <div class="flex justify-between"><span class="text-text-primary/60">Stock</span><span class="font-medium">{{ item.stock }}</span></div>
            </div>
            <div class="flex justify-end gap-1 border-t border-border-soft/50 pt-2">
              <button
                @click="store.editarProducto(item)"
                class="px-2 py-1 text-sm text-[#8C5A3C] hover:opacity-80"
                title="Editar"
              >
                <Icon icon="mdi:pencil-outline" class="text-lg" />
              </button>
              <button
                @click="confirmarEliminar(item)"
                class="px-2 py-1 text-sm text-[#A52A2A] hover:text-[#7C1D1B]"
                title="Eliminar"
              >
                <Icon icon="mdi:delete-outline" class="text-lg" />
              </button>
            </div>
          </div>
        </div>

        <div
          v-else-if="productosFiltrados.length"
          class="flex flex-col gap-2 p-3"
        >
          <div
            v-for="item in productosFiltrados"
            :key="item.id"
            class="bg-white border border-border-soft rounded-xl px-3 py-2 shadow-sm flex items-center gap-3"
          >
            <span class="text-sm font-medium text-text-primary flex-1 truncate">{{ item.nombre }}</span>
            <span class="text-xs text-text-primary/70 flex-shrink-0 hidden md:block truncate max-w-[220px]">{{ item.descripcion || '—' }}</span>
            <span class="text-xs text-text-primary/80 flex-shrink-0">${{ item.precioCosto }} · {{ item.stock }}</span>
            <span class="flex gap-1 flex-shrink-0">
              <button
                @click="store.editarProducto(item)"
                class="px-2 py-1 text-sm text-[#8C5A3C] hover:opacity-80"
                title="Editar"
              >
                <Icon icon="mdi:pencil-outline" class="text-lg" />
              </button>
              <button
                @click="confirmarEliminar(item)"
                class="px-2 py-1 text-sm text-[#A52A2A] hover:text-[#7C1D1B]"
                title="Eliminar"
              >
                <Icon icon="mdi:delete-outline" class="text-lg" />
              </button>
            </span>
          </div>
        </div>
  
        <div
          v-else
          class="text-center py-8"
        >
          No hay productos registrados.
        </div>
  
      </div>
  
      <!-- Modal eliminar -->
      <div
        v-if="modalEliminar"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
        @click.self="modalEliminar = null"
      >
        <div class="bg-white rounded-xl p-6 max-w-sm mx-4 shadow-xl">
          <p class="text-text-primary mb-4">
            ¿Estás seguro de que deseas eliminar el producto <strong>{{ modalEliminar.nombre }}</strong>?
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
        </div>
      </div>
  
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { useInventarioStore } from '~/stores/inventario.store'
  import { useToast } from '~/composables/useToast'
  import type { Inventario } from '~/models/inventario.model'
  
  const store = useInventarioStore()
  const toast = useToast()

  const busqueda = ref('')
  const vistaLista = ref(false)
  const guardando = ref(false)
  const modalEliminar = ref<Inventario | null>(null)

  onMounted(() => {
    store.cargarInventario()
  })

  async function guardarProducto() {
    const esEdicion = store.esEdicion
    guardando.value = true
    await store.guardarProducto()
    guardando.value = false
    if (!store.error) {
      toast.success(esEdicion ? 'Producto actualizado con éxito' : 'Producto creado con éxito')
    } else {
      toast.error(store.error)
    }
  }

  function confirmarEliminar(producto: Inventario) {
    modalEliminar.value = producto
  }

  async function ejecutarEliminar() {
    if (!modalEliminar.value) return
    const producto = modalEliminar.value
    await store.eliminarProducto(producto.id)
    if (!store.error) {
      toast.success('Producto eliminado con éxito')
    } else {
      toast.error(store.error)
    }
    modalEliminar.value = null
  }

  const productosFiltrados = computed(() => {
    return store.productos.filter(producto =>
      producto.nombre.toLowerCase().includes(
        busqueda.value.toLowerCase()
      )
    )
  })
  </script>