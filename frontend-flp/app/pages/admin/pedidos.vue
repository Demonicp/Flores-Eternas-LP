<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { usePedidosStore } from '~/stores/pedidos.store'
import { useToast } from '~/composables/useToast'
import { formatoPrecio } from '~/utils/formatters'
import { localService } from '~/services/local.service'
import type { Local } from '~/models/local.model'

definePageMeta({ layout: 'admin' })

const store = usePedidosStore()
const toast = useToast()
const estadoCambiando = ref<number | null>(null)
const expandedId = ref<number | null>(null)
const filtro = ref<'todos' | 'proceso' | 'proximos' | 'pendienteEntrega' | 'entregados' | 'valor'>('todos')
const ordenValor = ref<'asc' | 'desc' | null>(null)
const vistaLista = ref<Record<string, boolean>>({})

const locales = ref<Local[]>([])

/**
 * Devuelve el local cuyo punto de retiro coincide con la direccion del pedido.
 * Si coincide, el pedido fue solicitado para retiro en ese local.
 *
 * @author santiago (sesion 05/08/2026 - retiro en local)
 */
function localEntrega(direccion: string | undefined): Local | null {
  if (!direccion) return null
  return locales.value.find(l => l.direccion.trim() === direccion.trim()) || null
}

onMounted(async () => {
  store.cargarPedidos()
  try {
    locales.value = await localService.listarActivos()
  } catch {
    locales.value = []
  }
})

function toggleExpand(id: number) {
  expandedId.value = expandedId.value === id ? null : id
}

function parseAdiciones(json: string): Array<{ nombre: string; cantidad: number; precio: number }> {
  if (!json) return []
  try { return JSON.parse(json) } catch { return [] }
}

/**
 * Adiciones únicas de un pedido: las flores de un ramo personalizado comparten
 * el mismo adicionesJson, por lo que se deduplican para no repetirlas.
 *
 * @author santiago
 */
function adicionesUnicas(pedido: any): Array<{ nombre: string; cantidad: number; precio: number }> {
  const mapa = new Map<string, { nombre: string; cantidad: number; precio: number }>()
  for (const item of (pedido?.items || [])) {
    for (const adicion of parseAdiciones(item.adicionesJson)) {
      const clave = adicion.nombre + '|' + adicion.cantidad + '|' + adicion.precio
      if (!mapa.has(clave)) mapa.set(clave, adicion)
    }
  }
  return Array.from(mapa.values())
}

// Formatted display functions
function formatearFecha(fecha: string): string {
  const d = new Date(fecha)
  return d.toLocaleDateString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function formatearFechaHora(fecha: string): string {
  const d = new Date(fecha)
  return d.toLocaleString('es-CO', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * Etiqueta legible del tipo de pedido.
 * CATALOGO (y RAPIDO como valor historico) se muestran como "Catálogo";
 * PERSONALIZADO como "Personalizado".
 *
 * @author santiago
 */
function etiquetaTipoPedido(tipo: string | null | undefined): string {
  if (!tipo) return '—'
  if (tipo.toUpperCase() === 'PERSONALIZADO') return 'Personalizado'
  if (tipo.toUpperCase() === 'CATALOGO' || tipo.toUpperCase() === 'RAPIDO') return 'Catálogo'
  return tipo
}

// Color coding for status badges
function colorEstado(estado: string): string {
  switch (estado) {
    case 'EN_PROCESO': return 'text-purple-700 bg-purple-50 border-purple-200'
    case 'EN_PREPARACION': return 'text-amber-700 bg-amber-50 border-amber-200'
    case 'PENDIENTE_DE_ENTREGA': return 'text-blue-700 bg-blue-50 border-blue-200'
    case 'ENTREGADO': return 'text-green-700 bg-green-50 border-green-200'
    case 'CANCELADO': return 'text-red-700 bg-red-50 border-red-200'
    default: return ''
  }
}

// Payment link copy functionality
function copiarLink(token: string) {
  const link = window.location.origin + '/pago/personalizado/' + token
  navigator.clipboard.writeText(link).then(() => {
    toast.success('Link de pago copiado al portapapeles')
  }).catch(() => {
    toast.warning('No se pudo copiar automáticamente. Link: ' + link)
  })
}

// Change order status with error handling
async function cambiarEstado(id: number, nuevoEstado: string) {
  estadoCambiando.value = id
  try {
    await store.cambiarEstado(id, nuevoEstado)
    toast.success('Estado actualizado a ' + nuevoEstado.replace(/_/g, ' ').toLowerCase())
  } catch {
    toast.error('Error al actualizar el estado')
  } finally {
    estadoCambiando.value = null
  }
}

// Filter and sort orders
const pedidosFiltrados = computed(() => {
  let filtered = [...store.pedidos]

  switch (filtro.value) {
    case 'proceso':
      filtered = filtered.filter(p => p.estado === 'EN_PROCESO' || p.estado === 'EN_PREPARACION')
      break
    case 'proximos':
      filtered = filtered.filter(p => {
        if (!p.fechaEntrega) return false
        if (p.estado === 'ENTREGADO' || p.estado === 'CANCELADO') return false
        return true
      })
      break
    case 'pendienteEntrega':
      filtered = filtered.filter(p => p.estado === 'PENDIENTE_DE_ENTREGA')
      break
    case 'entregados':
      filtered = filtered.filter(p => p.estado === 'ENTREGADO')
      break
    case 'valor':
      filtered = filtered.sort((a, b) =>
        ordenValor.value === 'asc' ? a.total - b.total : b.total - a.total
      )
      break
    default:
      break
  }

  return filtered.sort((a, b) => {
    if (filtro.value === 'proximos') {
      if (!a.fechaEntrega) return 1
      if (!b.fechaEntrega) return -1
      return new Date(a.fechaEntrega).getTime() - new Date(b.fechaEntrega).getTime()
    }
    if (!a.fechaCreacion) return 1
    if (!b.fechaCreacion) return -1
    return new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
  })
})

// Load orders on component mount
// (carga de pedidos y locales movida al onMounted del inicio del script)
</script>

<template>
  <div class="min-h-screen">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <h1 class="text-2xl font-serif text-text-primary font-medium mb-4">
        Gestión de Pedidos
      </h1>

      <div class="mb-4 flex justify-end">
        <button
          @click="vistaLista[filtro] = !vistaLista[filtro]"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium bg-text-primary text-white hover:opacity-90 transition"
          :title="vistaLista[filtro] ? 'Cambiar a cuadrícula' : 'Cambiar a lista'"
        >
          <Icon :icon="vistaLista[filtro] ? 'mdi:view-grid-outline' : 'mdi:view-agenda-outline'" class="text-base" />
          {{ vistaLista[filtro] ? 'Cuadrícula' : 'Lista' }}
        </button>
      </div>

      <!-- Filter buttons -->
      <div class="mb-6 flex flex-wrap gap-2">
        <button
          @click="() => (filtro = 'todos', ordenValor = null, store.cargarPedidos())"
          :class="filtro === 'todos' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Todos
        </button>
        <button
          @click="() => (filtro = 'proceso', ordenValor = null, store.cargarPedidos())"
          :class="filtro === 'proceso' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          En proceso
        </button>

        <button
          @click="() => (filtro = 'proximos', ordenValor = null, store.cargarPedidos())"
          :class="filtro === 'proximos' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Próximos a entregar
        </button>

        <button
          @click="() => (filtro = 'pendienteEntrega', ordenValor = null, store.cargarPedidos())"
          :class="filtro === 'pendienteEntrega' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Pendiente entrega
        </button>

        <button
          @click="() => (filtro = 'entregados', ordenValor = null, store.cargarPedidos())"
          :class="filtro === 'entregados' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Entregados
        </button>

        <button
          @click="() => (filtro = 'valor', ordenValor = 'desc', store.cargarPedidos())"
          :class="filtro === 'valor' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Por valor
        </button>

        <div v-if="filtro === 'valor'" class="flex items-center gap-1">
          <select
            v-model="ordenValor"
            class="px-3 py-2 rounded-lg text-sm font-medium border border-border-soft bg-white text-text-primary cursor-pointer outline-none"
          >
            <option value="desc">Mayor a menor</option>
            <option value="asc">Menor a mayor</option>
          </select>
        </div>
      </div>

      <!-- Loading state -->
      <div v-if="store.loading" class="text-center py-12">
        <p class="text-text-primary/60">Cargando pedidos...</p>
      </div>

      <!-- Orders grid -->
      <div v-else>
        <div v-if="pedidosFiltrados.length > 0" :class="vistaLista[filtro] ? 'flex flex-col gap-2' : 'grid grid-cols-1 md:grid-cols-2 gap-4'">
          <div
            v-for="pedido in pedidosFiltrados"
            :key="pedido.id"
            class="bg-white border border-border-soft rounded-xl p-4 shadow-sm"
            :class="vistaLista[filtro] ? 'flex items-center gap-4 flex-wrap' : 'flex flex-col gap-3'"
          >
            <!-- Card header -->
            <div class="flex items-start justify-between gap-2">
              <div class="min-w-0">
                <p class="font-mono text-xs text-text-primary/70">#{{ pedido.id }}</p>
                <p class="text-sm font-semibold text-text-primary truncate">{{ pedido.nombreCliente || '—' }}</p>
                <p class="text-xs text-text-primary/60">{{ pedido.fechaCreacion ? formatearFechaHora(pedido.fechaCreacion) : '—' }}</p>
              </div>
              <select
                :value="pedido.estado"
                @change="cambiarEstado(pedido.id, ($event.target as HTMLSelectElement).value)"
                class="rounded-lg border border-border-soft bg-white px-2 py-1 text-xs font-medium focus:outline-none focus:ring-2 focus:ring-btn-primary cursor-pointer flex-shrink-0"
                :class="colorEstado(pedido.estado)"
              >
                <option value="EN_PROCESO">En proceso</option>
                <option value="EN_PREPARACION">En preparación</option>
                <option value="PENDIENTE_DE_ENTREGA">Pendiente de entrega</option>
                <option value="ENTREGADO">Entregado</option>
                <option value="CANCELADO">Cancelado</option>
              </select>
            </div>

            <!-- Cliente / tipo / entrega -->
            <div class="text-xs text-text-primary/80 space-y-1">
              <div class="flex justify-between gap-2">
                <span class="text-text-primary/60 flex-shrink-0">Tipo</span>
                <span class="text-right">{{ etiquetaTipoPedido(pedido.tipoPedido) }}</span>
              </div>
              <div class="flex justify-between gap-2">
                <span class="text-text-primary/60 flex-shrink-0">Email</span>
                <span class="text-right truncate">{{ pedido.emailCliente || '—' }}</span>
              </div>
              <div class="flex justify-between gap-2">
                <span class="text-text-primary/60 flex-shrink-0">Entrega</span>
                <span class="text-right">{{ pedido.fechaEntrega ? formatearFecha(pedido.fechaEntrega) : '—' }}</span>
              </div>
            </div>

            <!-- Financial summary -->
            <div class="grid grid-cols-3 gap-2">
              <div class="bg-bg-card rounded-lg p-2 border border-border-soft text-center">
                <p class="text-[11px] text-text-primary/60">Total</p>
                <p class="text-sm font-bold text-text-primary">${{ formatoPrecio(pedido.total) }}</p>
              </div>
              <div class="bg-bg-card rounded-lg p-2 border border-border-soft text-center">
                <p class="text-[11px] text-text-primary/60">Pagado</p>
                <p class="text-sm font-bold text-text-primary">${{ formatoPrecio(pedido.montoPagado) }}</p>
              </div>
              <div class="bg-bg-card rounded-lg p-2 border border-border-soft text-center">
                <p class="text-[11px] text-text-primary/60">Pendiente</p>
                <p class="text-sm font-bold text-text-primary">${{ formatoPrecio(pedido.montoPendiente) }}</p>
              </div>
            </div>

            <!-- Actions -->
            <div class="flex items-center gap-2 pt-2 border-t border-border-soft/50 mt-auto" :class="vistaLista[filtro] ? 'ml-auto flex-shrink-0 border-t-0 pt-0' : 'justify-between'">
              <button
                v-if="pedido.pagoToken && pedido.montoPendiente > 0"
                @click="copiarLink(pedido.pagoToken)"
                class="flex items-center gap-1 px-3 py-1.5 rounded-lg text-xs font-medium bg-btn-primary text-btn-primary-text hover:opacity-80 transition"
                title="Copiar link de pago"
              >
                <Icon icon="mdi:link-variant" class="text-sm" />
                Copiar link
              </button>
              <span v-else></span>

              <button
                @click="toggleExpand(pedido.id)"
                class="flex items-center gap-1 px-3 py-1.5 rounded-lg text-xs font-medium bg-text-primary text-white hover:opacity-90 transition"
              >
                <Icon :icon="expandedId === pedido.id ? 'mdi:chevron-up' : 'mdi:chevron-down'" class="text-sm" />
                {{ expandedId === pedido.id ? 'Ver menos' : 'Ver detalles' }}
              </button>
            </div>

            <span v-if="estadoCambiando === pedido.id" class="text-xs text-text-primary/50 text-center">
              Actualizando...
            </span>

            <!-- Expandable detail -->
            <div v-if="expandedId === pedido.id" class="border-t border-border-soft pt-3 space-y-4" :class="vistaLista[filtro] ? 'w-full' : ''">
              <!-- Info grid -->
              <div class="grid grid-cols-1 gap-4">
                <div class="space-y-2">
                  <h3 class="text-sm font-semibold text-text-primary border-b border-border-soft pb-1">Información del pedido</h3>
                  <div class="text-xs space-y-1">
                    <div class="flex justify-between"><span class="text-text-primary/60">ID</span><span class="font-mono">#{{ pedido.id }}</span></div>
                    <div class="flex justify-between"><span class="text-text-primary/60">Tipo</span><span>{{ etiquetaTipoPedido(pedido.tipoPedido) }}</span></div>
                    <div class="flex justify-between"><span class="text-text-primary/60">Creado</span><span>{{ formatearFechaHora(pedido.fechaCreacion) }}</span></div>
                    <div class="flex justify-between"><span class="text-text-primary/60">Entrega</span><span>{{ pedido.fechaEntrega ? formatearFecha(pedido.fechaEntrega) : '—' }}</span></div>
                    <div class="flex justify-between"><span class="text-text-primary/60">Mensaje</span><span class="text-right">{{ pedido.mensaje || '—' }}</span></div>
                  </div>
                </div>
                <div class="space-y-2">
                  <h3 class="text-sm font-semibold text-text-primary border-b border-border-soft pb-1">Datos del cliente</h3>
                  <div class="text-xs space-y-1">
                    <div class="flex justify-between"><span class="text-text-primary/60">Nombre</span><span>{{ pedido.nombreCliente || '—' }}</span></div>
                    <div class="flex justify-between"><span class="text-text-primary/60">Email</span><span class="text-right">{{ pedido.emailCliente || '—' }}</span></div>
                    <div class="flex justify-between gap-2"><span class="text-text-primary/60 flex-shrink-0">Dirección</span><span class="text-right break-words">{{ pedido.direccionEntrega || '—' }}</span></div>
                    <div v-if="localEntrega(pedido.direccionEntrega)" class="flex justify-between">
                      <span class="text-text-primary/60">Entrega</span>
                      <span class="text-green-700 font-medium text-right">Retiro en local: {{ localEntrega(pedido.direccionEntrega)!.nombreLocal }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Products table -->
              <div>
                <h3 class="text-sm font-semibold text-text-primary border-b border-border-soft pb-1 mb-2">Productos</h3>
                <div class="bg-bg-card rounded-lg overflow-x-auto">
                  <table v-if="pedido.items && pedido.items.length > 0" class="w-full text-xs text-text-primary min-w-[450px]">
                    <thead>
                      <tr class="text-left border-b border-border-soft bg-bg-card/80">
                        <th class="p-2 font-medium">Producto</th>
                        <th class="p-2 font-medium text-center">Cant</th>
                        <th class="p-2 font-medium text-right">Precio</th>
                        <th class="p-2 font-medium text-right">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(item, idx) in pedido.items" :key="idx" class="border-b border-border-soft/50">
                        <td class="p-2">
                          <div>{{ item.nombreRamo || '—' }}</div>
                          <div v-if="item.flores && item.flores.length > 0" class="mt-1 space-y-0.5">
                            <div v-for="(flor, fidx) in item.flores" :key="fidx" class="text-xs text-text-primary/60 pl-2">
                              └ {{ flor.cantidad }}x {{ flor.tipoFlor }}<span v-if="flor.color"> ({{ flor.color }})</span>
                            </div>
                          </div>
                          <div v-else-if="item.tipoFlor" class="mt-1 text-xs text-text-primary/60 pl-2">
                            └ {{ item.cantidad }}x {{ item.tipoFlor }}<span v-if="item.colorFlor"> ({{ item.colorFlor }})</span>
                          </div>
                        </td>
                        <td class="p-2 text-center">{{ item.cantidad || '—' }}</td>
                        <td class="p-2 text-right">${{ formatoPrecio(item.precioUnitario) }}</td>
                        <td class="p-2 text-right font-medium">${{ formatoPrecio((item.precioUnitario || 0) * (item.cantidad || 0)) }}</td>
                      </tr>
                    </tbody>
                  </table>
                  <p v-else class="text-xs text-text-primary/60 text-center py-4">No hay productos registrados.</p>
                </div>
              </div>

              <!-- Adiciones -->
              <div v-if="adicionesUnicas(pedido).length > 0">
                <h3 class="text-sm font-semibold text-text-primary border-b border-border-soft pb-1 mb-2">Agregados</h3>
                <div class="bg-bg-card rounded-lg overflow-x-auto">
                  <table class="w-full text-xs text-text-primary min-w-[300px]">
                    <thead>
                      <tr class="text-left border-b border-border-soft bg-bg-card/80">
                        <th class="p-2 font-medium">Nombre</th>
                        <th class="p-2 font-medium text-center">Cant</th>
                        <th class="p-2 font-medium text-right">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(adicion, aidx) in adicionesUnicas(pedido)" :key="'a-' + aidx" class="border-b border-border-soft/50">
                        <td class="p-2">{{ adicion.nombre }}</td>
                        <td class="p-2 text-center">{{ adicion.cantidad }}</td>
                        <td class="p-2 text-right">${{ formatoPrecio(adicion.precio * adicion.cantidad) }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              </div>
          </div>
        </div>

        <!-- Empty state -->
        <p v-else class="text-sm text-text-primary text-center py-8">
          No hay pedidos.
        </p>
      </div>

      <!-- Error message -->
      <p v-if="store.error" class="text-red-500 text-sm mt-4">{{ store.error }}</p>
    </section>
  </div>
</template>