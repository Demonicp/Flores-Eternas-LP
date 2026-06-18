<template>
  <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-12">

    <div v-if="loading" class="space-y-12">
      <div v-for="i in 3" :key="i">
        <div class="h-6 w-48 bg-gray-200 animate-pulse rounded mb-5" />
        <div class="flex gap-5 px-6">
          <div v-for="j in 4" :key="j" class="flex-shrink-0 w-52 h-72 bg-gray-200 animate-pulse rounded-xl" />
        </div>
      </div>
    </div>

    <template v-else>
      <div v-for="seccion in secciones" :key="seccion.nombre" class="mb-20 md:mb-28">
        <div class="flex items-center justify-between mb-5">
          <h2 class="font-serif text-xl md:text-2xl text-text-primary">
            {{ seccion.nombre }}
          </h2>
        </div>

        <div class="relative group">
          <button
            class="absolute -left-8 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-white/90 shadow-md flex items-center justify-center text-text-primary opacity-0 group-hover:opacity-100 transition-opacity duration-300 hover:bg-white"
            :class="{ 'opacity-100': (scrollLefts[seccion.nombre] ?? 0) > 0 }"
            @click="scrollSection(seccion.nombre, -1)"
            aria-label="Anterior"
          >
            <Icon icon="mdi:chevron-left" class="text-xl" />
          </button>

          <div
            :ref="(el) => setSectionRef(seccion.nombre, el as HTMLElement | null)"
            class="flex gap-5 overflow-x-auto scroll-smooth pb-2 scrollbar-hide px-6"
            @scroll="onScroll(seccion.nombre)"
          >
            <ProductCard
              v-for="item in seccion.ramos"
              :key="item.id"
              :producto="item"
              :badge="seccion.badge || undefined"
              @add-to-cart="onAddToCart"
            />
            <div
              v-if="seccion.ramos.length === 0"
              class="flex-1 flex items-center justify-center py-16 text-text-primary/50 text-sm"
            >
              No hay productos disponibles en esta sección.
            </div>
          </div>

          <button
            class="absolute -right-8 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-white/90 shadow-md flex items-center justify-center text-text-primary opacity-0 group-hover:opacity-100 transition-opacity duration-300 hover:bg-white"
            @click="scrollSection(seccion.nombre, 1)"
            aria-label="Siguiente"
          >
            <Icon icon="mdi:chevron-right" class="text-xl" />
          </button>
        </div>
      </div>

      <p v-if="mensaje && secciones.length === 0" class="text-center text-text-primary/60 text-sm mt-8">
        {{ mensaje }}
      </p>
    </template>
  </section>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { CategoriaSeccion } from '../../models/catalogo.model'
import { catalogoService } from '../../services/catalogo.service'
import ProductCard from './ProductCard.vue'

const secciones = ref<CategoriaSeccion[]>([])
const mensaje = ref('')
const loading = ref(true)

const sectionRefs = reactive<Record<string, HTMLElement | null>>({})
const scrollLefts = reactive<Record<string, number>>({})

function setSectionRef(nombre: string, el: HTMLElement | null) {
  sectionRefs[nombre] = el
}

function scrollSection(nombre: string, direction: -1 | 1) {
  const el = sectionRefs[nombre]
  if (!el) return
  const cardWidth = 280
  el.scrollBy({ left: direction * cardWidth, behavior: 'smooth' })
}

function onScroll(nombre: string) {
  const el = sectionRefs[nombre]
  if (el) {
    scrollLefts[nombre] = el.scrollLeft
  }
}

import { useCartStore } from '../../stores/cart.store'

const cartStore = useCartStore()

function onAddToCart(producto: { id: number; nombre: string; foto: string; precio: number }) {
  cartStore.agregarItem(producto)
}

onMounted(async () => {
  try {
    const response = await catalogoService.obtenerCatalogo()
    secciones.value = response.secciones ?? []
    mensaje.value = response.mensaje ?? ''
  } catch (e) {
    console.error('Error al cargar catálogo:', e)
    mensaje.value = 'Error al cargar el catálogo. Intenta de nuevo más tarde.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
