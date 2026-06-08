import { apiClient } from './api-client'
import type { ColorFlor } from '../models/color-flor.model'

export const colorFlorService = {
  listar(): Promise<ColorFlor[]> {
    return apiClient.get('/api/colores-flor')
  },
  crear(data: Partial<ColorFlor>): Promise<ColorFlor> {
    return apiClient.post('/api/colores-flor', data)
  },
  actualizar(id: number, data: Partial<ColorFlor>): Promise<ColorFlor> {
    return apiClient.put(`/api/colores-flor/${id}`, data)
  },
  eliminar(id: number): Promise<void> {
    return apiClient.del(`/api/colores-flor/${id}`)
  },
}
