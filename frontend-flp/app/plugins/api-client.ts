export default defineNuxtPlugin(() => {
  const apiBase = process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8080'
  console.log("API Base configurado en:", apiBase)
  return {
    provide: {
      apiBase
    }
  }
})
