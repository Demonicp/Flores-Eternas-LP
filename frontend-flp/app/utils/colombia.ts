/**
 * Dataset de departamentos y municipios de Colombia (códigos DANE oficiales).
 * Fuente: paquete npm `divipola` (v1.0.3, MIT).
 * Se usa en el checkout para que la ciudad dependa del departamento elegido.
 *
 * @author esteban
 * @author santiago (sesion 05/08/2026 - dependencia departamento -> ciudad)
 */

import divipola from 'divipola'

export type MunicipioDane = {
  mpioCode: string
  mpioName: string
  deptoName: string
}

const DATOS: MunicipioDane[] = divipola as MunicipioDane[]

/**
 * Departamentos cuyos nombres oficiales (DANE) difieren de la etiqueta amigable
 * que se muestra en el formulario de checkout.
 */
const DEPARTAMENTOS_ESPECIALES: Record<string, string> = {
  'BOGOTÁ. D.C.': 'Bogotá D.C.',
  'ARCHIPIÉLAGO DE SAN ANDRÉS. PROVIDENCIA Y SANTA CATALINA': 'San Andrés y Providencia',
}

const PALABRAS_MINUSCULAS = ['de', 'del', 'la', 'las', 'los', 'y']

/**
 * Convierte un nombre en MAYÚSCULAS del DANE a formato amigable
 * (ej. "VALLE DEL CAUCA" -> "Valle del Cauca").
 * La primera palabra siempre se capitaliza ("LA GUAJIRA" -> "La Guajira").
 *
 * @author esteban
 * @param nombre nombre original en mayúsculas.
 * @returns nombre capitalizado.
 */
function capitalizar(nombre: string): string {
  const palabras = nombre.split(' ')
  return palabras
    .map((palabra, i) => {
      const baja = palabra.toLowerCase()
      if (i > 0 && PALABRAS_MINUSCULAS.includes(baja)) return baja
      if (/^d\.c\.?$/i.test(palabra)) return 'D.C.'
      return palabra.charAt(0).toUpperCase() + baja.slice(1)
    })
    .join(' ')
}

/**
 * Normaliza el nombre de un departamento del DANE a su etiqueta amigable.
 *
 * @author esteban
 * @param deptoName nombre oficial del departamento.
 * @returns etiqueta amigable usada en el checkout.
 */
function normalizarDepartamento(deptoName: string): string {
  return DEPARTAMENTOS_ESPECIALES[deptoName] ?? capitalizar(deptoName)
}

let deparmentosCache: string[] | null = null

/**
 * Lista de departamentos de Colombia como etiquetas amigables, ordenadas.
 *
 * @author esteban
 * @returns departamentos disponibles.
 */
export function getDepartamentos(): string[] {
  if (deparmentosCache) return deparmentosCache
  const set = new Set<string>()
  for (const m of DATOS) {
    set.add(normalizarDepartamento(m.deptoName))
  }
  deparmentosCache = Array.from(set).sort((a, b) => a.localeCompare(b, 'es'))
  return deparmentosCache
}

/**
 * Devuelve los municipios de un departamento (etiquetas amigables).
 *
 * @author esteban
 * @param departamento etiqueta del departamento (ver getDepartamentos).
 * @returns municipios del departamento o lista vacía si no existe.
 */
export function getCiudades(departamento: string): string[] {
  const codigo = DATOS.find(m => normalizarDepartamento(m.deptoName) === departamento)?.deptoName
  if (!codigo) return []
  return DATOS
    .filter(m => m.deptoName === codigo)
    .map(m => capitalizar(m.mpioName))
    .sort((a, b) => a.localeCompare(b, 'es'))
}