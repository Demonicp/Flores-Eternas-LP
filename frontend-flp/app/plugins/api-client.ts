export default defineNuxtPlugin(() => {
  const apiBase = process.env.NUXT_PUBLIC_API_BASE
  console.log("API Base configurado en:", apiBase)
  return {
    provide: {
      apiBase
    }
  }
})
