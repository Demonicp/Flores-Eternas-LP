<template>
    <h2 class="text-[#7A4E2D] text-2xl font-radley mb-8">Resumen de tu ramo personalizado</h2>

    <div class="bg-white rounded-2xl shadow-lg p-8 w-full max-w-lg">
      <div class="space-y-4 mb-6">
        <div v-for="(item, idx) in store.floresSeleccionadas" :key="idx"
          class="flex items-center justify-between py-3 border-b border-[#FFEDE3] last:border-0"
        >
          <div class="flex items-center gap-3">
            <Icon :icon="item.tipoFlor.icono || 'mdi:flower-tulip-outline'" class="text-3xl" :style="item.tipoFlor.iconoColor ? { color: item.tipoFlor.iconoColor } : {}" />
            <div>
              <p class="text-[#7A4E2D] font-radley">{{ item.tipoFlor.descripcionFlor }}</p>
              <p class="text-xs text-gray-500">
                {{ item.colorFlor?.descripcionColor || 'Sin color' }} × {{ item.cantidad }}
              </p>
            </div>
          </div>
          <p class="text-[#7A4E2D] font-lora">
            ${{ ((item.tipoFlor.precioUnidad || 0) * item.cantidad).toFixed(2) }}
          </p>
        </div>
      </div>

      <div v-if="store.adiciones.length > 0" class="mb-6">
        <p class="text-gray-500 text-sm mb-2">Adiciones</p>
        <div v-for="adicion in store.adiciones" :key="adicion.id"
          class="flex justify-between text-[#7A4E2D]"
        >
          <span>{{ adicion.nombre }} x{{ adicion.cantidad }}</span>
          <span class="font-lora">${{ (adicion.precioCosto * adicion.cantidad).toFixed(2) }}</span>
        </div>
      </div>

      <div class="border-t border-[#FFEDE3] pt-4 mb-6">
        <div class="flex justify-between text-lg font-lora text-[#7A4E2D]">
          <span>Total</span>
          <span>${{ totalGeneral.toFixed(2) }}</span>
        </div>
        <div class="flex justify-between text-sm text-[#7A4E2D] mt-1">
          <span>Pago inicial (50%)</span>
          <span class="font-semibold">${{ (totalGeneral * 0.5).toFixed(2) }}</span>
        </div>
      </div>

      <div class="space-y-4 mb-6">
        <div>
          <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Nombre completo</label>
          <input v-model="form.nombre" @input="filtrarNombre" type="text" class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]" placeholder="Tu nombre" />
        </div>
        <div>
          <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Email</label>
          <input v-model="form.email" type="email" class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]" placeholder="correo@ejemplo.com" />
        </div>
        <div>
          <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Dirección de entrega</label>
          <input v-model="form.direccion" type="text" class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]" placeholder="Dirección" />
        </div>
        <div>
          <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Fecha de entrega</label>
          <input v-model="form.fechaEntrega" type="date" :min="hoyStr" class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]" />
        </div>
      </div>

      <!-- Modal error -->
      <div v-if="showError" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="showError = false">
        <div class="bg-white rounded-xl p-6 max-w-sm mx-4 shadow-xl">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center flex-shrink-0">
              <Icon icon="mdi:alert-circle" class="text-xl text-red-500" />
            </div>
            <p class="text-lg font-semibold text-[#7A4E2D]">Error</p>
          </div>
          <p class="text-[#7A4E2D]/80 mb-4">{{ errorMsg }}</p>
          <button @click="showError = false" class="w-full bg-[#7A4E2D] text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-[#5E3A1F] transition">
            Aceptar
          </button>
        </div>
      </div>

      <div class="flex gap-4 justify-center mt-8">
        <button
          @click="router.push('/flor/seleccion-apartados')"
          class="bg-[#FFEDE3] text-[#7A4E2D] font-radley px-6 py-3 rounded-full hover:bg-[#FFDCC8] transition"
        >
          Volver
        </button>
        <button
          @click="pagarAhora"
          :disabled="pagando || !formValido"
          class="bg-[#7A4E2D] text-white font-radley px-8 py-3 rounded-full hover:bg-[#5E3A1F] transition flex items-center gap-2 disabled:opacity-50"
        >
          <Icon icon="mdi:credit-card-outline" class="text-lg" />
          {{ pagando ? 'Procesando...' : 'Pagar 50% con PayU' }}
        </button>
      </div>
    </div>
</template>

<script setup>
definePageMeta({ layout: 'flor' })
import { computed, reactive, ref } from 'vue'

const hoyStr = computed(() => new Date().toISOString().split('T')[0])
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { apiClient } from '~/services/api-client'
import { useToast } from '~/composables/useToast'

const store = useRamoPersonalizadoStore()
const router = useRouter()
const toast = useToast()

if (store.floresSeleccionadas.length === 0) {
  router.replace('/flor/SeleccionFlor')
}

const form = reactive({
  nombre: '',
  email: '',
  direccion: '',
  fechaEntrega: '',
})
const pagando = ref(false)
const errorMsg = ref('')
const showError = ref(false)

const formValido = computed(() =>
  form.nombre.trim() && form.email.trim() && form.direccion.trim() && form.fechaEntrega
)

const subtotalFlores = computed(() =>
  store.floresSeleccionadas.reduce((sum, f) => sum + (f.tipoFlor.precioUnidad || 0) * f.cantidad, 0)
)

const totalAdiciones = computed(() =>
  store.adiciones.reduce((acc, a) => acc + (a.precioCosto * a.cantidad), 0)
)

const totalGeneral = computed(() =>
  subtotalFlores.value + totalAdiciones.value
)

const direccionRegex = /^(calle|carrera|av\.?|avenida|transversal|diagonal|circular|cra|kr|cl|cll|tv|tr|dg|cq)\s+\d{1,3}[a-zA-Z]?\s*#?\s*\d{1,3}[a-zA-Z]?[-–]\d{1,3}[a-zA-Z]?$/i
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const telefonoRegex = /^(\+57\s?)?(3\d{9}|60\d{8})$/
const nombreRegex = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/

const validarFormulario = () => {
  if (!nombreRegex.test(form.nombre)) {
    toast.error('Nombre solo admite letras y espacios')
    return false
  }
  if (!emailRegex.test(form.email)) {
    toast.error('Email inválido')
    return false
  }
  if (!direccionRegex.test(form.direccion)) {
    toast.error('Dirección debe ser: calle 28 #25-38')
    return false
  }
  if (!form.fechaEntrega) {
    toast.error('Fecha de entrega obligatoria')
    return false
  }
  return true
}

const filtrarNombre = (event) => {
  const input = event.target
  input.value = input.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '')
  form.nombre = input.value
}

async function pagarAhora() {
  if (!validarFormulario()) return
  pagando.value = true
  errorMsg.value = ''

  const flores = store.floresSeleccionadas.map(f => ({
    tipoFlorId: f.tipoFlor.id,
    colorFlorId: f.colorFlor?.id || null,
    cantidad: f.cantidad,
  }))

  const adiciones = store.adiciones.map(a => ({
    inventarioId: a.id,
    cantidad: a.cantidad,
  }))

  try {
    const res = await apiClient.post('/api/pagos/payu/iniciar', {
      nombreCliente: form.nombre,
      emailCliente: form.email,
      direccionEntrega: form.direccion,
      fechaEntrega: form.fechaEntrega,
      flores,
      adiciones,
      responseUrl: window.location.origin + '/pago/resultado',
    })

    store.resetear()

    if (res.urlPago && res.parametrosForm?.merchantId) {
      const formEl = document.createElement('form')
      formEl.method = 'POST'
      formEl.action = res.urlPago
      for (const [key, val] of Object.entries(res.parametrosForm)) {
        const input = document.createElement('input')
        input.type = 'hidden'
        input.name = key
        input.value = val
        formEl.appendChild(input)
      }
      document.body.appendChild(formEl)
      formEl.submit()
    } else {
      router.push('/pago/resultado?statePol=4&referenceSale=' + res.pedidoId)
    }
  } catch (e) {
    errorMsg.value = e instanceof Error ? e.message : 'Error al procesar el pago. Intenta nuevamente.'
    showError.value = true
  } finally {
    pagando.value = false
  }
}
</script>
