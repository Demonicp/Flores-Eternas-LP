import type { CategoriaRamo } from './categoria-ramo.model'
import type { TipoFlor } from './tipo-flor.model'
import type { ColorFlor } from './color-flor.model'

export type DetalleLinea = {
  cantidad: number
  idTipoFlor: number
  idColorFlor: number
}

export type DetalleLineaForm = {
  idTipoFlor: number | null
  idColorFlor: number | null
  cantidad: number
}

export type RamoRequest = {
  nombreRamo: string
  precioRamo: number
  descripcionRamo: string
  fotoRamo: string
  idCategoriaRamo: number
  disponible: boolean
  stock: number | null
  detallesRamo: DetalleLinea[]
}

export type DetalleRamoResponse = {
  id: number
  cantidad: number
  tipoFlor: TipoFlor
  colorFlor: ColorFlor
}

export type RamoResponse = {
  id: number
  nombreRamo: string
  precioRamo: number
  descripcionRamo: string
  fotoRamo: string
  disponible: boolean
  stock: number | null
  categoria: CategoriaRamo
  detallesRamo: DetalleRamoResponse[]
}
