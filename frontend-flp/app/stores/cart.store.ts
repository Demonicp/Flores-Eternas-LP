import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ItemCarrito, PedidoRequest, PedidoResponse, PersonalizadoCarrito } from '../models/pedido.model'
import { pedidoService } from '../services/pedido.service'

export const useCartStore = defineStore('cart', () => {
  const items = ref<ItemCarrito[]>([])
  const personalizados = ref<PersonalizadoCarrito[]>([])
  const abierto = ref(false)
  const vista = ref<'sidebar' | 'overlay'>('sidebar')
  const cargando = ref(false)
  const respuestaPedido = ref<PedidoResponse | null>(null)

  const checkoutForm = ref({
    nombre: '',
    email: '',
    direccion: '',
    fechaEntrega: '',
    cedula: '',
    telefono: '',
  })
  const errorMsg = ref('')
  const modoCheckout = ref<'cart' | 'checkout' | 'confirm'>('cart')

  const totalItems = computed(() =>
    items.value.reduce((sum, i) => sum + i.cantidad, 0) + personalizados.value.length
  )

  const subtotal = computed(() =>
    items.value.reduce((sum, i) => sum + i.ramo.precio * i.cantidad, 0) +
    personalizados.value.reduce((sum, p) => sum + p.precio, 0)
  )

  const tienePersonalizados = computed(() => personalizados.value.length > 0)

  function toggleCarrito() {
    abierto.value = !abierto.value
  }

  function abrirCarrito() {
    abierto.value = true
  }

  function cerrarCarrito() {
    abierto.value = false
  }

  function abrirOverlay() {
    vista.value = 'overlay'
    abierto.value = true
  }

  function cerrarOverlay() {
    vista.value = 'sidebar'
    abierto.value = false
  }

  function agregarItem(ramo: { id: number; nombre: string; foto: string; precio: number }) {
    const existente = items.value.find(i => i.ramo.id === ramo.id)
    if (existente) {
      existente.cantidad++
    } else {
      items.value.push({ ramo, cantidad: 1 })
    }
  }

  function eliminarItem(idRamo: number) {
    items.value = items.value.filter(i => i.ramo.id !== idRamo)
  }

  function agregarPersonalizado(p: PersonalizadoCarrito) {
    personalizados.value.push(p)
  }

  function eliminarPersonalizado(id: string) {
    personalizados.value = personalizados.value.filter(p => p.id !== id)
  }

  function cambiarCantidad(idRamo: number, delta: number) {
    const item = items.value.find(i => i.ramo.id === idRamo)
    if (!item) return
    const nueva = item.cantidad + delta
    if (nueva <= 0) {
      eliminarItem(idRamo)
    } else {
      item.cantidad = nueva
    }
  }

  function limpiarCarrito() {
    items.value = []
    personalizados.value = []
    respuestaPedido.value = null
    checkoutForm.value = { nombre: '', email: '', direccion: '', fechaEntrega: '', cedula: '', telefono: '' }
    errorMsg.value = ''
    modoCheckout.value = 'cart'
  }

  async function realizarPedido(data: PedidoRequest): Promise<PedidoResponse> {
    cargando.value = true
    try {
      const body: PedidoRequest = {
        ...data,
        floresPersonalizadas: personalizados.value.length > 0
          ? personalizados.value.flatMap(p => p.flores.map(f => ({
              tipoFlorId: f.tipoFlorId,
              colorFlorId: f.colorFlorId,
              cantidad: f.cantidad,
            })))
          : undefined,
        adicionesPersonalizadas: personalizados.value.length > 0
          ? personalizados.value.flatMap(p => p.adiciones.map(a => ({
              inventarioId: a.inventarioId,
              cantidad: a.cantidad,
            })))
          : undefined,
        cedulaCliente: checkoutForm.value.cedula || undefined,
        telefonoCliente: checkoutForm.value.telefono || undefined,
      }
      const res = await pedidoService.crearPedido(body)
      respuestaPedido.value = res
      return res
    } finally {
      cargando.value = false
    }
  }

  return {
    items,
    personalizados,
    abierto,
    vista,
    cargando,
    respuestaPedido,
    checkoutForm,
    errorMsg,
    modoCheckout,
    totalItems,
    subtotal,
    tienePersonalizados,
    toggleCarrito,
    abrirCarrito,
    cerrarCarrito,
    abrirOverlay,
    cerrarOverlay,
    agregarItem,
    eliminarItem,
    cambiarCantidad,
    agregarPersonalizado,
    eliminarPersonalizado,
    limpiarCarrito,
    realizarPedido,
  }
})
