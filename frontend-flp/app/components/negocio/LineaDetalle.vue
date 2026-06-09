<template>
  <div class="flex items-end gap-2 p-2 bg-bg-card rounded-lg">
    <div class="flex-1">
      <label class="block text-xs text-text-primary font-medium mb-1">Tipo De Flor</label>
      <select
        :value="modelValue.idTipoFlor"
        @change="onChange('idTipoFlor', Number(($event.target as HTMLSelectElement).value))"
        class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
      >
        <option :value="null" disabled>Seleccione...</option>
        <option v-for="tf in tiposFlor" :key="tf.id" :value="tf.id">
          {{ tf.descripcionFlor }}
        </option>
      </select>
    </div>
    <div class="flex-1">
      <label class="block text-xs text-text-primary font-medium mb-1">Color</label>
      <select
        :value="modelValue.idColorFlor"
        @change="onChange('idColorFlor', Number(($event.target as HTMLSelectElement).value))"
        class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
      >
        <option :value="null" disabled>Seleccione...</option>
        <option v-for="c in colores" :key="c.id" :value="c.id">
          {{ c.descripcionColor }}
        </option>
      </select>
    </div>
    <div class="w-20">
      <label class="block text-xs text-text-primary font-medium mb-1">Cant.</label>
      <input
        type="number"
        min="1"
        :value="modelValue.cantidad"
        @input="onChange('cantidad', Number(($event.target as HTMLInputElement).value))"
        class="w-full rounded-lg border border-border-soft bg-bg-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:ring-2 focus:ring-btn-primary"
      />
    </div>
    <button
      type="button"
      @click="$emit('eliminar')"
      class="mb-0.5 px-2 py-2 text-red-500 hover:text-red-700 text-lg leading-none"
      title="Eliminar flor"
    >
      ✕
    </button>
  </div>
</template>

<script setup lang="ts">
import type { DetalleLineaForm } from '../../models/ramo.model'
import type { TipoFlor } from '../../models/tipo-flor.model'
import type { ColorFlor } from '../../models/color-flor.model'

const props = defineProps<{
  modelValue: DetalleLineaForm
  tiposFlor: TipoFlor[]
  colores: ColorFlor[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: DetalleLineaForm]
  eliminar: []
}>()

function onChange(field: keyof DetalleLineaForm, value: number) {
  emit('update:modelValue', { ...props.modelValue, [field]: value })
}
</script>
