/**
 * @author esteban
 * Servicio de autenticación para el sistema Flores Eternas.
 * Responsable de las comunicaciones HTTP con los endpoints de autenticación
 * del backend (/api/auth/register, /api/auth/login, /api/auth/forgot-password
 * y /api/auth/reset-password).
 * Sigue el patrón de servicios del MVVM: stateless, solo devuelve promesas.
 */

import { apiClient } from './api-client'
import type {
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  MessageResponse,
  RegisterRequest,
  ResetPasswordRequest,
} from '../models/auth.model'

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

/**
 * @author esteban
 * Solicita el envio de un codigo de recuperacion de contrasena al correo
 * indicado. El backend siempre responde 200 con un mensaje generico para
 * evitar que un atacante pueda enumerar correos registrados.
 * @param request Correo electronico de la administradora.
 * @returns Promesa con la respuesta del backend (mensaje generico).
 */
export async function solicitarCodigoRecuperacion(
  request: ForgotPasswordRequest
): Promise<MessageResponse> {
  return apiClient.post<MessageResponse>('/api/auth/forgot-password', request)
}

/**
 * @author esteban
 * Restablece la contrasena de la administradora a partir del codigo
 * de 6 digitos recibido por correo. El backend valida que el codigo
 * este vigente y que la nueva contrasena cumpla la politica.
 * @param request Correo, codigo de 6 digitos y nueva contrasena.
 * @returns Promesa con la respuesta del backend (mensaje de exito).
 */
export async function cambiarContrasena(
  request: ResetPasswordRequest
): Promise<MessageResponse> {
  return apiClient.post<MessageResponse>('/api/auth/reset-password', request)
}

export const authService = {
  registrar,
  iniciarSesion,
  solicitarCodigoRecuperacion,
  cambiarContrasena,
}