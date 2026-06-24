<template>
  <Teleport to="body">
    <Transition name="search-fade">
      <div v-if="abierto" class="fixed inset-0 z-[60] flex flex-col">
        <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="cerrar" />
        <div class="relative z-10 w-full max-w-2xl mx-auto mt-24 px-4">
          <div class="bg-white rounded-2xl shadow-2xl border border-border-soft overflow-hidden">
            <!-- Search input -->
            <div class="flex items-center gap-3 px-5 py-4 border-b border-border-soft/60">
              <Icon icon="mdi:magnify" class="text-2xl text-text-primary/40" />
              <input
                ref="inputRef"
                v-model="query"
                type="text"
                placeholder="Buscar ramos, flores, ocasiones..."
                class="flex-1 text-[16px] bg-transparent outline-none text-text-primary placeholder:text-text-primary/30"
              />
              <button v-if="query" class="text-text-primary/30 hover:text-text-primary/60 transition-colors p-1" @click="query = ''">
                <Icon icon="mdi:close-circle" class="text-xl" />
              </button>
              <button class="text-text-primary/30 hover:text-text-primary/60 transition-colors p-1" @click="cerrar">
                <Icon icon="mdi:close" class="text-xl" />
              </button>
            </div>

            <!-- Results -->
            <div class="max-h-[60vh] overflow-y-auto">
              <div v-if="cargando" class="flex items-center justify-center py-12">
                <Icon icon="mdi:loading" class="text-3xl text-btn-primary animate-spin" />
              </div>

              <div v-else-if="resultados.length === 0 && query.length >= 2" class="flex flex-col items-center py-12 text-text-primary/40">
                <Icon icon="mdi:search-off" class="text-5xl mb-3" />
                <p class="text-sm">No encontramos resultados para "{{ query }}"</p>
              </div>

              <div v-else-if="query.length < 2 && query.length > 0" class="flex flex-col items-center py-12 text-text-primary/30">
                <Icon icon="mdi:keyboard-outline" class="text-4xl mb-2" />
                <p class="text-sm">Escribe al menos 2 caracteres para buscar</p>
              </div>

              <div v-else-if="query.length === 0" class="flex flex-col items-center py-12 text-text-primary/30">
                <Icon icon="mdi:flower-tulip-outline" class="text-5xl mb-3" />
                <p class="text-sm">Busca tu ramo ideal</p>
              </div>

              <div v-else class="divide-y divide-border-soft/40">
                <button
                  v-for="r in resultados"
                  :key="r.id"
                  class="w-full flex items-center gap-4 px-5 py-3.5 hover:bg-bg-card/40 transition-colors text-left"
                  @click="seleccionar(r)"
                >
                  <div class="w-14 h-14 rounded-xl bg-bg-card flex items-center justify-center overflow-hidden flex-shrink-0">
                    <img v-if="r.foto" :src="r.foto" :alt="r.nombre" class="w-full h-full object-cover" />
                    <Icon v-else icon="mdi:flower-tulip-outline" class="text-2xl text-text-primary/30" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-[15px] font-medium text-text-primary truncate">{{ r.nombre }}</p>
                    <p v-if="r.descripcionCorta" class="text-xs text-text-primary/50 truncate mt-0.5">{{ r.descripcionCorta }}</p>
                  </div>
                  <div class="text-sm font-semibold text-text-primary flex-shrink-0">
                    ${{ formatoPrecio(r.precio) }}
                  </div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { catalogoService } from '../../services/catalogo.service'
import type { RamoResumen } from '../../models/catalogo.model'

const abierto = defineModel<boolean>('abierto', { default: false })
const inputRef = ref<HTMLInputElement | null>(null)
const query = ref('')
const todos = ref<RamoResumen[]>([])
const cargando = ref(false)
const resultados = ref<RamoResumen[]>([])

watch(abierto, async (val) => {
  if (val) {
    query.value = ''
    resultados.value = []
    cargando.value = true
    try {
      const [predefinidos, temporada] = await Promise.all([
        catalogoService.listarPredefinidos(),
        catalogoService.listarTemporada(),
      ])
      todos.value = [...predefinidos, ...temporada]
    } catch {
      todos.value = []
    } finally {
      cargando.value = false
    }
    await nextTick()
    inputRef.value?.focus()
  }
})

watch(query, (val) => {
  const q = val.trim().toLowerCase()
  if (q.length < 2) {
    resultados.value = []
    return
  }
  resultados.value = todos.value.filter(r =>
    r.nombre.toLowerCase().includes(q) ||
    (r.descripcionCorta && r.descripcionCorta.toLowerCase().includes(q))
  )
})

function seleccionar(r: RamoResumen) {
  cerrar()
  navigateTo('/')
}

function cerrar() {
  abierto.value = false
}

function formatoPrecio(valor: number): string {
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(valor)
}
</script>

<style scoped>
.search-fade-enter-active,
.search-fade-leave-active {
  transition: opacity 0.25s ease;
}
.search-fade-enter-from,
.search-fade-leave-to {
  opacity: 0;
}
.search-fade-enter-active > div:last-child > div,
.search-fade-leave-active > div:last-child > div {
  transition: transform 0.25s ease, opacity 0.25s ease;
}
.search-fade-enter-from > div:last-child > div,
.search-fade-leave-to > div:last-child > div {
  transform: translateY(-20px);
  opacity: 0;
}
</style>
