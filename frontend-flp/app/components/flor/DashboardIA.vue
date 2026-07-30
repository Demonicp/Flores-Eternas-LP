<template>
  <div class="space-y-4">
    <div v-if="imagenUrl" class="bg-white rounded-2xl shadow-lg p-4">
      <div class="inline-block px-3 py-1 bg-[#5C3D2E] text-white text-xs font-semibold rounded-full mb-3">
        Vista Previa IA
      </div>
      <img :src="imagenUrl" alt="Vista previa del ramo" class="w-full max-h-72 object-contain rounded-xl" />
    </div>

    <div class="bg-amber-50 border border-amber-300 rounded-2xl p-3 flex items-start gap-2">
      <Icon icon="mdi:alert-circle" class="text-amber-500 text-lg flex-shrink-0 mt-0.5" />
      <div class="text-xs text-amber-800">
        <p class="font-semibold">Generación única por pedido</p>
        <p>Si cambias los tipos de flor, la imagen se borrará y podrás generar una nueva.</p>
      </div>
    </div>

    <button
      @click="$emit('generar')"
      :disabled="generando || imagenGenerada"
      class="w-full py-3 rounded-full font-bold text-sm text-white flex items-center justify-center gap-2 transition shadow-md"
      :class="generando || imagenGenerada ? 'bg-gray-300 text-gray-500 cursor-not-allowed' : 'bg-[#7A4E2D] hover:bg-[#5E3A1F] cursor-pointer'"
    >
      <Icon :icon="generando ? 'mdi:loading' : 'mdi:sparkles'" class="text-lg" :class="generando ? 'animate-spin' : ''" />
      {{ generando ? 'Generando con IA...' : imagenGenerada ? 'Imagen ya generada' : 'Generar vista previa con IA' }}
    </button>

    <p v-if="errorGeneracion" class="text-red-500 text-xs text-center">{{ errorGeneracion }}</p>
  </div>
</template>

<script setup>
defineProps({
  imagenUrl: { type: String, default: null },
  imagenGenerada: { type: Boolean, default: false },
  generando: { type: Boolean, default: false },
  errorGeneracion: { type: String, default: '' },
})

defineEmits(['generar'])
</script>