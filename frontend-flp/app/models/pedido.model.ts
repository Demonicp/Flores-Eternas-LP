export type ItemCarrito = {
  ramo: {
    id: number
    nombre: string
    foto: string
    precio: number
  }
  cantidad: number
}

export type PedidoRequest = {
  nombreCliente: string
  emailCliente: string
  direccionEntrega: string
  fechaEntrega: string
  tipoPedido: string
  tipoPago: string
  items: Array<{
    idRamo: number
    cantidad: number
  }>
}

export type PedidoResponse = {
  id: number
  total: number
  montoPagado: number
  montoPendiente: number
  estado: string
  tipoPedido: string
  fechaEntrega: string
  mensaje: string
  items: Array<{
    nombreRamo: string
    cantidad: number
    precioUnitario: number
  }>
}
