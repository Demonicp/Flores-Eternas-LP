const TOKEN_KEY = 'flp_admin_token'


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
  const options: RequestInit = {
    method,
    headers,
  }
  if (body !== undefined) {
    options.body = JSON.stringify(body)
  }
  const res = await fetch(url, options)
  if (!res.ok) {
    const text = await res.text().catch(() => '')
    throw new ApiError(res.status, text || `Error ${res.status}`)
  }
  if (res.status === 204) return undefined as T
  return res.json()
}

export const floresApi = {
  getTipos: () => apiClient.get<any[]>('/api/flores/tipos'),
  getColores: () => apiClient.get<any[]>('/api/flores/colores'),
  getAdiciones: () => apiClient.get<any[]>('/api/flores/adiciones'),
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
