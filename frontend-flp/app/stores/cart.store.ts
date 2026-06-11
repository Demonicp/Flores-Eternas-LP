import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ItemCarrito, PedidoRequest, PedidoResponse } from '../models/pedido.model'
import { pedidoService } from '../services/pedido.service'

export const useCartStore = defineStore('cart', () => {
  const items = ref<ItemCarrito[]>([])
  const abierto = ref(false)
  const cargando = ref(false)
  const respuestaPedido = ref<PedidoResponse | null>(null)

  const totalItems = computed(() =>
    items.value.reduce((sum, i) => sum + i.cantidad, 0)
  )

  const subtotal = computed(() =>
    items.value.reduce((sum, i) => sum + i.ramo.precio * i.cantidad, 0)
  )

  function toggleCarrito() {
    abierto.value = !abierto.value
  }

  function abrirCarrito() {
    abierto.value = true
  }

  function cerrarCarrito() {
    abierto.value = false
  }

  function agregarItem(ramo: { id: number; nombre: string; foto: string; precio: number }) {
    const existente = items.value.find(i => i.ramo.id === ramo.id)
    if (existente) {
      existente.cantidad++
    } else {
      items.value.push({ ramo, cantidad: 1 })
    }
    abierto.value = true
  }

  function eliminarItem(idRamo: number) {
    items.value = items.value.filter(i => i.ramo.id !== idRamo)
  }

  function cambiarCantidad(idRamo: number, delta: number) {
    const item = items.value.find(i => i.ramo.id === idRamo)
    if (!item) return
    const nueva = item.cantidad + delta
    if (nueva <= 0) {
      eliminarItem(idRamo)
    } else {
      item.cantidad = nueva
    }
  }

  function limpiarCarrito() {
    items.value = []
    respuestaPedido.value = null
  }

  async function realizarPedido(data: PedidoRequest): Promise<PedidoResponse> {
    cargando.value = true
    try {
      const res = await pedidoService.crearPedido(data)
      respuestaPedido.value = res
      return res
    } finally {
      cargando.value = false
    }
  }

  return {
    items,
    abierto,
    cargando,
    respuestaPedido,
    totalItems,
    subtotal,
    toggleCarrito,
    abrirCarrito,
    cerrarCarrito,
    agregarItem,
    eliminarItem,
    cambiarCantidad,
    limpiarCarrito,
    realizarPedido,
  }
})
