<template>
  <div class="min-h-screen bg-bg-page">
    <TheHeader />
    <CatalogoPublico />
    <CarritoLateral />
  </div>
</template>

<script setup lang="ts">
import TheHeader from '~/components/catalogo/TheHeader.vue'
import CatalogoPublico from '~/components/catalogo/CatalogoPublico.vue'
import CarritoLateral from '~/components/catalogo/CarritoLateral.vue'
</script>
