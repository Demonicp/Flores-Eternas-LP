<!--
  @author esteban
  Componente UI de spinner reutilizable para el sistema Flores Eternas.
  Indicador visual de carga/loading animado.
  Sigue el patrón de componentes UI puros: sin lógica de negocio, solo presentación.
-->
<template>
  <div class="flex items-center justify-center" :style="{ width: sizeValue, height: sizeValue }">
    <div
      class="rounded-full border-2 border-[#FFEDE3] border-t-[#83572E] animate-spin"
      :style="{ width: sizeValue, height: sizeValue }"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * @author esteban
 * Props del componente UiSpinner.
 * Controla el tamaño del indicador de carga.
 */
interface Props {
  /** Tamaño del spinner: 'sm' (16px), 'md' (24px), 'lg' (40px) o valor numérico en px */
  size?: 'sm' | 'md' | 'lg' | number
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
})

/**
 * @author esteban
 * Conversión del tamaño a valor en píxeles.
 * Soporta presets ('sm', 'md', 'lg') o valores numéricos personalizados.
 */
const sizeValue = computed(() => {
  if (typeof props.size === 'number') return `${props.size}px`
  const sizes = { sm: '16px', md: '24px', lg: '40px' }
  return sizes[props.size] ?? sizes.md
})
</script>

<script lang="ts">
/** Necesario para usar computed en el script setup */
import { computed } from 'vue'
</script>