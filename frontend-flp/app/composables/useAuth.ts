/**
 * @author esteban
 * Composables de autenticación para el sistema Flores Eternas.
 * Proporciona acceso reactivo al store de autenticación desde cualquier
 * componente o página, manteniendo la Separation of Concerns del MVVM.
 * Este composable actúa como la capa ViewModel que conecta la Vista con el Model.
 */

import { useAuthStore } from '../stores/auth.store'

/**
 * @author esteban
 * Hook reutilizable para acceder al estado y funciones de autenticación.
 * Recomendado usar en páginas y componentes en lugar de importar el store directamente.
 *
 * @returns Instancia del store de autenticación con todas sus propiedades y métodos:
 * - token: string | null — Token JWT actual
 * - nombre: string — Nombre del administrador
 * - rol: string — Rol del administrador
 * - isLoggedIn: boolean — True si hay sesión activa
 * - loading: boolean — True si hay operación en curso
 * - error: string | null — Mensaje de error de la última operación
 * - login(credentials): Promise<boolean> — Inicia sesión
 * - registrar(datos): Promise<boolean> — Registra administrador
 * - logout(): void — Cierra sesión
 *
 * @example
 * const { isLoggedIn, login, logout } = useAuth()
 *
 * async function handleLogin() {
 *   const success = await login({ correo: 'admin@test.com', contrasena: '123456' })
 *   if (success) navigateTo('/admin/dashboard')
 * }
 */
export function useAuth() {
  return useAuthStore()
}