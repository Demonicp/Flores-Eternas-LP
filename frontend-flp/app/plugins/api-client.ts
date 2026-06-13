import { defineNuxtPlugin } from '#app'
import { setApiBase } from '~/services/api-client'

export default defineNuxtPlugin(() => {
  if (import.meta.env.DEV) {
    // 👉 Cuando corres npm run dev (local)
    setApiBase('http://localhost:8080')
  } else {
    // 👉 Cuando despliegas en Vercel (producción)
    setApiBase('https://flores-eternas-lp.onrender.com')
  }

  // Opcional: log para verificar
  console.log('API Base configurado en:', setApiBase)
})
