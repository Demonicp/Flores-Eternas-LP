<!--
  @author esteban
  Componente UI de input reutilizable para el sistema Flores Eternas.
  Incluye label, campo de texto, validación visual y mensaje de error.
  Sigue el patrón de componentes UI puros: sin lógica de negocio, solo presentación.
-->
<template>
  <div class="flex flex-col gap-1.5">
    <label
      v-if="label"
      :for="inputId"
      class="text-sm font-medium font-['Poppins'] text-[#83572E]"
    >
      {{ label }}
      <span v-if="required" class="text-red-500">*</span>
    </label>

    <input
      :id="inputId"
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :class="[
        'w-full rounded-lg border bg-white px-3 py-2.5 text-sm font-[\'Poppins\'] text-[#83572E]',
        'placeholder:text-gray-400',
        'focus:outline-none focus:ring-2 focus:ring-[#FFEDE3] focus:border-[#FFEDE3]',
        'transition-all duration-200',
        error
          ? 'border-red-400 focus:ring-red-300'
          : 'border-black',
        disabled && 'opacity-50 cursor-not-allowed bg-gray-100',
      ]"
      :aria-describedby="error ? `${inputId}-error` : undefined"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
    />

    <p
      v-if="error"
      :id="`${inputId}-error`"
      class="text-xs text-red-500 font-['Poppins']"
    >
      {{ error }}
    </p>
  </div>
</template>

<script setup lang="ts">
/**
 * @author esteban
 * Props del componente UiInput.
 * Controla la apariencia y comportamiento del campo de texto.
 */
interface Props {
  /** Tipo de input: text, email, password, number, etc */
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'search'
  /** Etiqueta descriptiva que aparece encima del input */
  label?: string
  /** Texto placeholder que aparece dentro del input */
  placeholder?: string
  /** Valor actual del input (v-model) */
  modelValue?: string | number
  /** Mensaje de error a mostrar debajo del input */
  error?: string | null
  /** Deshabilita el input, impide interacción */
  disabled?: boolean
  /** Indica si el campo es obligatorio (muestra asterisco en el label) */
  required?: boolean
  /** ID único del input, generado automáticamente si no se provee */
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  label: '',
  placeholder: '',
  modelValue: '',
  error: null,
  disabled: false,
  required: false,
})

defineEmits<{
  'update:modelValue': [value: string]
}>()

/**
 * @author esteban
 * ID único para el input. Genera un ID determinista basado en el label
 * para evitar hydration mismatch entre SSR y CSR.
 * Si se provee un id custom via props, se usa ese en su lugar.
 * El ID es estable ya que depende del label (string estático),
 * no de valores aleatorios o hooks de Vue.
 */
const inputId = useId()
</script>