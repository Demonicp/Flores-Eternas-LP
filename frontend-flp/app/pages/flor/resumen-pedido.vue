<template>
    <div class="w-full">
      <h2 class="text-[#7A4E2D] text-2xl font-radley mb-8 text-center">Resumen de tu ramo personalizado</h2>

      <div class="mb-8 max-w-md mx-auto">
        <DashboardIA
          :imagen-url="store.imagenUrl"
          :imagen-generada="store.imagenGenerada"
          :generando="generando"
          :error-generacion="errorGeneracion"
          @generar="generarImagenIA"
        />
      </div>

    <div class="bg-white rounded-2xl shadow-lg p-8 w-full max-w-lg mx-auto">
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
            <div class="mt-3">
              <label class="block text-sm text-[#7A4E2D] font-medium mb-2">Tipo de pago</label>
              <div class="flex flex-col sm:flex-row gap-4">
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="radio" v-model="pagoCompleto" :value="false" class="accent-[#7A4E2D]" />
                  <span class="text-sm text-[#7A4E2D]">Pagar 50% inicial (${{ (totalGeneral * 0.5).toFixed(2) }})</span>
                </label>
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="radio" v-model="pagoCompleto" :value="true" class="accent-[#7A4E2D]" />
                  <span class="text-sm text-[#7A4E2D]">Pagar 100% (${{ totalGeneral.toFixed(2) }})</span>
                </label>
              </div>
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
              <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Modo de entrega</label>
              <div class="flex flex-col sm:flex-row gap-3">
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="radio" v-model="form.modoEntrega" value="domicilio" class="accent-[#7A4E2D]" />
                  <span class="text-sm text-[#7A4E2D]">A domicilio</span>
                </label>
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="radio" v-model="form.modoEntrega" value="retiro" class="accent-[#7A4E2D]" />
                  <span class="text-sm text-[#7A4E2D]">Retiro en local</span>
                </label>
              </div>
            </div>
            <div v-if="form.modoEntrega === 'domicilio'">
              <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Dirección de entrega</label>
              <input v-model="form.direccion" type="text" class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]" placeholder="Dirección" />
            </div>
            <div>
              <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Teléfono de contacto</label>
              <input v-model="form.telefono" type="tel" class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]" placeholder="+57 3001234567" />
            </div>
            <template v-if="form.modoEntrega === 'domicilio'">
              <div>
                <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Departamento</label>
                <select v-model="form.region" @change="form.ciudad = ''"
                  class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]">
                  <option value="" disabled>Selecciona un departamento</option>
                  <option v-for="dept in departamentos" :key="dept" :value="dept">{{ dept }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Ciudad</label>
                <select v-model="form.ciudad" :disabled="!form.region"
                  class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D] disabled:opacity-60">
                  <option value="" disabled>{{ form.region ? 'Selecciona tu ciudad' : 'Primero elige el departamento' }}</option>
                  <option v-for="ciudad in ciudadesDisponibles" :key="ciudad" :value="ciudad">{{ ciudad }}</option>
                </select>
              </div>
            </template>
            <div v-if="form.modoEntrega === 'retiro'">
              <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Local de retiro</label>
              <select v-model="form.localSeleccionadoId"
                class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]">
                <option value="" disabled>Selecciona un local</option>
                <option v-for="local in locales" :key="local.id" :value="local.id">{{ local.nombreLocal }} — {{ local.direccion }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm text-[#7A4E2D] font-medium mb-1">Fecha de entrega</label>
              <input v-model="form.fechaEntrega" type="date" :min="minFechaStr" class="w-full border-2 border-[#FFEDE3] rounded-lg px-3 py-2 text-sm text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]" />
              <p class="text-xs text-gray-400 mt-1">* La fecha de entrega debe ser al menos 5 días hábiles después de hoy</p>
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

          <div class="flex flex-col sm:flex-row gap-4 justify-center mt-8">
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
              {{ pagando ? 'Procesando...' : pagoCompleto ? 'Pagar total' : 'Pagar 50%' }}
            </button>
          </div>
        </div>
    </div>
</template>

<script setup>
definePageMeta({ layout: 'flor' })
import { computed, reactive, ref, onMounted } from 'vue'
import { getDepartamentos, getCiudades } from '~/utils/colombia'
import { localService } from '~/services/local.service'

function sumarDiasHabiles(desde, dias) {
  const fecha = new Date(desde)
  let agregados = 0
  while (agregados < dias) {
    fecha.setDate(fecha.getDate() + 1)
    const dia = fecha.getDay()
    if (dia !== 0 && dia !== 6) agregados++
  }
  return fecha.toISOString().split('T')[0]
}
const minFechaStr = computed(() => sumarDiasHabiles(new Date(), 5))
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { apiClient } from '~/services/api-client'
import { geminiApi } from '~/services/gemini.service'
import { useToast } from '~/composables/useToast'

const store = useRamoPersonalizadoStore()
const router = useRouter()
const toast = useToast()

if (store.floresSeleccionadas.length === 0) {
  router.replace('/flor/SeleccionFlor')
}

/**
 * Datos de envio del pedido. En modo retiro, el cliente solo elige un local
 * y la direccion/ciudad/region del local se inyectan al pagar.
 *
 * @author santiago (sesion 05/08/2026 - retiro en local)
 */
const form = reactive({
  nombre: '',
  email: '',
  direccion: '',
  telefono: '',
  ciudad: '',
  region: '',
  fechaEntrega: '',
  modoEntrega: 'domicilio',
  localSeleccionadoId: null,
})
const pagoCompleto = ref(false)
const pagando = ref(false)
const errorMsg = ref('')
const showError = ref(false)
const generando = ref(false)
const errorGeneracion = ref('')

const departamentos = getDepartamentos()
const locales = ref([])

const ciudadesDisponibles = computed(() => {
  if (!form.region) return []
  return getCiudades(form.region)
})

onMounted(async () => {
  try {
    locales.value = await localService.listarActivos()
  } catch {
    locales.value = []
  }
})

const formValido = computed(() =>
  form.nombre.trim() && form.email.trim() && form.telefono.trim()
  && form.fechaEntrega
  && (form.modoEntrega === 'retiro'
    ? !!form.localSeleccionadoId
    : (form.direccion.trim() && form.ciudad.trim() && form.region.trim()))
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

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const telefonoRegex = /^[0-9+\s\-()]{7,15}$/
const nombreRegex = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/

/**
 * Normaliza el teléfono agregando el prefijo nacional +57 por defecto cuando
 * el número es local de 10 dígitos (Colombia).
 *
 * @author santiago
 */
function normalizarTelefono(tel) {
  if (!tel) return tel
  const digitos = tel.replace(/\D/g, '')
  if (digitos.startsWith('57') && digitos.length === 12) return '+57 ' + digitos.slice(2)
  if (digitos.length === 10) return '+57 ' + digitos
  return tel.trim()
}

const validarFormulario = () => {
  if (!nombreRegex.test(form.nombre)) {
    toast.error('Nombre solo admite letras y espacios')
    return false
  }
  if (!emailRegex.test(form.email)) {
    toast.error('Email inválido')
    return false
  }
  if (form.modoEntrega === 'retiro') {
    if (!form.localSeleccionadoId) {
      toast.error('Selecciona un local de retiro')
      return false
    }
    if (!telefonoRegex.test(form.telefono)) {
      toast.error('Teléfono no válido')
      return false
    }
    if (!form.fechaEntrega) {
      toast.error('Fecha de entrega obligatoria')
      return false
    }
    if (form.fechaEntrega < minFechaStr.value) {
      toast.error('La fecha de entrega debe ser al menos 5 días hábiles después de hoy')
      return false
    }
    return true
  }
  if (!telefonoRegex.test(form.telefono)) {
    toast.error('Teléfono no válido')
    return false
  }
  if (!form.ciudad.trim()) {
    toast.error('Ciudad obligatoria')
    return false
  }
  if (!form.region.trim()) {
    toast.error('Región obligatoria')
    return false
  }
  if (!telefonoRegex.test(form.telefono)) {
    toast.error('Teléfono inválido. Formato: +57 3001234567')
    return false
  }
  if (!form.ciudad.trim()) {
    toast.error('Ciudad obligatoria')
    return false
  }
  if (!form.region.trim()) {
    toast.error('Región obligatoria')
    return false
  }
  if (!form.fechaEntrega) {
    toast.error('Fecha de entrega obligatoria')
    return false
  }
  if (form.fechaEntrega < minFechaStr.value) {
    toast.error('La fecha de entrega debe ser al menos 5 días hábiles después de hoy')
    return false
  }
  return true
}

const filtrarNombre = (event) => {
  const input = event.target
  input.value = input.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '')
  form.nombre = input.value
}

async function generarImagenIA() {
  if (generando.value || store.imagenGenerada) return
  generando.value = true
  errorGeneracion.value = ''

  const flores = store.floresSeleccionadas.map(f =>
    `${f.cantidad}x ${f.tipoFlor.descripcionFlor}${f.colorFlor ? ' color ' + f.colorFlor.descripcionColor : ''}`
  ).join(', ')
  const adiciones = store.adiciones.map(a =>
    `${a.cantidad}x ${a.nombre}`
  ).join(', ')

  const prompt = `Fotografía profesional de catálogo de un ramo de flores eternas artesanal hecho a mano con cinta de satén brillante y listón de raso. Compuesto por: ${flores}.${adiciones ? ` Accesorios incluidos: ${adiciones}.` : ''} El ramo tiene envoltorio elegante en capas de papel coreano plisado con un gran moño de cinta satinada. Iluminación suave de estudio, brillo sutil del tejido de satén, composición limpia de boutique floral, alta resolución.`

  try {
    const res = await geminiApi.generarImagen(prompt, store.sesionToken)
    store.imagenUrl = res.imageUrl
  } catch (e) {
    errorGeneracion.value = e instanceof Error ? e.message : 'Error al generar la imagen'
  } finally {
    generando.value = false
  }
}

async function pagarAhora() {
  if (!validarFormulario()) return
  pagando.value = true
  errorMsg.value = ''

  const esRetiro = form.modoEntrega === 'retiro'
  const local = esRetiro
    ? locales.value.find(l => l.id === form.localSeleccionadoId) || null
    : null
  const direccionEntrega = local?.direccion ?? form.direccion
  const ciudad = local?.ciudad ?? form.ciudad
  const region = local?.region ?? form.region

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
    const res = await apiClient.post('/api/pagos/wompi/iniciar', {
      nombreCliente: form.nombre,
      emailCliente: form.email,
      direccionEntrega,
      telefono: normalizarTelefono(form.telefono),
      ciudad,
      region,
      fechaEntrega: form.fechaEntrega,
      flores,
      adiciones,
      pagoCompleto: pagoCompleto.value,
      responseUrl: window.location.origin + '/pago/resultado',
    })

    store.resetear()

    if (res.signature) {
      const formEl = document.createElement('form')
      formEl.method = 'GET'
      formEl.action = 'https://checkout.wompi.co/p/'
      const campos = [
        ['public-key', res.publicKey],
        ['currency', res.currency],
        ['amount-in-cents', String(res.amountInCents)],
        ['reference', res.reference],
        ['signature:integrity', res.signature],
        ['redirect-url', res.redirectUrl || (window.location.origin + '/pago/resultado')],
        ['customer-data:email', form.email],
        ['customer-data:full-name', form.nombre],
        ['shipping-address:address-line-1', direccionEntrega],
        ['shipping-address:country', 'CO'],
        ['shipping-address:city', ciudad],
        ['shipping-address:region', region],
        ['shipping-address:phone-number', normalizarTelefono(form.telefono)],
      ]
      for (const [name, val] of campos) {
        const input = document.createElement('input')
        input.type = 'hidden'
        input.name = name
        input.value = val
        formEl.appendChild(input)
      }
      document.body.appendChild(formEl)
      formEl.submit()
    } else {
      router.push('/pago/resultado?estado=APROBADO&ref=' + res.pedidoId)
    }
  } catch (e) {
    errorMsg.value = e instanceof Error ? e.message : 'Error al procesar el pago. Intenta nuevamente.'
    showError.value = true
  } finally {
    pagando.value = false
  }
}

</script>
