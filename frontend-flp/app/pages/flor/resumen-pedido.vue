<template>
  <div class="min-h-screen bg-[#FFFAF5] flex flex-col items-center">
    <nav class="w-full flex justify-between items-center py-5 px-16 bg-[#FFEDE3] text-[#7A4E2D] font-medium">
      <div class="flex gap-10">
        <a href="#" class="hover:underline">Entrega inmediata</a>
        <a href="#" class="font-semibold underline">Personalizado</a>
      </div>
      <NuxtLink to="/"><img src="/assets/images/flplogoblack.png" alt="Flores Eternas LP" class="h-14 w-auto cursor-pointer" /></NuxtLink>
      <div class="flex gap-6 items-center">
        <button><Icon name="ph:user" size="26" /></button>
        <button><Icon name="ph:shopping-cart" size="26" /></button>
      </div>
    </nav>

    <main class="flex flex-col items-center mt-10 w-full max-w-5xl px-8">
      <h2 class="text-[#7A4E2D] text-2xl font-serif mb-8">Resumen de tu pedido</h2>

      <div class="flex gap-8 w-full">
        <div
          class="w-56 h-72 rounded-xl shadow-md flex flex-col items-center justify-center gap-3 flex-shrink-0 bg-[#FFEDE3]"
        >
          <div class="text-5xl">🌸</div>
          <span class="text-[#7A4E2D] text-lg font-serif">{{ store.tipoFlor?.descripcionFlor }}</span>
          <span class="text-[#7A4E2D] text-sm">${{ store.tipoFlor?.precioUnidad?.toFixed(2) }} c/u</span>
        </div>

        <div class="bg-white rounded-2xl shadow-lg p-8 flex-1">
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <p class="text-gray-500 text-sm">Flor</p>
              <p class="text-[#7A4E2D] font-serif">{{ store.tipoFlor?.descripcionFlor }}</p>
            </div>
            <div>
              <p class="text-gray-500 text-sm">Cantidad</p>
              <p class="text-[#7A4E2D] font-serif">{{ store.cantidad }}</p>
            </div>
            <div>
              <p class="text-gray-500 text-sm">Color</p>
              <p class="text-[#7A4E2D] font-serif">{{ store.colorFlor?.descripcionColor }}</p>
            </div>
            <div>
              <p class="text-gray-500 text-sm">Subtotal flores</p>
              <p class="text-[#7A4E2D] font-serif">${{ subtotalFlores.toFixed(2) }}</p>
            </div>
          </div>

          <div v-if="store.adiciones.length > 0" class="mb-6">
            <p class="text-gray-500 text-sm mb-2">Adiciones</p>
            <div v-for="adicion in store.adiciones" :key="adicion.id"
              class="flex justify-between text-[#7A4E2D]"
            >
              <span>{{ adicion.nombre }} x{{ adicion.cantidad }}</span>
              <span>${{ (adicion.precioCosto * adicion.cantidad).toFixed(2) }}</span>
            </div>
          </div>

          <div class="border-t border-[#FFEDE3] pt-4 mb-6">
            <div class="flex justify-between text-lg font-serif text-[#7A4E2D]">
              <span>Total</span>
              <span>${{ totalGeneral.toFixed(2) }}</span>
            </div>
          </div>

          <div class="border-t border-[#FFEDE3] pt-6 space-y-4">
            <h3 class="text-[#7A4E2D] font-serif text-lg">Tus datos</h3>
            <div>
              <label class="block text-sm text-gray-500 mb-1">Cédula</label>
              <input v-model="cedula" type="text" placeholder="1234567890"
                class="w-full border-2 border-[#FFEDE3] rounded-lg px-4 py-2 text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]"
              />
            </div>
            <div>
              <label class="block text-sm text-gray-500 mb-1">Nombre completo</label>
              <input v-model="nombreCliente" type="text" placeholder="María Pérez"
                class="w-full border-2 border-[#FFEDE3] rounded-lg px-4 py-2 text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]"
              />
            </div>
            <div>
              <label class="block text-sm text-gray-500 mb-1">Teléfono</label>
              <input v-model="telefono" type="text" placeholder="3001234567"
                class="w-full border-2 border-[#FFEDE3] rounded-lg px-4 py-2 text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]"
              />
            </div>
            <div>
              <label class="block text-sm text-gray-500 mb-1">Dirección de entrega</label>
              <input v-model="direccion" type="text" placeholder="Calle 123 # 45-67"
                class="w-full border-2 border-[#FFEDE3] rounded-lg px-4 py-2 text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]"
              />
            </div>
          </div>

          <div class="flex gap-4 justify-center mt-8">
            <button
              @click="router.push('/flor/adiciones')"
              class="bg-[#FFEDE3] text-[#7A4E2D] font-serif px-6 py-3 rounded-full hover:bg-[#FFDCC8] transition"
            >
              Volver
            </button>
            <button
              @click="realizarPedido"
              :disabled="enviando"
              class="font-serif px-8 py-3 rounded-full transition"
              :class="enviando ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-[#7A4E2D] text-white hover:bg-[#5E3A1F]'"
            >
              {{ enviando ? 'Enviando...' : 'Realizar Compra' }}
            </button>
          </div>
        </div>
      </div>
    </main>

    <div v-if="mostrarModal"
      class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
      @click.self="cerrarModal"
    >
      <div class="bg-white rounded-2xl shadow-2xl p-10 max-w-md mx-4 text-center">
        <div class="text-5xl mb-4">{{ exito ? '✅' : '❌' }}</div>
        <h3 class="text-2xl font-serif text-[#7A4E2D] mb-2">{{ mensajeTitulo }}</h3>
        <p class="text-gray-600 mb-6">{{ mensajeTexto }}</p>
        <button
          @click="cerrarModal"
          class="bg-[#7A4E2D] text-white font-serif px-8 py-3 rounded-full hover:bg-[#5E3A1F] transition"
        >
          {{ exito ? 'Ir al inicio' : 'Cerrar' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { floresApi } from '~/services/api-client'

const store = useRamoPersonalizadoStore()
const router = useRouter()

if (!store.tipoFlor) {
  router.replace('/flor/SeleccionFlor')
}

const cedula = ref(store.cedula || '')
const nombreCliente = ref(store.nombreCliente || '')
const telefono = ref(store.telefono || '')
const direccion = ref(store.direccionEntrega || '')

const subtotalFlores = computed(() => {
  return (store.tipoFlor?.precioUnidad || 0) * (store.cantidad || 0)
})

const totalAdiciones = computed(() => {
  return store.adiciones.reduce((acc, a) => acc + (a.precioCosto * a.cantidad), 0)
})

const totalGeneral = computed(() => {
  return subtotalFlores.value + totalAdiciones.value
})

const enviando = ref(false)
const mostrarModal = ref(false)
const exito = ref(false)
const mensajeTitulo = ref('')
const mensajeTexto = ref('')

async function realizarPedido() {
  if (!store.tipoFlor) {
    exito.value = false
    mensajeTitulo.value = 'Falta la flor'
    mensajeTexto.value = 'No has seleccionado un tipo de flor. Vuelve atrás y elige uno.'
    mostrarModal.value = true
    return
  }
  if (!store.colorFlor) {
    exito.value = false
    mensajeTitulo.value = 'Falta el color'
    mensajeTexto.value = 'No has seleccionado un color para tu ramo. Vuelve atrás y elige uno.'
    mostrarModal.value = true
    return
  }
  if (!cedula.value || !nombreCliente.value || !direccion.value) {
    exito.value = false
    mensajeTitulo.value = 'Campos incompletos'
    const faltan = []
    if (!cedula.value) faltan.push('cédula')
    if (!nombreCliente.value) faltan.push('nombre')
    if (!direccion.value) faltan.push('dirección de entrega')
    mensajeTexto.value = 'Por favor completa: ' + faltan.join(', ') + '.'
    mostrarModal.value = true
    return
  }

  enviando.value = true
  try {
    const adicionesPayload = store.adiciones.length > 0
      ? store.adiciones.map(a => ({
          inventarioId: a.id,
          cantidad: a.cantidad
        }))
      : []

    const body = {
      tipoFlorId: store.tipoFlor.id,
      colorFlorId: store.colorFlor.id,
      cantidad: store.cantidad,
      adiciones: adicionesPayload,
      direccionEntrega: direccion.value,
      cedula: cedula.value,
      nombreCliente: nombreCliente.value,
      telefono: telefono.value
    }

    const data = await floresApi.crearPedido(body)

    exito.value = true
    mensajeTitulo.value = '¡Pedido creado!'
    mensajeTexto.value = `Tu pedido #${data.id} ha sido registrado por $${data.totalPedido?.toFixed(2) || totalGeneral.value.toFixed(2)}. Total: $${data.totalPedido}.`
    mostrarModal.value = true

  } catch (e) {
    console.error('Error:', e)
    exito.value = false
    mensajeTitulo.value = 'Error al crear pedido'
    mensajeTexto.value = e.message || 'Ocurrió un error inesperado. Intenta de nuevo.'
    mostrarModal.value = true
  } finally {
    enviando.value = false
  }
}

function cerrarModal() {
  mostrarModal.value = false
  if (exito.value) {
    store.resetear()
    router.push('/')
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display&display=swap');
</style>
