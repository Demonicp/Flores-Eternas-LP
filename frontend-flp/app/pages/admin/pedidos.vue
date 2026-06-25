<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { usePedidosStore } from '~/stores/pedidos.store'
import { useToast } from '~/composables/useToast'
import { formatoPrecio } from '~/utils/formatters'
import { useRouter } from 'vue-router'

definePageMeta({ layout: 'admin' })

const store = usePedidosStore()
const toast = useToast()
const router = useRouter()
const estadoCambiando = ref<number | null>(null)
const filtro = ref<'todos' | 'pendientes' | 'entregados' | 'proximos' | 'valor'>('todos')

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

// Navigate to order detail page
function verDetalle(id: number) {
  router.push(`/admin/pedidos/${id}`)
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
    case 'pendientes':
      filtered = filtered.filter(p => p.estado !== 'ENTREGADO' && p.estado !== 'CANCELADO')
      break
    case 'entregados':
      filtered = filtered.filter(p => p.estado === 'ENTREGADO')
      break
    case 'proximos':
      filtered = filtered.filter(p => {
        if (!p.fechaEntrega) return false
        const hoy = new Date()
        const entrega = new Date(p.fechaEntrega)
        const diff = Math.ceil((entrega.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24))
        return diff <= 3 && diff >= 0
      })
      break
    case 'valor':
      filtered = filtered.sort((a, b) => b.total - a.total)
      break
    default:
      break
  }

  return filtered.sort((a, b) => {
    if (!a.fechaCreacion) return 1
    if (!b.fechaCreacion) return -1
    return new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
  })
})

// Load orders on component mount
onMounted(() => {
  store.cargarPedidos()
})
</script>

<template>
  <div class="min-h-screen">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <h1 class="text-2xl font-serif text-text-primary font-medium mb-4">
        Gestión de Pedidos
      </h1>

      <!-- Filter buttons -->
      <div class="mb-6 flex flex-wrap gap-2">
        <button
          @click="() => (filtro = 'todos', store.cargarPedidos())"
          :class="filtro === 'todos' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Todos
        </button>
        <button
          @click="() => (filtro = 'pendientes', store.cargarPedidos())"
          :class="filtro === 'pendientes' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Pendientes
        </button>
        
        <button
          @click="() => (filtro = 'entregados', store.cargarPedidos())"
          :class="filtro === 'entregados' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Entregados
        </button>
        
        <button
          @click="() => (filtro = 'proximos', store.cargarPedidos())"
          :class="filtro === 'proximos' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Próximos a entregar
        </button>
        
        <button
          @click="() => (filtro = 'valor', store.cargarPedidos())"
          :class="filtro === 'valor' ? 'bg-btn-primary text-btn-primary-text' : 'bg-gray-100 text-text-primary'"
          class="px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          Por valor
        </button>
      </div>

      <!-- Loading state -->
      <div v-if="store.loading" class="text-center py-12">
        <p class="text-text-primary/60">Cargando pedidos...</p>
      </div>

      <!-- Orders table -->
      <div v-else class="bg-bg-card rounded-xl overflow-hidden">
        <table v-if="pedidosFiltrados.length > 0" class="w-full text-sm text-text-primary">
          <thead>
            <tr class="text-left border-b border-border-soft bg-bg-card/80">
              <th class="p-3 font-medium">ID</th>
              <th class="p-3 font-medium">Cliente</th>
              <th class="p-3 font-medium hidden md:table-cell">Email</th>
              <th class="p-3 font-medium">Total</th>
              <th class="p-3 font-medium hidden md:table-cell">Pagado</th>
              <th class="p-3 font-medium hidden sm:table-cell">Tipo</th>
              <th class="p-3 font-medium hidden md:table-cell">Fecha Entrega</th>
              <th class="p-3 font-medium hidden md:table-cell">Fecha Creación</th>
              <th class="p-3 font-medium">Estado</th>
              <th class="p-3"></th>
              <th class="p-3"></th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="pedido in pedidosFiltrados"
              :key="pedido.id"
              class="border-b border-border-soft/50 hover:bg-bg-input/50 transition-colors"
            >
              <td class="p-3 font-mono text-xs">#{{ pedido.id }}</td>
              <td class="p-3">{{ pedido.nombreCliente || '—' }}</td>
              <td class="p-3 hidden md:table-cell">{{ pedido.emailCliente || '—' }}</td>
              <td class="p-3">${{ formatoPrecio(pedido.total) }}</td>
              <td class="p-3 hidden md:table-cell">
                <span
                  :class="pedido.montoPagado >= pedido.total ? 'text-green-600' : 'text-amber-600'"
                >
                  ${{ formatoPrecio(pedido.montoPagado) }}
                </span>
              </td>
              <td class="p-3 hidden sm:table-cell">{{ pedido.tipoPedido || '—' }}</td>
              <td class="p-3 hidden md:table-cell">
                {{ pedido.fechaEntrega ? formatearFecha(pedido.fechaEntrega) : '—' }}
              </td>
              <td class="p-3 hidden md:table-cell">
                {{ pedido.fechaCreacion ? formatearFechaHora(pedido.fechaCreacion) : '—' }}
              </td>
              <td class="p-3">
                <select
                  :value="pedido.estado"
                  @change="cambiarEstado(pedido.id, ($event.target as HTMLSelectElement).value)"
                  class="rounded-lg border border-border-soft bg-white px-2 py-1 text-xs font-medium focus:outline-none focus:ring-2 focus:ring-btn-primary cursor-pointer"
                  :class="colorEstado(pedido.estado)"
                >
                  <option value="EN_PROCESO">En proceso</option>
                  <option value="EN_PREPARACION">En preparación</option>
                  <option value="PENDIENTE_DE_ENTREGA">Pendiente de entrega</option>
                  <option value="ENTREGADO">Entregado</option>
                  <option value="CANCELADO">Cancelado</option>
                </select>
              </td>
              <td class="p-3">
                <button
                  @click="verDetalle(pedido.id)"
                  class="text-btn-primary hover:underline text-xs"
                >Ver detalle</button>
              </td>
              <td class="p-3 flex gap-1 items-center">
                <span v-if="estadoCambiando === pedido.id" class="text-xs text-text-primary/50">
                  Actualizando...
                </span>
                <button
                  v-if="pedido.pagoToken && pedido.montoPendiente > 0"
                  @click="copiarLink(pedido.pagoToken)"
                  class="px-2 py-1 text-xs bg-btn-primary text-btn-primary-text rounded hover:opacity-80"
                  title="Copiar link de pago"
                >
                  <Icon icon="mdi:link-variant" class="text-sm" />
                </button>
              </td>
            </tr>
          </tbody>
        </table>

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