import { apiClient } from './api-client'
import type { CatalogoResponse, RamoResumen } from '../models/catalogo.model'

export const catalogoService = {
  obtenerCatalogo(): Promise<CatalogoResponse> {
    return apiClient.get('/api/catalogo')
  },
  listarPredefinidos(): Promise<RamoResumen[]> {
    return apiClient.get('/api/catalogo/predefinidos')
  },
  listarTemporada(): Promise<RamoResumen[]> {
    return apiClient.get('/api/catalogo/temporada')
  },
}
