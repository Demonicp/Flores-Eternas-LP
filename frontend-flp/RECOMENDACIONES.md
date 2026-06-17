# Recomendaciones — Frontend Flores Eternas LP

## Consumir la API correctamente (sin hardcodear localhost)

### Regla #1: Variable de entorno única

Toda comunicación con el backend usa la variable `NUXT_PUBLIC_API_BASE` definida en `.env`:

```env
NUXT_PUBLIC_API_BASE=https://tu-backend.com
```

Si la variable no está definida, la aplicación lanza un error alto y claro. **No hay fallback a localhost.**

### Regla #2: Siempre usar `floresApi` o `apiClient`

**Nunca uses `fetch()` directo en las páginas ni componentes.** Todas las llamadas al backend deben ir a través del cliente centralizado en `~/services/api-client.ts`.

```ts
import { floresApi } from '~/services/api-client'

// ✅ Bien
const tipos = await floresApi.getTipos()
const colores = await floresApi.getColores()
const pedido = await floresApi.crearPedido(data)

// ❌ Mal — no hardcodear URLs ni usar fetch() directo
const base = config.public.apiBase || 'http://localhost:8080'
const res = await fetch(`${base}/api/flores/tipos`)
```

Si necesitas un endpoint nuevo, agrega el método a `floresApi` en `api-client.ts`, no crees un `fetch()` nuevo en la página.

### Regla #3: Si no existe el método en `api-client.ts`, agrégalo ahí

```ts
export const floresApi = {
  getTipos: () => apiClient.get<any[]>('/api/flores/tipos'),
  getColores: () => apiClient.get<any[]>('/api/flores/colores'),
  getAdiciones: () => apiClient.get<any[]>('/api/flores/adiciones'),
  crearPedido: (data: any) => apiClient.post<any>('/api/pedidos/crear', data),
  // agrega aquí los nuevos endpoints
}
```

### Regla #4: Regla de linter sugerida (ESLint)

Agrega esta regla para prevenir hardcodeos accidentales:

```json
{
  "rules": {
    "no-restricted-syntax": ["error", {
      "selector": "Literal[value=/localhost/]",
      "message": "No hardcodear localhost. Usar floresApi desde ~/services/api-client"
    }]
  }
}
```

### Regla #5: Error alto si falta configuración

Si la variable `NUXT_PUBLIC_API_BASE` no está definida, la app debe fallar de forma visible, no caer silenciosamente a un fallback:

```ts
const apiBase = import.meta.env.NUXT_PUBLIC_API_BASE
if (!apiBase) throw new Error('❌ Falta NUXT_PUBLIC_API_BASE en .env')
```

### Resumen

| ✅ Hacer | ❌ No hacer |
|---|---|
| Usar `floresApi.getXxx()` en páginas | Usar `fetch()` directo |
| Usar `apiClient.post/put/del` para nuevos endpoints | Importar `config.public.apiBase` en páginas |
| Agregar métodos a `floresApi` en `api-client.ts` | Hardcodear `'http://localhost:8080'` |
| Definir `NUXT_PUBLIC_API_BASE` en `.env` | Usar `NUXT_PUBLIC_API_BASE_URL` (nombre incorrecto) |
