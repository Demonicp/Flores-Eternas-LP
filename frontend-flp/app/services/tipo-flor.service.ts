import { apiClient } from './api-client'
import type { TipoFlor } from '../models/tipo-flor.model'

export const tipoFlorService = {
  listar(): Promise<TipoFlor[]> {
    return apiClient.get('/api/tipos-flor')
  },
  crear(data: Partial<TipoFlor>): Promise<TipoFlor> {
    return apiClient.post('/api/tipos-flor', data)
  },
  actualizar(id: number, data: Partial<TipoFlor>): Promise<TipoFlor> {
    return apiClient.put(`/api/tipos-flor/${id}`, data)
  },
  eliminar(id: number): Promise<void> {
    return apiClient.del(`/api/tipos-flor/${id}`)
  },
}
