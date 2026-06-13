<template>
  <div class="min-h-screen">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <EventosSection />
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useNegocioStore } from '~/stores/negocio.store'
import EventosSection from '~/components/negocio/EventosSection.vue'

definePageMeta({ layout: 'admin' })

const store = useNegocioStore()

onMounted(() => {
  store.cargarCategorias()
})
</script>
