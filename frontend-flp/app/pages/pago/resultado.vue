<template>
  <div class="min-h-screen bg-bg-page flex items-center justify-center">
    <div class="bg-white rounded-xl shadow-sm border border-border-soft p-10 max-w-md w-full text-center">
      <div v-if="cargando" class="py-8">
        <Icon icon="mdi:loading" class="text-4xl text-text-primary animate-spin mx-auto mb-4" />
        <p class="text-text-primary">Verificando pago...</p>
      </div>

      <template v-else-if="aprobado">
        <Icon icon="mdi:check-circle" class="text-5xl text-green-500 mx-auto mb-4" />
        <h1 class="text-2xl font-serif text-text-primary font-medium mb-2">Pago exitoso</h1>
        <p class="text-text-primary/70 mb-1">Pedido #{{ pedidoId }}</p>
        <p class="text-text-primary/70 mb-6">{{ mensaje }}</p>
        <NuxtLink to="/" class="inline-block bg-btn-primary text-btn-primary-text px-6 py-2 rounded-lg text-sm font-medium hover:opacity-90">
          Volver al inicio
        </NuxtLink>
      </template>

      <template v-else-if="rechazado">
        <Icon icon="mdi:close-circle" class="text-5xl text-red-500 mx-auto mb-4" />
        <h1 class="text-2xl font-serif text-text-primary font-medium mb-2">Pago rechazado</h1>
        <p class="text-text-primary/70 mb-6">{{ mensaje || 'Intenta nuevamente.' }}</p>
        <button @click="volverAIntentar" class="bg-btn-primary text-btn-primary-text px-6 py-2 rounded-lg text-sm font-medium hover:opacity-90">
          Intentar de nuevo
        </button>
      </template>

      <template v-else>
        <Icon icon="mdi:clock-outline" class="text-5xl text-amber-500 mx-auto mb-4" />
        <h1 class="text-2xl font-serif text-text-primary font-medium mb-2">Pago pendiente</h1>
        <p class="text-text-primary/70 mb-6">Esperando confirmación del pago.</p>
        <NuxtLink to="/" class="inline-block bg-btn-primary text-btn-primary-text px-6 py-2 rounded-lg text-sm font-medium hover:opacity-90">
          Volver al inicio
        </NuxtLink>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const router = useRouter()

const cargando = ref(true)
const aprobado = ref(false)
const rechazado = ref(false)
const pedidoId = ref('')
const mensaje = ref('')

onMounted(() => {
  const referenceCode = (route.query.referenceCode as string) || ''
  const statePol = (route.query.statePol as string) || ''
  const transactionId = (route.query.transactionId as string) || ''
  const value = (route.query.value as string) || ''
  pedidoId.value = (route.query.referenceSale as string) || referenceCode

  if (statePol === '4' || statePol === 'APPROVED') {
    aprobado.value = true
    mensaje.value = 'Tu pago fue procesado con éxito.'
  } else if (statePol === '6' || statePol === 'DECLINED') {
    rechazado.value = true
    mensaje.value = 'El pago fue rechazado.'
  } else {
    cargando.value = false
  }

  cargando.value = false
})

function volverAIntentar() {
  router.back()
}
</script>
