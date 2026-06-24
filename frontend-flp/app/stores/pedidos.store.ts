import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { PedidoResponse } from '../models/pedido.model'
import { pedidoService } from '../services/pedido.service'

export const usePedidosStore = defineStore('pedidos', () => {

  const pedidos = ref<PedidoResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function cargarPedidos() {
    loading.value = true
    error.value = null
    try {
      pedidos.value = await pedidoService.listar()
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar pedidos'
    } finally {
      loading.value = false
    }
  }

  async function cambiarEstado(id: number, estado: string) {
    error.value = null
    try {
      await pedidoService.actualizarEstado(id, estado)
      const idx = pedidos.value.findIndex(p => p.id === id)
      if (idx !== -1) {
        pedidos.value[idx].estado = estado
      }
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al actualizar estado'
      throw e
    }
  }

  return {
    pedidos,
    loading,
    error,
    cargarPedidos,
    cambiarEstado,
  }
})
