/**
 * Modelo de local (punto de retiro) del catalogo publico.
 * Refleja la entidad `Local` del backend, usada en el checkout para
 * la opcion de retirar el pedido en un local fisico.
 *
 * @author esteban
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
export type Local = {
  id: number
  nombreLocal: string
  direccion: string
  ciudad: string
  region: string
  activo: boolean
}