<template>
  <div class="min-h-screen">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <h1 class="text-2xl font-serif text-text-primary font-medium mb-4">Gestión de Pedidos</h1>

      <div v-if="store.loading" class="text-center py-12">
        <p class="text-text-primary/60">Cargando pedidos...</p>
      </div>

      <div v-else class="bg-bg-card rounded-xl overflow-hidden">
        <table v-if="store.pedidos.length > 0" class="w-full text-sm text-text-primary">
          <thead>
            <tr class="text-left border-b border-border-soft bg-bg-card/80">
              <th class="p-3 font-medium">ID</th>
              <th class="p-3 font-medium">Cliente</th>
              <th class="p-3 font-medium hidden md:table-cell">Email</th>
              <th class="p-3 font-medium">Total</th>
              <th class="p-3 font-medium hidden md:table-cell">Pagado</th>
              <th class="p-3 font-medium hidden sm:table-cell">Tipo</th>
              <th class="p-3 font-medium hidden md:table-cell">Fecha Entrega</th>
              <th class="p-3 font-medium">Estado</th>
              <th class="p-3"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="pedido in store.pedidos" :key="pedido.id" class="border-b border-border-soft/50 hover:bg-bg-input/50 transition-colors">
              <td class="p-3 font-mono text-xs">#{{ pedido.id }}</td>
              <td class="p-3">{{ pedido.nombreCliente || '—' }}</td>
              <td class="p-3 hidden md:table-cell">{{ pedido.emailCliente || '—' }}</td>
              <td class="p-3">${{ formatoPrecio(pedido.total) }}</td>
              <td class="p-3 hidden md:table-cell">
                <span :class="pedido.montoPagado >= pedido.total ? 'text-green-600' : 'text-amber-600'">
                  ${{ formatoPrecio(pedido.montoPagado) }}
                </span>
              </td>
              <td class="p-3 hidden sm:table-cell">{{ pedido.tipoPedido || '—' }}</td>
              <td class="p-3 hidden md:table-cell">{{ pedido.fechaEntrega ? formatearFecha(pedido.fechaEntrega) : '—' }}</td>
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
              <td class="p-3 flex gap-1 items-center">
                <span v-if="estadoCambiando === pedido.id" class="text-xs text-text-primary/50">Actualizando...</span>
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
        <p v-else class="text-sm text-text-primary text-center py-8">
          No hay pedidos registrados aún.
        </p>
      </div>

      <p v-if="store.error" class="text-red-500 text-sm mt-4">{{ store.error }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin' })
import { onMounted, ref } from 'vue'
import { usePedidosStore } from '~/stores/pedidos.store'
import { useToast } from '~/composables/useToast'
import { formatoPrecio } from '~/utils/formatters'

const store = usePedidosStore()
const toast = useToast()
const estadoCambiando = ref<number | null>(null)

function formatearFecha(fecha: string): string {
  const d = new Date(fecha)
  return d.toLocaleDateString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

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

function copiarLink(token: string) {
  const link = window.location.origin + '/pago/personalizado/' + token
  navigator.clipboard.writeText(link).then(() => {
    toast.success('Link de pago copiado al portapapeles')
  }).catch(() => {
    toast.warning('No se pudo copiar automáticamente. Link: ' + link)
  })
}

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

onMounted(() => {
  store.cargarPedidos()
})
</script>