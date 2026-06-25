export type ItemCarrito = {
  ramo: {
    id: number
    nombre: string
    foto: string
    precio: number
  }
  cantidad: number
}

export type FlorPersonalizada = {
  tipoFlorId: number
  colorFlorId: number | null
  cantidad: number
  descripcionFlor: string
  descripcionColor: string | null
}

export type AdicionPersonalizada = {
  inventarioId: number
  nombre: string
  cantidad: number
  precioCosto: number
}

export type PersonalizadoCarrito = {
  id: string
  nombre: string
  foto: string
  precio: number
  flores: FlorPersonalizada[]
  adiciones: AdicionPersonalizada[]
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
  floresPersonalizadas?: Array<{
    tipoFlorId: number
    colorFlorId: number | null
    cantidad: number
  }>
  adicionesPersonalizadas?: Array<{
    inventarioId: number
    cantidad: number
  }>
  cedulaCliente?: string
  telefonoCliente?: string
}

export type PedidoResponse = {
  id: number
  total: number
  montoPagado: number
  montoPendiente: number
  estado: string
  tipoPedido: string
  fechaEntrega: string
  fechaCreacion: string
  mensaje: string
  nombreCliente?: string
  emailCliente?: string
  direccionEntrega?: string
  pagoToken?: string
  items: Array<{
    nombreRamo: string
    cantidad: number
    precioUnitario: number
    tipoFlor?: string
    colorFlor?: string
    adicionesJson?: string
  }>
}
