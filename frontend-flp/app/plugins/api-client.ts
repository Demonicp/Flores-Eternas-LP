import { setApiBase } from '../services/api-client'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  setApiBase(config.public.apiBase || 'http://localhost:8080')
})
