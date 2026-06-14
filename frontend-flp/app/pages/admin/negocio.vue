<template>
  <div class="min-h-screen space-y-8">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <CatalogoSection />
    </section>

    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <EventosSection />
    </section>

    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <OpcionesSection />
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useNegocioStore } from '~/stores/negocio.store'
import CatalogoSection from '~/components/negocio/CatalogoSection.vue'
import EventosSection from '~/components/negocio/EventosSection.vue'
import OpcionesSection from '~/components/negocio/OpcionesSection.vue'

definePageMeta({ layout: 'admin' })

const store = useNegocioStore()

onMounted(() => {
  store.cargarTodo()
})
</script>
