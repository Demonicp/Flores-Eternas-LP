<template>
  <NuxtLayout>
    <NuxtPage />
    <CarritoLateral v-if="cartStore.abierto" />
  </NuxtLayout>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { getApiBase } from '~/services/api-client'
import { useCartStore } from '~/stores/cart.store'
import CarritoLateral from '~/components/catalogo/CarritoLateral.vue'

const cartStore = useCartStore()

onMounted(() => {
  fetch(`${getApiBase()}/api/catalogo/health`).catch(() => {})
})
</script>
