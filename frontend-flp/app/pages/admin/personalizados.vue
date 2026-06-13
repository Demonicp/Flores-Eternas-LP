<template>
  <div class="min-h-screen">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <OpcionesSection />
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useNegocioStore } from '~/stores/negocio.store'
import OpcionesSection from '~/components/negocio/OpcionesSection.vue'

definePageMeta({ layout: 'admin' })

const store = useNegocioStore()

onMounted(() => {
  store.cargarColores()
  store.cargarFlores()
})
</script>
