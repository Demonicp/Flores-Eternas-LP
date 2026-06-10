<!--
  @author esteban
  Componente UI de tarjeta reutilizable para el sistema Flores Eternas.
  Contenedor visual con fondo, borde redondeado, sombra opcional y título.
  Sigue el patrón de componentes UI puros: sin lógica de negocio, solo presentación.
-->
<template>
  <div
    :class="[
      'rounded-3xl bg-white/90 border border-[#E8D5C8] overflow-hidden backdrop-blur-sm',
      elevated ? 'shadow-md' : 'shadow-sm',
      padding ? `p-${padding}` : 'p-6',
    ]"
  >
    <div v-if="title" class="mb-4 border-b border-[#E8D5C8] pb-3">
      <h3
        :class="[
          'font-[\'Radley\'] text-xl text-[#83572E]',
          titleAlign === 'center' ? 'text-center' : 'text-left',
        ]"
      >
        {{ title }}
      </h3>
    </div>
    <slot />
  </div>
</template>

<script setup lang="ts">
/**
 * @author esteban
 * Props del componente UiCard.
 * Controla la apariencia del contenedor de tarjeta.
 */
interface Props {
  /** Título de la tarjeta que aparece en la parte superior con borde decorativo */
  title?: string
  /** Alineación del título: 'left' (default) o 'center' */
  titleAlign?: 'left' | 'center'
  /** Si es true, agrega sombra más pronunciada para efecto elevado */
  elevated?: boolean
  /** Padding interno de la tarjeta: 4, 6, 8 (corresponde a Tailwind spacing) */
  padding?: 4 | 6 | 8
}

withDefaults(defineProps<Props>(), {
  title: '',
  titleAlign: 'left',
  elevated: false,
  padding: 6,
})
</script>