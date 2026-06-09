import { apiClient } from './api-client'
import type { RamoRequest, RamoResponse } from '../models/ramo.model'

export const ramoService = {
  listar(): Promise<RamoResponse[]> {
    return apiClient.get('/api/ramos')
  },
  obtener(id: number): Promise<RamoResponse> {
    return apiClient.get(`/api/ramos/${id}`)
  },
  crear(data: RamoRequest): Promise<RamoResponse> {
    return apiClient.post('/api/ramos', data)
  },
  actualizar(id: number, data: RamoRequest): Promise<RamoResponse> {
    return apiClient.put(`/api/ramos/${id}`, data)
  },
  eliminar(id: number): Promise<void> {
    return apiClient.del(`/api/ramos/${id}`)
  },
}
