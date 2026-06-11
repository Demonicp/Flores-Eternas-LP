import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useRamoPersonalizadoStore = defineStore('ramoPersonalizado', () => {
  const tipoFlor = ref(null)
  const colorFlor = ref(null)
  const cantidad = ref(1)
  const adiciones = ref([])
  const cedula = ref('')
  const nombreCliente = ref('')
  const telefono = ref('')
  const direccionEntrega = ref('')

  function seleccionarTipoFlor(flor) {
    tipoFlor.value = flor
  }

  function seleccionarColor(color) {
    colorFlor.value = color
  }

  function setCantidad(val) {
    cantidad.value = val
  }

  function agregarAdicion(item, cant = 1) {
    const existente = adiciones.value.find(a => a.id === item.id)
    if (existente) {
      existente.cantidad += cant
    } else {
      adiciones.value.push({ ...item, cantidad: cant })
    }
  }

  function quitarAdicion(itemId) {
    adiciones.value = adiciones.value.filter(a => a.id !== itemId)
  }

  function calcularTotal() {
    let total = 0
    if (tipoFlor.value && cantidad.value > 0) {
      total += tipoFlor.value.precioUnidad * cantidad.value
    }
    for (const adicion of adiciones.value) {
      total += (adicion.precioCosto || 0) * adicion.cantidad
    }
    return total
  }

  function resetear() {
    tipoFlor.value = null
    colorFlor.value = null
    cantidad.value = 1
    adiciones.value = []
    cedula.value = ''
    nombreCliente.value = ''
    telefono.value = ''
    direccionEntrega.value = ''
  }

  return {
    tipoFlor, colorFlor, cantidad, adiciones, cedula, nombreCliente, telefono, direccionEntrega,
    seleccionarTipoFlor, seleccionarColor, setCantidad,
    agregarAdicion, quitarAdicion, calcularTotal, resetear
  }
})
