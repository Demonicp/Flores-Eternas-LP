/**
 * @author esteban
 * Modelos de autenticación para el sistema Flores Eternas.
 * Contiene las interfaces TypeScript que definen la estructura de datos
 * para login, registro y respuestas de autenticación.
 */

/**
 * @author esteban
 * DTO para solicitar autenticación (login) en el sistema.
 * Contiene las credenciales del administrador: correo y contraseña.
 */
export interface LoginRequest {
  correo: string
  contrasena: string
}

/**
 * @author esteban
 * DTO para registrar un nuevo administrador en el sistema.
 * Solo funciona cuando no existe ningún otro administrador registrado.
 */
export interface RegisterRequest {
  correo: string
  contrasena: string
  nombre: string
}

/**
 * @author esteban
 * DTO con la respuesta de autenticación exitosa del backend.
 * Contiene el token JWT y datos básicos del usuario autenticado.
 */
export interface LoginResponse {
  token: string
  rol: string
  nombre: string
}

/**
 * @author esteban
 * Interfaz que representa al administrador autenticado.
 * Se usa para almacenar el estado global del usuario en el store.
 */
export interface AdminUser {
  token: string
  rol: string
  nombre: string
}