import { apiClient } from './api-client'

export const geminiApi = {
  generarImagen: (prompt: string) =>
    apiClient.post<{ imageUrl: string }>('/api/gemini/generar-imagen', { prompt }),
}