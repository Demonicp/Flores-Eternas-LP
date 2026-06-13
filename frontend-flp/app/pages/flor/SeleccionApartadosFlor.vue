<template>
    <div class="min-h-screen bg-[#FFFCF6] font-['Playfair_Display'] text-[#6B3E2E] flex flex-col">
      <!-- Header -->
      <header class="bg-[#FFEDE3] py-8 text-center">
        <h1 class="text-5xl font-bold leading-tight">LP<br />FLORES ETERNAS</h1>
      </header>
  
      <!-- Contenido principal -->
      <main class="flex-1 grid grid-cols-2 gap-20 px-20 py-16">
        <!-- Flor seleccionada -->
        <div class="flex flex-col items-center justify-center space-y-10">
          <h2 class="text-2xl">Flor seleccionada</h2>
          <div class="w-72 h-96 bg-[#FFEDE3] rounded-lg shadow-inner"></div>
  
          <div class="text-center space-y-4">
            <p class="text-xl">¿Quieres realizar adiciones? aquí</p>
            <button
              class="bg-[#FFEDE3] text-[#6B3E2E] px-10 py-3 rounded-lg shadow hover:bg-[#FFDCC8] text-lg"
            >
              Adiciones
            </button>
          </div>
        </div>
  
        <!-- Cantidad y colores -->
        <div class="flex flex-col justify-center space-y-12">
          <div>
            <h2 class="text-2xl mb-6">Cantidad:</h2>
            <div class="flex flex-wrap gap-5">
              <button
                v-for="cantidad in cantidades"
                :key="cantidad"
                @click="form.cantidad = cantidad"
                :class="[
                  'w-14 h-14 rounded-full flex items-center justify-center text-white text-lg font-semibold',
                  form.cantidad === cantidad ? 'bg-[#6B3E2E]' : 'bg-[#B47A4E]'
                ]"
              >
                {{ cantidad }}
              </button>
            </div>
          </div>
  
          <div>
            <h2 class="text-2xl mb-6">Colores:</h2>
            <div class="grid grid-cols-7 gap-6">
              <button
                v-for="color in colores"
                :key="color.id"
                @click="form.colorSeleccionado = color.id"
                :style="{ backgroundColor: color.hex }"
                :class="[
                  'w-10 h-10 rounded-full border-2',
                  form.colorSeleccionado === color.id ? 'border-[#6B3E2E]' : 'border-transparent'
                ]"
              ></button>
            </div>
          </div>
  
          <div class="flex justify-center pt-8">
            <button
              class="bg-[#FFEDE3] text-[#6B3E2E] px-12 py-3 rounded-lg shadow hover:bg-[#FFDCC8] text-lg"
            >
              Siguiente →
            </button>
          </div>
        </div>
      </main>
  
      <!-- Footer -->
      <footer class="text-right px-10 py-6 text-lg text-[#6B3E2E]">
        ¿Necesitas ayuda? <span class="text-2xl">❓</span>
      </footer>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  
  const cantidades = [2, 3, 7, 10, 12, 15, 20, 25, 30, 35, 40, 50, 100]
  const colores = ref<{ id: number; hex: string }[]>([])
  const form = ref({
    cantidad: 0,
    colorSeleccionado: null,
  })
  
  // Cargar colores desde el backend
  onMounted(async () => {
    try {
      const { data } = await $fetch('https://tu-backend.com/api/colores')
      colores.value = data
    } catch (error) {
      console.error('Error al cargar colores:', error)
    }
  })
  </script>
  
  <style scoped>
  button {
    transition: all 0.2s ease;
  }
  button:hover {
    transform: scale(1.08);
  }
  </style>
  