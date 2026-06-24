import { apiClient } from './api-client'
import type { PedidoRequest, PedidoResponse } from '../models/pedido.model'

export const pedidoService = {
  crearPedido(data: PedidoRequest): Promise<PedidoResponse> {
    return apiClient.post('/api/pedidos', data)
  },
  listar(): Promise<PedidoResponse[]> {
    return apiClient.get('/api/admin/pedidos')
  },
  actualizarEstado(id: number, estado: string): Promise<PedidoResponse> {
    return apiClient.put(`/api/admin/pedidos/${id}/estado`, { estado })
  },
}
