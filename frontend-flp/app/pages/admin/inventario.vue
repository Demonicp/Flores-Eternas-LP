<template>
  <div class="min-h-screen">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <InventarioSection />
    </section>
  </div>
</template>

<script setup lang="ts">
import InventarioSection from '~/components/inventario/InventarioSection.vue'

definePageMeta({
  layout: 'admin'
})
</script>