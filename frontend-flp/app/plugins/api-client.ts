import { defineNuxtPlugin } from '#app'
import { setApiBase } from '~/services/api-client'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase
  if (!apiBase) throw new Error('❌ Falta NUXT_PUBLIC_API_BASE en .env')
  setApiBase(apiBase)
  console.log('API Base configurado en:', apiBase)
})
