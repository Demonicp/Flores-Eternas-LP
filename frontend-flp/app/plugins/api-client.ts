import { defineNuxtPlugin } from '#app'
import { setApiBase, getApiBase } from '~/services/api-client'

export default defineNuxtPlugin(() => {
  if (import.meta.env.DEV) {
    setApiBase('http://localhost:8080')
  } else {
    setApiBase('https://flores-eternas-lp.onrender.com')
  }

  // ✅ Mostrar el valor configurado
  console.log('API Base configurado en:', getApiBase())
})
