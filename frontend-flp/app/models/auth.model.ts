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

/**
 * @author esteban
 * DTO para solicitar el envio de un codigo de recuperacion de contrasena.
 * Contiene unicamente el correo electronico de la administradora.
 */
export interface ForgotPasswordRequest {
  correo: string
}

/**
 * @author esteban
 * DTO para restablecer la contrasena a partir del codigo de 6 digitos
 * recibido por correo. El backend valida el codigo, su vigencia y la
 * politica de contrasena antes de persistir el cambio.
 */
export interface ResetPasswordRequest {
  correo: string
  codigo: string
  nuevaContrasena: string
}

/**
 * @author esteban
 * Respuesta generica del backend con un solo campo "message".
 * Se usa en endpoints de recuperacion de contrasena que solo
 * devuelven un mensaje legible (exito o error).
 */
export interface MessageResponse {
  message: string
}