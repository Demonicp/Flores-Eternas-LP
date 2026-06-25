<template>
  <div
    class="product-card min-w-[260px] max-w-[260px] bg-white rounded-2xl p-4 flex flex-col gap-3 shadow-sm hover:shadow-md transition-all duration-300 cursor-pointer hover:-translate-y-0.5 group"
  >
    <div class="relative">
      <span
        v-if="badge"
        class="absolute top-0 left-0 z-10 px-3 py-1 rounded-full text-xs font-medium"
        :class="badge === 'nuevo' ? 'bg-green-100 text-green-700' : 'bg-amber-100 text-amber-700'"
      >
        {{ badgeLabel }}
      </span>
    </div>

    <div
      class="w-full aspect-[4/3] rounded-xl bg-bg-card flex items-center justify-center overflow-hidden relative"
    >
      <img
        v-if="producto.foto && !imgError"
        :src="producto.foto"
        :alt="producto.nombre"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
        @error="imgError = true"
      />
      <Icon v-else icon="mdi:flower-tulip-outline" class="text-5xl text-text-primary/30" />
      <div class="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors duration-300 rounded-xl" />
    </div>

    <div class="flex flex-col gap-1.5 flex-1 min-h-[90px]">
      <h3 class="font-serif text-base text-text-primary font-medium truncate group-hover:text-btn-primary transition-colors">
        {{ producto.nombre }}
      </h3>
      <p v-if="producto.descripcionCorta" class="text-xs text-text-primary/60 line-clamp-2 leading-relaxed flex-1">
        {{ producto.descripcionCorta }}
      </p>
      <p v-else class="text-xs text-text-primary/60 flex-1">&nbsp;</p>
      <p class="text-sm text-text-primary/70 font-medium">
        {{ formatoPrecio(producto.precio) }} COP
      </p>
    </div>

    <button
      class="w-full flex items-center justify-center gap-2 bg-btn-primary text-btn-primary-text px-4 py-2.5 rounded-full text-sm font-medium hover:opacity-90 transition-opacity duration-200"
      @click.stop="$emit('add-to-cart', producto)"
    >
      <Icon icon="mdi:cart-outline" class="text-lg" />
      añadir al carrito
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { RamoResumen } from '../../models/catalogo.model'

const props = defineProps<{
  producto: RamoResumen
  badge?: string
}>()

defineEmits<{
  'add-to-cart': [producto: RamoResumen]
}>()

const imgError = ref(false)

const badgeLabel = computed(() => {
  if (props.badge === 'nuevo') return 'nuevo'
  if (props.badge === 'entrega-inmediata') return 'Entrega inmediata'
  return ''
})

function formatoPrecio(valor: number): string {
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(valor)
}
</script>
