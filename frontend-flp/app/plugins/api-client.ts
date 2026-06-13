import { defineNuxtPlugin } from '#app'
import { setApiBase } from '~/services/api-client'

export default defineNuxtPlugin(() => {
  let apiBase

  if (import.meta.env.DEV) {
    // 👉 Cuando corres npm run dev (local)
    apiBase = 'http://localhost:8080'
  } else {
    // 👉 Cuando despliegas en Vercel (producción)
    apiBase = 'https://flores-eternas-lp.onrender.com'
  }

  setApiBase(apiBase)
  console.log('API Base configurado en:', apiBase)
})
