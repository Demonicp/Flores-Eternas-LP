<template>
    <h2 class="text-[#7A4E2D] text-2xl font-radley mb-8">Resumen de tu pedido</h2>

    <div class="bg-white rounded-2xl shadow-lg p-8">
      <div class="space-y-4 mb-6">
        <div v-for="(item, idx) in store.floresSeleccionadas" :key="idx"
          class="flex items-center justify-between py-3 border-b border-[#FFEDE3] last:border-0"
        >
          <div class="flex items-center gap-3">
            <div class="text-3xl">🌸</div>
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
      </div>

      <div class="border-t border-[#FFEDE3] pt-6 space-y-4">
        <h3 class="text-[#7A4E2D] font-radley text-lg">Tus datos</h3>
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
        <div>
          <label class="block text-sm text-gray-500 mb-1">Fecha de entrega</label>
          <input type="date" v-model="fechaEntrega" :min="fechaMinimaStr"
            class="w-full border-2 border-[#FFEDE3] rounded-lg px-4 py-2 text-[#7A4E2D] focus:outline-none focus:border-[#7A4E2D]"
          />
          <p class="text-xs text-gray-500 mt-1 font-radley italic">* La entrega se programa mínimo 5 días después del pedido</p>
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
          @click="realizarPedido"
          :disabled="enviando"
          class="font-radley px-8 py-3 rounded-full transition"
          :class="enviando ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-[#7A4E2D] text-white hover:bg-[#5E3A1F]'"
        >
          {{ enviando ? 'Enviando...' : 'Realizar Compra' }}
        </button>
      </div>
    </div>

    <Transition name="fade">
      <div v-if="mostrarModal"
        class="fixed inset-0 bg-[#7A4E2D]/20 flex items-center justify-center z-50"
        @click.self="cerrarModal"
      >
        <div class="bg-[#FFFAF5] rounded-2xl shadow-xl border border-[#FFEDE3] p-10 max-w-md mx-4 text-center">
          <div class="w-16 h-16 rounded-full mx-auto mb-4 flex items-center justify-center" :class="exito ? 'bg-[#E8F5E9]' : 'bg-[#FFEBEE]'">
            <svg v-if="exito" xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-[#4CAF50]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-[#E53935]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </div>
          <h3 class="text-2xl font-radley text-[#7A4E2D] mb-2">{{ mensajeTitulo }}</h3>
          <p class="text-[#7A4E2D]/70 mb-6">{{ mensajeTexto }}</p>
          <button
            @click="cerrarModal"
            class="bg-[#7A4E2D] text-white font-radley px-8 py-3 rounded-full hover:bg-[#5E3A1F] transition"
          >
            {{ exito ? 'Ir al inicio' : 'Cerrar' }}
          </button>
        </div>
      </div>
    </Transition>
</template>

<script setup>
definePageMeta({ layout: 'flor' })
import { ref, computed } from 'vue'
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { floresApi } from '~/services/api-client'

const store = useRamoPersonalizadoStore()
const router = useRouter()

if (store.floresSeleccionadas.length === 0) {
  router.replace('/flor/SeleccionFlor')
}

const cedula = ref(store.cedula || '')
const nombreCliente = ref(store.nombreCliente || '')
const telefono = ref(store.telefono || '')
const direccion = ref(store.direccionEntrega || '')
const fechaMinima = new Date()
fechaMinima.setDate(fechaMinima.getDate() + 5)
const fechaMinimaStr = fechaMinima.toISOString().split('T')[0]
const fechaEntrega = ref('')

const subtotalFlores = computed(() =>
  store.floresSeleccionadas.reduce((sum, f) => sum + (f.tipoFlor.precioUnidad || 0) * f.cantidad, 0)
)

const totalAdiciones = computed(() =>
  store.adiciones.reduce((acc, a) => acc + (a.precioCosto * a.cantidad), 0)
)

const totalGeneral = computed(() =>
  subtotalFlores.value + totalAdiciones.value
)

const enviando = ref(false)
const mostrarModal = ref(false)
const exito = ref(false)
const mensajeTitulo = ref('')
const mensajeTexto = ref('')

async function realizarPedido() {
  if (store.floresSeleccionadas.length === 0) {
    exito.value = false
    mensajeTitulo.value = 'Faltan flores'
    mensajeTexto.value = 'No has seleccionado ninguna flor. Vuelve atrás y elige al menos una.'
    mostrarModal.value = true
    return
  }
  const floresInvalidas = store.floresSeleccionadas.filter(f => !f.cantidad || f.cantidad < 1 || !f.colorFlor)
  if (floresInvalidas.length > 0) {
    const nombres = [...new Set(floresInvalidas.map(f => f.tipoFlor.descripcionFlor))]
    exito.value = false
    mensajeTitulo.value = 'Configuración incompleta'
    mensajeTexto.value = `Las siguientes flores necesitan cantidad y color: ${nombres.join(', ')}. Vuelve a configurar tu ramo.`
    mostrarModal.value = true
    return
  }
  if (!cedula.value || !nombreCliente.value || !direccion.value || !fechaEntrega.value) {
    exito.value = false
    mensajeTitulo.value = 'Campos incompletos'
    const faltan = []
    if (!cedula.value) faltan.push('cédula')
    if (!nombreCliente.value) faltan.push('nombre')
    if (!direccion.value) faltan.push('dirección de entrega')
    if (!fechaEntrega.value) faltan.push('fecha de entrega')
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
      flores: store.floresSeleccionadas.map(f => ({
        tipoFlorId: f.tipoFlor.id,
        colorFlorId: f.colorFlor?.id || null,
        cantidad: f.cantidad
      })),
      adiciones: adicionesPayload,
      direccionEntrega: direccion.value,
      fechaEntrega: fechaEntrega.value,
      cedula: cedula.value,
      nombreCliente: nombreCliente.value,
      telefono: telefono.value
    }

    const data = await floresApi.crearPedido(body)

    exito.value = true
    mensajeTitulo.value = '¡Pedido creado!'
    mensajeTexto.value = `Tu pedido ha sido registrado por $${data.totalPedido?.toFixed(2) || totalGeneral.value.toFixed(2)}.`
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
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
