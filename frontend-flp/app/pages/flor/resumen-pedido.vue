<template>
    <h2 class="text-[#7A4E2D] text-2xl font-radley mb-8">Resumen de tu ramo personalizado</h2>

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

      <div class="flex gap-4 justify-center mt-8">
        <button
          @click="router.push('/flor/seleccion-apartados')"
          class="bg-[#FFEDE3] text-[#7A4E2D] font-radley px-6 py-3 rounded-full hover:bg-[#FFDCC8] transition"
        >
          Volver
        </button>
        <button
          @click="agregarAlCarrito"
          class="bg-[#7A4E2D] text-white font-radley px-8 py-3 rounded-full hover:bg-[#5E3A1F] transition flex items-center gap-2"
        >
          <Icon icon="mdi:cart-plus" class="text-lg" />
          Agregar al carrito
        </button>
      </div>
      <p class="text-center text-xs text-gray-500 mt-4 font-radley italic">
        Luego podrás seguir agregando productos del catálogo y pagar todo junto en el carrito.
      </p>
    </div>
</template>

<script setup>
definePageMeta({ layout: 'flor' })
import { computed } from 'vue'
import { useRamoPersonalizadoStore } from '~/stores/ramoPersonalizado'
import { useCartStore } from '~/stores/cart.store'

const store = useRamoPersonalizadoStore()
const cartStore = useCartStore()
const router = useRouter()

if (store.floresSeleccionadas.length === 0) {
  router.replace('/flor/SeleccionFlor')
}

const subtotalFlores = computed(() =>
  store.floresSeleccionadas.reduce((sum, f) => sum + (f.tipoFlor.precioUnidad || 0) * f.cantidad, 0)
)

const totalAdiciones = computed(() =>
  store.adiciones.reduce((acc, a) => acc + (a.precioCosto * a.cantidad), 0)
)

const totalGeneral = computed(() =>
  subtotalFlores.value + totalAdiciones.value
)

function agregarAlCarrito() {
  const flores = store.floresSeleccionadas.map(f => ({
    tipoFlorId: f.tipoFlor.id,
    colorFlorId: f.colorFlor?.id || null,
    cantidad: f.cantidad,
    descripcionFlor: f.tipoFlor.descripcionFlor,
    descripcionColor: f.colorFlor?.descripcionColor || null,
  }))

  const adiciones = store.adiciones.map(a => ({
    inventarioId: a.id,
    nombre: a.nombre,
    cantidad: a.cantidad,
    precioCosto: a.precioCosto,
  }))

  cartStore.agregarPersonalizado({
    id: crypto.randomUUID(),
    nombre: 'Ramo personalizado',
    foto: '',
    precio: totalGeneral.value,
    flores,
    adiciones,
  })

  store.resetear()
  router.push('/')
}
</script>
