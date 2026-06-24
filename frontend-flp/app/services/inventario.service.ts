import { apiClient } from './api-client'
import type { Inventario } from '../models/inventario.model'

export const inventarioService = {
  listar(): Promise<Inventario[]> {
    return apiClient.get('/api/inventario')
  },

  crear(data: Partial<Inventario>) {
    return apiClient.post('/api/inventario', data)
  },

  actualizar(id: number, data: Partial<Inventario>) {
    return apiClient.put(`/api/inventario/${id}`, data)
  },

  eliminar(id: number) {
    return apiClient.del(`/api/inventario/${id}`)
  }

}