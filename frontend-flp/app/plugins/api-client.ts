import { setApiBase } from "~/services/api-client";

export default defineNuxtPlugin(() => {
  const apiBase = import.meta.env.NUXT_PUBLIC_API_BASE
  if (!apiBase) throw new Error('❌ Falta NUXT_PUBLIC_API_BASE en .env')
  setApiBase(apiBase)
  console.log('API Base configurado en:', apiBase)
})
