import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface VarianteFlor {
  tipoFlor: any
  colorFlor: any | null
  cantidad: number
}

export const useRamoPersonalizadoStore = defineStore('ramoPersonalizado', () => {
  const floresSeleccionadas = ref<VarianteFlor[]>([])
  const adiciones = ref<any[]>([])
  const cedula = ref('')
  const nombreCliente = ref('')
  const telefono = ref('')
  const direccionEntrega = ref('')

  const totalFlores = computed(() =>
    floresSeleccionadas.value.reduce((sum, f) => sum + f.cantidad, 0)
  )

  function isSelected(tipoFlorId: number): boolean {
    return floresSeleccionadas.value.some(f => f.tipoFlor.id === tipoFlorId)
  }

  function toggleFlor(tipoFlor: any) {
    const idx = floresSeleccionadas.value.findIndex(f => f.tipoFlor.id === tipoFlor.id)
    if (idx >= 0) {
      floresSeleccionadas.value = floresSeleccionadas.value.filter(f => f.tipoFlor.id !== tipoFlor.id)
    } else {
      floresSeleccionadas.value.push({ tipoFlor, colorFlor: null, cantidad: 1 })
    }
  }

  function agregarVariante(tipoFlor: any) {
    floresSeleccionadas.value.push({ tipoFlor, colorFlor: null, cantidad: 1 })
  }

  function quitarVariante(index: number) {
    floresSeleccionadas.value.splice(index, 1)
  }

  function setColor(index: number, color: any) {
    if (floresSeleccionadas.value[index]) {
      floresSeleccionadas.value[index].colorFlor = color
    }
  }

  function setCantidad(index: number, cant: number) {
    if (floresSeleccionadas.value[index]) {
      floresSeleccionadas.value[index].cantidad = Math.max(1, cant)
    }
  }

  function agregarAdicion(item: any, cant = 1) {
    const existente = adiciones.value.find(a => a.id === item.id)
    if (existente) {
      existente.cantidad += cant
    } else {
      adiciones.value.push({ ...item, cantidad: cant })
    }
  }

  function quitarAdicion(itemId: number) {
    adiciones.value = adiciones.value.filter(a => a.id !== itemId)
  }

  function calcularTotal(): number {
    let total = 0
    for (const f of floresSeleccionadas.value) {
      total += (f.tipoFlor.precioUnidad || 0) * f.cantidad
    }
    for (const adicion of adiciones.value) {
      total += (adicion.precioCosto || 0) * adicion.cantidad
    }
    return total
  }

  function resetear() {
    floresSeleccionadas.value = []
    adiciones.value = []
    cedula.value = ''
    nombreCliente.value = ''
    telefono.value = ''
    direccionEntrega.value = ''
  }

  return {
    floresSeleccionadas, adiciones, cedula, nombreCliente, telefono, direccionEntrega,
    totalFlores,
    isSelected, toggleFlor, agregarVariante, quitarVariante, setColor, setCantidad,
    agregarAdicion, quitarAdicion, calcularTotal, resetear
  }
})
