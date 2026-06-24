import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { inventarioService } from '~/services/inventario.service'
import type { Inventario } from '~/models/inventario.model'

export const useInventarioStore = defineStore('inventario', () => {

  const nombre = ref('')
  const descripcion = ref('')
  const precioCosto = ref<number | null>(null)
  const stock = ref<number | null>(null)

  const editandoId = ref<number | null>(null)
  const esEdicion = computed(() => editandoId.value !== null)

  const productos = ref<Inventario[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function cargarInventario() {
    loading.value = true

    try {
      productos.value = await inventarioService.listar()
    } catch (e) {
      error.value = 'Error al cargar inventario'
    } finally {
      loading.value = false
    }
  }

  function resetForm() {

    editandoId.value = null
  
    nombre.value = ''
  
    descripcion.value = ''
  
    precioCosto.value = null
  
    stock.value = null
  }
  async function guardarProducto() {

    if (
      precioCosto.value === null ||
      stock.value === null
    ) {
      error.value = 'Debe ingresar precio y stock'
      return
    }
  
    const payload: Partial<Inventario> = {
      nombre: nombre.value,
      descripcion: descripcion.value,
      precioCosto: precioCosto.value,
      stock: stock.value
    }
  
    if (editandoId.value) {
      await inventarioService.actualizar(
        editandoId.value,
        payload
      )
    } else {
      await inventarioService.crear(payload)
    }
  
    await cargarInventario()
  
    resetForm()
  }
  
  function editarProducto(producto: Inventario) {

    editandoId.value = producto.id
  
    nombre.value = producto.nombre
  
    descripcion.value = producto.descripcion
  
    precioCosto.value = producto.precioCosto
  
    stock.value = producto.stock
  }

  async function eliminarProducto(id: number) {

    await inventarioService.eliminar(id)
  
    await cargarInventario()
  }

  return {
    productos,
    loading,
    error,

    nombre,
    descripcion,
    precioCosto,
    stock,

    editandoId,
    esEdicion,

    cargarInventario,
    guardarProducto,
    editarProducto,
    eliminarProducto,
    resetForm
  }
})