/**
 * @author esteban
 * Servicio de autenticación para el sistema Flores Eternas.
 * Responsable de las comunicaciones HTTP con los endpoints de autenticación
 * del backend (/api/auth/register y /api/auth/login).
 * Sigue el patrón de servicios del MVVM: stateless, solo devuelve promesas.
 */

import { apiClient } from './api-client'
import type { LoginRequest, LoginResponse, RegisterRequest } from '../models/auth.model'

/**
 * @author esteban
 * Registro de administrador en el sistema.
 * Solo funciona cuando no existe ningún otro administrador registrado.
 * @param request Datos del administrador a registrar (correo, contraseña, nombre)
 * @returns Promesa con la respuesta del backend (token, rol, nombre)
 * @throws Error si el registro falla o ya existe un administrador
 */
export async function registrar(request: RegisterRequest): Promise<LoginResponse> {
  return apiClient.post<LoginResponse>('/api/auth/register', request)
}

/**
 * @author esteban
 * Inicio de sesión de administrador en el sistema.
 * @param request Credenciales del administrador (correo y contraseña)
 * @returns Promesa con la respuesta del backend (token, rol, nombre)
 * @throws Error si las credenciales son inválidas
 */
export async function iniciarSesion(request: LoginRequest): Promise<LoginResponse> {
  return apiClient.post<LoginResponse>('/api/auth/login', request)
}

export const authService = {
  registrar,
  iniciarSesion,
}