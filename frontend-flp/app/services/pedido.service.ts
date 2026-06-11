import { apiClient } from './api-client'
import type { PedidoRequest, PedidoResponse } from '../models/pedido.model'

export const pedidoService = {
  crearPedido(data: PedidoRequest): Promise<PedidoResponse> {
    return apiClient.post('/api/pedidos', data)
  },
}
