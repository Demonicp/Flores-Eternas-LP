<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { apiClient } from '~/services/api-client'
import { formatoPrecio } from '~/utils/formatters'
import { useToast } from '~/composables/useToast'

definePageMeta({ layout: 'admin' })

const route = useRoute()
const router = useRouter()
const toast = useToast()

const pedido = ref<any>(null)
const cargando = ref(true)
const error = ref('')
const estadoCambiando = ref(false)

const id = route.params.id as string

function formatearFecha(fecha: string): string {
  if (!fecha) return '—'
  const d = new Date(fecha)
  return d.toLocaleDateString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function formatearFechaHora(fecha: string): string {
  if (!fecha) return '—'
  const d = new Date(fecha)
  return d.toLocaleString('es-CO', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
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

function parseAdiciones(json: string): Array<{ nombre: string; cantidad: number; precio: number }> {
  if (!json) return []
  try { return JSON.parse(json) } catch { return [] }
}

async function cargarPedido() {
  cargando.value = true
  error.value = ''
  try {
    pedido.value = await apiClient.get(`/api/admin/pedidos/${id}`)
  } catch {
    error.value = 'No se pudo cargar el pedido.'
  } finally {
    cargando.value = false
  }
}

async function cambiarEstado(nuevoEstado: string) {
  estadoCambiando.value = true
  try {
    await apiClient.put(`/api/admin/pedidos/${id}/estado`, { estado: nuevoEstado })
    pedido.value.estado = nuevoEstado
    toast.success('Estado actualizado a ' + nuevoEstado.replace(/_/g, ' ').toLowerCase())
  } catch {
    toast.error('Error al actualizar el estado')
  } finally {
    estadoCambiando.value = false
  }
}

function copiarLink(token: string) {
  const link = window.location.origin + '/pago/personalizado/' + token
  navigator.clipboard.writeText(link).then(() => {
    toast.success('Link de pago copiado')
  }).catch(() => {
    toast.warning('Link: ' + link)
  })
}

onMounted(cargarPedido)
</script>

<template>
  <div class="min-h-screen">
    <section class="bg-white rounded-xl p-6 shadow-sm border border-border-soft">
      <div class="flex items-center gap-4 mb-6">
        <button @click="router.push('/admin/pedidos')"
          class="text-text-primary/60 hover:text-text-primary transition">
          <Icon icon="mdi:arrow-left" class="text-xl" />
        </button>
        <h1 class="text-2xl font-serif text-text-primary font-medium">
          Pedido #{{ id }}
        </h1>
      </div>

      <div v-if="cargando" class="text-center py-12">
        <p class="text-text-primary/60">Cargando pedido...</p>
      </div>

      <div v-else-if="error" class="text-center py-12">
        <div class="flex flex-col items-center gap-3">
          <div class="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center">
            <Icon icon="mdi:alert-circle" class="text-2xl text-red-500" />
          </div>
          <p class="text-red-500 font-medium">{{ error }}</p>
          <button @click="cargarPedido" class="text-btn-primary hover:underline text-sm">
            Reintentar
          </button>
          <button @click="router.push('/admin/pedidos')"
            class="text-text-primary/60 hover:text-text-primary text-sm">
            Volver a pedidos
          </button>
        </div>
      </div>

      <div v-else class="text-center py-12">
        <div class="flex flex-col items-center gap-3">
          <div class="w-12 h-12 rounded-full bg-gray-100 flex items-center justify-center">
            <Icon icon="mdi:help-circle" class="text-2xl text-gray-400" />
          </div>
          <p class="text-text-primary/60">No se encontró el pedido #{{ id }}</p>
          <button @click="cargarPedido" class="text-btn-primary hover:underline text-sm">
            Reintentar
          </button>
          <button @click="router.push('/admin/pedidos')"
            class="text-text-primary/60 hover:text-text-primary text-sm">
            Volver a pedidos
          </button>
        </div>
      </div>

      <template v-else-if="pedido">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div class="space-y-3">
            <h2 class="text-lg font-medium text-text-primary border-b border-border-soft pb-2">
              Informacion del pedido
            </h2>
            <div class="flex justify-between text-sm">
              <span class="text-text-primary/60">ID</span>
              <span class="font-mono">#{{ pedido.id }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-text-primary/60">Tipo</span>
              <span>{{ pedido.tipoPedido || '—' }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-text-primary/60">Fecha creacion</span>
              <span>{{ formatearFechaHora(pedido.fechaCreacion) }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-text-primary/60">Fecha entrega</span>
              <span>{{ formatearFecha(pedido.fechaEntrega) }}</span>
            </div>
          </div>

          <div class="space-y-3">
            <h2 class="text-lg font-medium text-text-primary border-b border-border-soft pb-2">
              Datos del cliente
            </h2>
            <div class="flex justify-between text-sm">
              <span class="text-text-primary/60">Nombre</span>
              <span>{{ pedido.nombreCliente || '—' }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-text-primary/60">Email</span>
              <span>{{ pedido.emailCliente || '—' }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-text-primary/60">Direccion</span>
              <span>{{ pedido.direccionEntrega || '—' }}</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div class="bg-bg-card rounded-lg p-4 border border-border-soft text-center">
            <p class="text-xs text-text-primary/60 mb-1">Total</p>
            <p class="text-xl font-bold text-text-primary">${{ formatoPrecio(pedido.total) }}</p>
          </div>
          <div class="bg-bg-card rounded-lg p-4 border border-border-soft text-center">
            <p class="text-xs text-text-primary/60 mb-1">Pagado</p>
            <p class="text-xl font-bold" :class="pedido.montoPagado >= pedido.total ? 'text-green-600' : 'text-amber-600'">
              ${{ formatoPrecio(pedido.montoPagado) }}
            </p>
          </div>
          <div class="bg-bg-card rounded-lg p-4 border border-border-soft text-center">
            <p class="text-xs text-text-primary/60 mb-1">Saldo pendiente</p>
            <p class="text-xl font-bold" :class="pedido.montoPendiente > 0 ? 'text-red-600' : 'text-green-600'">
              ${{ formatoPrecio(pedido.montoPendiente) }}
            </p>
          </div>
        </div>

        <div class="mb-6">
          <h2 class="text-lg font-medium text-text-primary border-b border-border-soft pb-2 mb-4">
            Estado del pedido
          </h2>
          <div class="flex items-center gap-3">
            <select :value="pedido.estado"
              @change="cambiarEstado(($event.target as HTMLSelectElement).value)"
              :disabled="estadoCambiando"
              class="rounded-lg border border-border-soft bg-white px-3 py-2 text-sm font-medium focus:outline-none focus:ring-2 focus:ring-btn-primary cursor-pointer"
              :class="colorEstado(pedido.estado)">
              <option value="EN_PROCESO">En proceso</option>
              <option value="EN_PREPARACION">En preparacion</option>
              <option value="PENDIENTE_DE_ENTREGA">Pendiente de entrega</option>
              <option value="ENTREGADO">Entregado</option>
              <option value="CANCELADO">Cancelado</option>
            </select>
            <span v-if="estadoCambiando" class="text-xs text-text-primary/50">Actualizando...</span>
          </div>
        </div>

        <div class="mb-6">
          <h2 class="text-lg font-medium text-text-primary border-b border-border-soft pb-2 mb-4">
            Productos
          </h2>
          <div class="bg-bg-card rounded-xl overflow-x-auto overflow-y-auto max-h-[500px]">
            <table class="w-full text-sm text-text-primary min-w-[600px]">
              <thead>
                <tr class="text-left border-b border-border-soft bg-bg-card/80">
                  <th class="p-3 font-medium sticky top-0 bg-bg-card">Producto</th>
                  <th class="p-3 font-medium sticky top-0 bg-bg-card">Tipo flor</th>
                  <th class="p-3 font-medium sticky top-0 bg-bg-card">Color</th>
                  <th class="p-3 font-medium text-center sticky top-0 bg-bg-card">Cantidad</th>
                  <th class="p-3 font-medium text-right sticky top-0 bg-bg-card">Precio unitario</th>
                  <th class="p-3 font-medium text-right sticky top-0 bg-bg-card">Subtotal</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, idx) in pedido.items" :key="idx"
                  class="border-b border-border-soft/50">
                  <td class="p-3">{{ item.nombreRamo || '—' }}</td>
                  <td class="p-3">{{ item.tipoFlor || '—' }}</td>
                  <td class="p-3">{{ item.colorFlor || '—' }}</td>
                  <td class="p-3 text-center">{{ item.cantidad || '—' }}</td>
                  <td class="p-3 text-right">${{ formatoPrecio(item.precioUnitario) }}</td>
                  <td class="p-3 text-right font-medium">
                    ${{ formatoPrecio((item.precioUnitario || 0) * (item.cantidad || 0)) }}
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="!pedido.items || pedido.items.length === 0"
              class="text-sm text-text-primary/60 text-center py-8 flex flex-col items-center gap-2">
              <Icon icon="mdi:package-variant-closed" class="text-3xl text-text-primary/30" />
              <p>Este pedido no tiene productos registrados.</p>
              <p class="text-xs text-text-primary/40">Puede que haya sido creado antes de una actualización del sistema.</p>
            </div>
          </div>
        </div>

        <div v-if="pedido.items && pedido.items.some((i: any) => i.adicionesJson)" class="mb-6">
          <h2 class="text-lg font-medium text-text-primary border-b border-border-soft pb-2 mb-4">
            Agregados
          </h2>
          <div class="bg-bg-card rounded-xl overflow-hidden">
            <table class="w-full text-sm text-text-primary">
              <thead>
                <tr class="text-left border-b border-border-soft bg-bg-card/80">
                  <th class="p-3 font-medium">Nombre</th>
                  <th class="p-3 font-medium text-center">Cantidad</th>
                  <th class="p-3 font-medium text-right">Precio</th>
                </tr>
              </thead>
              <tbody>
                <template v-for="(item, idx) in pedido.items" :key="'a-' + idx">
                  <tr v-for="(adicion, aidx) in parseAdiciones(item.adicionesJson)"
                    :key="'a-' + idx + '-' + aidx"
                    class="border-b border-border-soft/50">
                    <td class="p-3">{{ adicion.nombre }}</td>
                    <td class="p-3 text-center">{{ adicion.cantidad }}</td>
                    <td class="p-3 text-right">${{ formatoPrecio(adicion.precio * adicion.cantidad) }}</td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
        </div>

        <div v-if="pedido.pagoToken && pedido.montoPendiente > 0" class="flex gap-3">
          <button @click="copiarLink(pedido.pagoToken)"
            class="px-4 py-2 text-sm bg-btn-primary text-btn-primary-text rounded-lg hover:opacity-90 transition">
            Copiar link de pago
          </button>
        </div>

        <div class="mt-8 flex justify-start">
          <button @click="router.push('/admin/pedidos')"
            class="px-4 py-2 text-sm border border-border-soft rounded-lg hover:bg-bg-input transition">
            Volver a pedidos
          </button>
        </div>
      </template>
    </section>
  </div>
</template>
