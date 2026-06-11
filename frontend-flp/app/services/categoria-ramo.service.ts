import { apiClient } from './api-client'
import type { CategoriaRamo } from '../models/categoria-ramo.model'

export const categoriaRamoService = {
  listar(): Promise<CategoriaRamo[]> {
    return apiClient.get('/api/categorias-ramo')
  },
  crear(data: Partial<CategoriaRamo>): Promise<CategoriaRamo> {
    return apiClient.post('/api/categorias-ramo', data)
  },
  actualizar(id: number, data: Partial<CategoriaRamo>): Promise<CategoriaRamo> {
    return apiClient.put(`/api/categorias-ramo/${id}`, data)
  },
  eliminar(id: number): Promise<void> {
    return apiClient.del(`/api/categorias-ramo/${id}`)
  },
}
