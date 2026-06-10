/**
 * @author esteban
 * Store de autenticación para el sistema Flores Eternas.
 * Gestiona el estado global del administrador: token JWT, sesión,
 * y persistencia en localStorage para mantener la sesión entre recargas.
 * Implementado con Pinia siguiendo el patrón del proyecto.
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginRequest, LoginResponse, AdminUser } from '../models/auth.model'
import { authService } from '../services/auth.service'

const TOKEN_KEY = 'flp_admin_token'
const USER_KEY = 'flp_admin_user'

/**
 * @author esteban
 * Store de autenticación que maneja:
 * - Almacenamiento del token JWT en localStorage
 * - Datos del administrador (nombre, rol)
 * - Estado de sesión (isLoggedIn)
 * - Acciones de login y logout
 */
export const useAuthStore = defineStore('auth', () => {
  /* ─── Estado ─── */
  const token = ref<string | null>(null)
  const nombre = ref<string>('')
  const rol = ref<string>('')
  const loading = ref(false)
  const error = ref<string | null>(null)

  /* ─── Computed ─── */
  const isLoggedIn = computed(() => !!token.value)

  /* ─── Inicialización ─── */
  function cargarDesdeStorage() {
    if (typeof window === 'undefined') return
    const storedToken = localStorage.getItem(TOKEN_KEY)
    const storedUser = localStorage.getItem(USER_KEY)
    if (storedToken && storedUser) {
      try {
        const user: AdminUser = JSON.parse(storedUser)
        token.value = storedToken
        nombre.value = user.nombre
        rol.value = user.rol
      } catch {
        logout()
      }
    }
  }

  /* ─── Acciones ─── */
  function guardarSesion(response: LoginResponse) {
    token.value = response.token
    nombre.value = response.nombre
    rol.value = response.rol
    localStorage.setItem(TOKEN_KEY, response.token)
    localStorage.setItem(USER_KEY, JSON.stringify({
      token: response.token,
      rol: response.rol,
      nombre: response.nombre,
    }))
  }

  function logout() {
    token.value = null
    nombre.value = ''
    rol.value = ''
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  async function login(credentials: LoginRequest): Promise<boolean> {
    loading.value = true
    error.value = null
    try {
      const response = await authService.iniciarSesion(credentials)
      guardarSesion(response)
      return true
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al iniciar sesión'
      return false
    } finally {
      loading.value = false
    }
  }

  async function registrar(datos: { correo: string; contrasena: string; nombre: string }): Promise<boolean> {
    loading.value = true
    error.value = null
    try {
      const response = await authService.registrar(datos)
      guardarSesion(response)
      return true
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al registrar'
      return false
    } finally {
      loading.value = false
    }
  }

  /* ─── Inicializar al crear el store ─── */
  cargarDesdeStorage()

  return {
    token,
    nombre,
    rol,
    loading,
    error,
    isLoggedIn,
    login,
    registrar,
    logout,
    cargarDesdeStorage,
  }
})