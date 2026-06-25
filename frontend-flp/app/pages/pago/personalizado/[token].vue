<template>
  <div class="min-h-screen bg-bg-page flex items-center justify-center">
    <div class="bg-white rounded-xl shadow-sm border border-border-soft p-10 max-w-md w-full">
      <div v-if="cargando" class="py-8 text-center">
        <Icon icon="mdi:loading" class="text-4xl text-text-primary animate-spin mx-auto mb-4" />
        <p class="text-text-primary">Cargando pedido...</p>
      </div>

      <template v-else-if="error">
        <Icon icon="mdi:alert-circle-outline" class="text-5xl text-red-500 mx-auto mb-4" />
        <h1 class="text-xl font-serif text-text-primary font-medium mb-2 text-center">Pedido no encontrado</h1>
        <p class="text-text-primary/70 text-center">{{ error }}</p>
      </template>

      <template v-else-if="pedido">
        <Icon icon="mdi:package-variant-closed" class="text-4xl text-text-primary mx-auto mb-4" />
        <h1 class="text-xl font-serif text-text-primary font-medium mb-1 text-center">Pedido #{{ pedido.id }}</h1>
        <p class="text-text-primary/60 text-sm text-center mb-6">{{ pedido.tipoPedido }}</p>

        <div class="border-t border-border-soft pt-4 space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-text-primary/60">Total</span>
            <span class="font-medium">${{ formatoPrecio(pedido.total) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-text-primary/60">Pagado</span>
            <span class="font-medium">${{ formatoPrecio(pedido.montoPagado) }}</span>
          </div>
          <div class="flex justify-between text-base border-t border-border-soft pt-2">
            <span class="font-medium">Saldo pendiente</span>
            <span class="font-bold text-text-primary">${{ formatoPrecio(pedido.montoPendiente) }}</span>
          </div>
        </div>

        <button
          v-if="pedido.montoPendiente > 0"
          @click="pagarSaldo"
          :disabled="pagando"
          class="mt-6 w-full bg-btn-primary text-btn-primary-text py-3 rounded-lg text-sm font-medium hover:opacity-90 transition disabled:opacity-50"
        >
          {{ pagando ? 'Redirigiendo a PayU...' : 'Pagar saldo restante con PayU' }}
        </button>

        <p v-else class="mt-6 text-center text-green-600 text-sm font-medium">
          Pedido pagado en su totalidad.
        </p>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatoPrecio } from '~/utils/formatters'
import { apiClient } from '~/services/api-client'

const route = useRoute()
const token = route.params.token as string

const cargando = ref(true)
const error = ref('')
const pedido = ref<any>(null)
const pagando = ref(false)

async function cargarPedido() {
  try {
    const res = await apiClient.get(`/api/pagos/personalizado/${token}`)
    pedido.value = res as any
  } catch (e: any) {
    error.value = 'El enlace no es válido o el pedido no existe.'
  } finally {
    cargando.value = false
  }
}

async function pagarSaldo() {
  if (!pedido.value) return
  pagando.value = true
  try {
    const res: any = await apiClient.post(`/api/pagos/personalizado/${token}/pagar`, {
      responseUrl: window.location.origin + '/pago/resultado',
    })
    if (res.urlPago && res.parametrosForm) {
      const form = document.createElement('form')
      form.method = 'POST'
      form.action = res.urlPago
      for (const [key, val] of Object.entries(res.parametrosForm)) {
        const input = document.createElement('input')
        input.type = 'hidden'
        input.name = key
        input.value = val as string
        form.appendChild(input)
      }
      document.body.appendChild(form)
      form.submit()
    } else {
      pedido.value.montoPagado = pedido.value.total
      pedido.value.montoPendiente = 0
    }
  } catch (e: any) {
    error.value = 'Error al iniciar el pago.'
  } finally {
    pagando.value = false
  }
}

onMounted(cargarPedido)
</script>
