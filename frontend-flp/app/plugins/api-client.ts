import { setApiBase } from "~/services/api-client";

export default defineNuxtPlugin(() => {
  const apiBase = 'https://flores-eternas-lp.onrender.com'
  setApiBase(apiBase)
  console.log('API Base configurado en:', apiBase)
})
