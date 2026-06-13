import { defineNuxtPlugin } from '#app'
import { setApiBase, getApiBase } from '~/services/api-client'

export default defineNuxtPlugin(() => {
  // Usa la variable pública si existe, o localhost como fallback
  const apiBase = import.meta.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8080'
  setApiBase(apiBase)

  // Mostrar el valor configurado correctamente
  console.log('API Base configurado en:', getApiBase())
})
