const TOKEN_KEY = 'flp_admin_token'

const cache = new Map<string, { data: any; expiry: number }>()

export class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message)
  }
}

let _apiBase = ''

export function setApiBase(base: string) {
  _apiBase = base
}

export function getApiBase(): string {
  return _apiBase
}

function getToken(): string | null {
  if (typeof window === 'undefined') return null
  return localStorage.getItem(TOKEN_KEY)
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const url = `${_apiBase}${path}`
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  for (let attempt = 0; attempt < 2; attempt++) {
    try {
      const controller = new AbortController()
      if (attempt > 0) {
        setTimeout(() => controller.abort(), 20000)
      }

      const options: RequestInit = {
        method,
        headers,
        signal: controller.signal,
      }
      if (body !== undefined) {
        options.body = JSON.stringify(body)
      }

      const res = await fetch(url, options)
      controller.signal.addEventListener('abort', () => {})

      if (!res.ok) {
        let msg = `Error ${res.status}`
        try {
          const data = await res.json()
          msg = data.message || data.error || msg
        } catch {
          const text = await res.text().catch(() => '')
          if (text) msg = text
        }
        if (typeof window !== 'undefined') {
          const { error } = await import('~/composables/useToast')
          error(msg)
        }
        throw new ApiError(res.status, msg)
      }
      if (res.status === 204) return undefined as T
      return res.json()
    } catch (e) {
      if (attempt === 0 && method === 'GET' && !(e instanceof DOMException && e.name === 'AbortError')) {
        await new Promise(r => setTimeout(r, 3000))
        continue
      }
      throw e
    }
  }
  throw new Error('Unexpected error')
}

export function getCached<T>(key: string, ttlMs: number, fetcher: () => Promise<T>): Promise<T> {
  const cached = cache.get(key)
  if (cached && cached.expiry > Date.now()) {
    return Promise.resolve(cached.data)
  }
  return fetcher().then(data => {
    cache.set(key, { data, expiry: Date.now() + ttlMs })
    return data
  })
}

export const floresApi = {
  getTipos: () => getCached('tipos', 300000, () => apiClient.get<any[]>('/api/flores/tipos')),
  getColores: () => getCached('colores', 300000, () => apiClient.get<any[]>('/api/flores/colores')),
  getAdiciones: () => getCached('adiciones', 300000, () => apiClient.get<any[]>('/api/flores/adiciones')),
  crearPedido: (data: any) => apiClient.post<any>('/api/pedidos/crear', data),
}

export const apiClient = {
  get<T>(path: string): Promise<T> {
    return request<T>('GET', path)
  },
  post<T>(path: string, body?: unknown): Promise<T> {
    return request<T>('POST', path, body)
  },
  put<T>(path: string, body?: unknown): Promise<T> {
    return request<T>('PUT', path, body)
  },
  del<T>(path: string): Promise<T> {
    return request<T>('DELETE', path)
  },
}
