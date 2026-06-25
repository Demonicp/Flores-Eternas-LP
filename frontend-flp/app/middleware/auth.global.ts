/**
 * @author esteban
 * Middleware de autenticación para el sistema Flores Eternas.
 * Protege las rutas del panel de administrador (/admin/*).
 * Redirige a /admin/login si no hay sesión activa.
 * Las rutas /admin/login y /admin/registro son públicas y no requieren auth.
 *
 * IMPORTANTE: Para que este middleware se ejecute automáticamente en todas las rutas,
 * el archivo debe llamarse 'auth.global.ts'. Si se llama 'auth.ts' solo,
 * se debe agregar 'definePageMeta({ middleware: [auth] })' en cada página.
 *
 * ESTADO: COMENTADO PARA DESARROLLO — Descomentar antes de producción
 */
import { useAuthStore } from '~/stores/auth.store'

export default defineNuxtRouteMiddleware((to) => {
  if (!to.path.startsWith('/admin')) {
    return
  }

  const auth = useAuthStore()
  const publicRoutes = ['/admin/login', '/admin/registro']
  const isPublicRoute = publicRoutes.includes(to.path)

  if (!auth.isLoggedIn && !isPublicRoute) {
    return navigateTo('/admin/login')
  }

  if (auth.isLoggedIn && (to.path === '/admin/login' || to.path === '/admin/registro')) {
    return navigateTo('/admin/pedidos')
  }
})