<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup>
import { onMounted } from 'vue'
import { getApiBase } from '~/services/api-client'

onMounted(() => {
  fetch(`${getApiBase()}/api/catalogo/health`).catch(() => {})
})
</script>
