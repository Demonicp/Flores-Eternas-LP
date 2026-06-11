export type RamoResumen = {
  id: number
  nombre: string
  foto: string
  descripcionCorta: string
  precio: number
  disponible: boolean
  fechaCreacion: string
}

export type CategoriaSeccion = {
  nombre: string
  ramos: RamoResumen[]
  badge: string | null
}

export type CatalogoResponse = {
  predefinidos: RamoResumen[]
  temporada: RamoResumen[]
  secciones: CategoriaSeccion[]
  mensaje: string
}
