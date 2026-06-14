<template>
  <div class="min-h-screen bg-[#FFFAF5] flex flex-col items-center relative">
    <TheHeader />
    <main class="flex flex-col items-center w-full mt-10 px-8 max-w-5xl pb-20">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import TheHeader from '~/components/catalogo/TheHeader.vue'
</script>
