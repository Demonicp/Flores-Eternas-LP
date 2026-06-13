import { defineNuxtPlugin } from '#app'
import { setApiBase } from '~/services/api-client'

export default defineNuxtPlugin(() => {
  // Usa la variable pública si existe
  const apiBase = import.meta.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'
  setApiBase(apiBase)

  console.log('API Base configurado en:', apiBase)
})
