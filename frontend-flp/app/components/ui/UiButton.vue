<!--
  @author esteban
  Componente UI de botón reutilizable para el sistema Flores Eternas.
  Permite cambiar variant (primary/secondary), size, y estado de carga.
  Sigue el patrón de componentes UI puros: sin lógica de negocio, solo presentación.
-->
<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :class="buttonClasses"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="inline-block w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin" />
    <slot />
  </button>
</template>

<script setup lang="ts">
/**
 * @author esteban
 * Props del componente UiButton.
 * Cada prop controla un aspecto de la apariencia o comportamiento del botón.
 */
interface Props {
  /** Variante visual del botón: primary (fondo) o secondary (outline) */
  variant?: 'primary' | 'secondary'
  /** Tamaño del botón: sm (pequeño), md (mediano), lg (grande) */
  size?: 'sm' | 'md' | 'lg'
  /** Deshabilita el botón, impide interacción y muestra estilo de deshabilitado */
  disabled?: boolean
  /** Muestra un spinner animado y deshabilita el botón */
  loading?: boolean
  /** Tipo HTML del botón: button, submit, reset */
  type?: 'button' | 'submit' | 'reset'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  type: 'button',
})

defineEmits<{
  click: [event: MouseEvent]
}>()

/**
 * @author esteban
 * Clases CSS del botón combinadas. Usa computed para asegurar que Tailwind
 * procese correctamente las clases dinámicas.
 */
const buttonClasses = computed(() => {
  const base = 'inline-flex items-center justify-center gap-2 rounded-2xl font-medium transition-all font-[\'Poppins\'] focus:outline-none focus:ring-2 focus:ring-[#FFEDE3] focus:ring-offset-2'
  const sizeMap = {
    sm: 'px-3 py-1.5 text-sm min-h-[32px]',
    md: 'px-5 py-2.5 text-base min-h-[44px]',
    lg: 'px-7 py-3 text-2xl min-h-[52px]',
  }
  const variantMap = {
    primary: 'bg-[#FFEDE3] text-[#83572E]',
    secondary: 'bg-transparent text-[#83572E] border-2 border-[#83572E] hover:bg-[#FFEDE3]/30',
  }
  const stateClass = (props.disabled || props.loading) ? 'opacity-60 cursor-not-allowed' : 'hover:opacity-90 active:scale-[0.98]'

  return [base, sizeMap[props.size], variantMap[props.variant], stateClass].join(' ')
})
</script>