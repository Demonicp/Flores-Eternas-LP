import { defineNuxtPlugin } from '#app'
import { setApiBase, getApiBase } from '~/services/api-client'

export default defineNuxtPlugin(() => {
  const apiBase = import.meta.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8080'
  setApiBase(apiBase)

  console.log('API Base configurado en:', getApiBase())
})
