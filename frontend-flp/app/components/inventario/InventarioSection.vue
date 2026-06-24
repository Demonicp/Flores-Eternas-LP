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
  @click="store.guardarProducto()"
  class="px-6 py-2 rounded-lg bg-btn-primary text-btn-primary-text"
>
  {{ store.esEdicion ? 'Actualizar' : 'Guardar' }}
</button>

<button
  v-if="store.esEdicion"
  @click="store.resetForm()"
  class="px-6 py-2 rounded-lg bg-gray-300"
>
  Cancelar
</button>

</div>

      <div class="mb-4">
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar producto..."
          class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2"
        />
      </div>
  
      <div class="bg-bg-card rounded-xl overflow-hidden">
  
        <table
          v-if="productosFiltrados.length"
          class="w-full text-sm"
        >
          <thead>
            <tr class="border-b border-border-soft">
              <th class="p-3 text-left">Nombre</th>
              <th class="p-3 text-left">Descripción</th>
              <th class="p-3 text-left">Costo</th>
              <th class="p-3 text-left">Stock</th>
              <th class="p-3 text-left">Acciones</th>
            </tr>
          </thead>
  
          <tbody>
  
            <tr
              v-for="item in productosFiltrados"
              :key="item.id"
              class="border-b border-border-soft"
            >
              <td class="p-3">
                {{ item.nombre }}
              </td>
  
              <td class="p-3">
                {{ item.descripcion }}
              </td>
  
              <td class="p-3">
                ${{ item.precioCosto }}
              </td>
  
              <td class="p-3">
                {{ item.stock }}
              </td>
              <td class="p-3 flex gap-2">

                <button
                  @click="store.editarProducto(item)"
                  class="text-blue-600 hover:text-blue-800"
                >
                  <Icon icon="mdi:pencil-outline" />
                </button>

                <button
                @click="store.eliminarProducto(item.id)"
                class="text-red-600 hover:text-red-800"
                >
                <Icon icon="mdi:delete-outline" />
                </button>

              </td>
            </tr>
  
          </tbody>
        </table>
  
        <div
          v-else
          class="text-center py-8"
        >
          No hay productos registrados.
        </div>
  
      </div>
  
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { useInventarioStore } from '~/stores/inventario.store'
  
  const store = useInventarioStore()
  
  const busqueda = ref('')
  
  onMounted(() => {
    store.cargarInventario()
  })
  
  const productosFiltrados = computed(() => {
    return store.productos.filter(producto =>
      producto.nombre.toLowerCase().includes(
        busqueda.value.toLowerCase()
      )
    )
  })
  </script>