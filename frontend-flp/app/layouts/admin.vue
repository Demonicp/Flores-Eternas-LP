<template>
  <div class="min-h-screen flex">
    <aside class="w-64 bg-gray-900 text-white flex flex-col">
      <div class="p-4 text-xl font-bold border-b border-gray-700">
        Flores Eternas
      </div>
      <nav class="flex-1 p-4 space-y-2">
        <NuxtLink
          v-for="item in menuItems"
          :key="item.to"
          :to="item.to"
          class="block px-3 py-2 rounded hover:bg-gray-700 transition-colors"
        >
          {{ item.label }}
        </NuxtLink>
      </nav>
    </aside>
    <main class="flex-1 bg-gray-100">
      <header class="bg-white shadow px-6 py-4">
        <h2 class="text-lg font-semibold text-gray-800">Panel de Administración</h2>
      </header>
      <div class="p-6">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
const menuItems = [
  { label: 'Dashboard', to: '/admin/dashboard' },
  { label: 'Añadir Flores', to: '/admin/flores' },
  { label: 'Inventario', to: '/admin/inventario' },
  { label: 'Pedidos', to: '/admin/pedidos' },
]
</script>
