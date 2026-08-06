/**
 * Servicio de locales (puntos de retiro).
 * - listarActivos: endpooint publico para el checkout del cliente.
 * - listarTodos: endpooint admin (para futura gestion de locales).
 *
 * @author esteban
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
import { apiClient, getCached } from './api-client'
import type { Local } from '../models/local.model'

export const localService = {
  listarActivos(): Promise<Local[]> {
    return getCached('locales', 300000, () => apiClient.get<Local[]>('/api/locales'))
  },
  listarTodos(): Promise<Local[]> {
    return apiClient.get<Local[]>('/api/admin/locales')
  },
}