import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { PedidoResponse } from '../models/pedido.model'
import { pedidoService } from '../services/pedido.service'

export const usePedidosStore = defineStore('pedidos', () => {

  const pedidos = ref<PedidoResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const filtro = ref<'todos' | 'pendientes' | 'entregados' | 'proximos'>('todos')

  const pedidosFiltrados = computed(() => {
    let filtered = [...pedidos.value]

    switch (filtro.value) {
      case 'pendientes':
        filtered = filtered.filter(p => p.estado !== 'ENTREGADO' && p.estado !== 'CANCELADO')
        break
      case 'entregados':
        filtered = filtered.filter(p => p.estado === 'ENTREGADO')
        break
      case 'proximos':
        filtered = filtered.filter(p => {
          if (!p.fechaEntrega) return false
          const hoy = new Date()
          const entrega = new Date(p.fechaEntrega)
          const diff = Math.ceil((entrega.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24))
          return diff <= 3 && diff >= 0
        })
        break
      case 'todos':
        break
      default:
        break
    }

    return filtered.sort((a, b) => {
      if (!a.fechaCreacion) return 1
      if (!b.fechaCreacion) return -1
      return new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
    })
  })

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
    filtro,
    pedidosFiltrados,
    cargarPedidos,
    cambiarEstado,
  }
})
