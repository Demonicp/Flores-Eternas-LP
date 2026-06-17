export function formatoPrecio(valor: number): string {
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(valor)
}

export function sanitizar(texto: string, maxLength: number = 200): string {
  return texto.trim().slice(0, maxLength)
}
