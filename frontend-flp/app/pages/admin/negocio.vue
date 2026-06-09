<template>
  <div class="min-h-screen" style="background-color: #FCF9F6;">
    <div class="max-w-6xl mx-auto space-y-8">
      <!-- Sección A -->
      <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
        <CatalogoSection />
      </section>

      <!-- Sección B -->
      <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
        <EventosSection />
      </section>

      <!-- Sección C -->
      <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
        <OpcionesSection />
      </section>
    </div>
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
