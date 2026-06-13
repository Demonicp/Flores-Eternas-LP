import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { RamoResponse, DetalleLineaForm } from '../models/ramo.model'
import type { CategoriaRamo } from '../models/categoria-ramo.model'
import type { TipoFlor } from '../models/tipo-flor.model'
import type { ColorFlor } from '../models/color-flor.model'
import { ramoService } from '../services/ramo.service'
import { categoriaRamoService } from '../services/categoria-ramo.service'
import { tipoFlorService } from '../services/tipo-flor.service'
import { colorFlorService } from '../services/color-flor.service'

export const useNegocioStore = defineStore('negocio', () => {

  /* ─── Sección A: Ramos ─── */
  const ramos = ref<RamoResponse[]>([])
  const ramoLoading = ref(false)
  const ramoError = ref<string | null>(null)

  const ramoEditandoId = ref<number | null>(null)
  const ramoFormNombre = ref('')
  const ramoFormPrecio = ref<number | null>(null)
  const ramoFormDescripcion = ref('')
  const ramoFormFoto = ref('')
  const ramoFormIdCategoria = ref<number | null>(null)
  const ramoFormDisponible = ref(true)
  const ramoFormStock = ref<number | null>(null)
  const ramoFormDetalles = ref<DetalleLineaForm[]>([])

  const esEdicionRamo = computed(() => ramoEditandoId.value !== null)

  function resetRamoForm() {
    ramoEditandoId.value = null
    ramoFormNombre.value = ''
    ramoFormPrecio.value = null
    ramoFormDescripcion.value = ''
    ramoFormFoto.value = ''
    ramoFormIdCategoria.value = null
    ramoFormDisponible.value = true
    ramoFormStock.value = null
    ramoFormDetalles.value = []
  }

  function agregarLineaDetalle() {
    ramoFormDetalles.value.push({ idTipoFlor: null, idColorFlor: null, cantidad: 1 })
  }

  function eliminarLineaDetalle(index: number) {
    ramoFormDetalles.value.splice(index, 1)
  }

  async function cargarRamos() {
    ramoLoading.value = true
    ramoError.value = null
    try {
      ramos.value = await ramoService.listar()
    } catch (e) {
      ramoError.value = e instanceof Error ? e.message : 'Error al cargar ramos'
    } finally {
      ramoLoading.value = false
    }
  }

  async function guardarRamo() {
    ramoLoading.value = true
    ramoError.value = null
    try {
      const detalles = ramoFormDetalles.value
        .filter(d => d.idTipoFlor && d.idColorFlor)
        .map(d => ({ cantidad: d.cantidad, idTipoFlor: d.idTipoFlor!, idColorFlor: d.idColorFlor! }))

      const payload = {
        nombreRamo: ramoFormNombre.value,
        precioRamo: ramoFormPrecio.value!,
        descripcionRamo: ramoFormDescripcion.value,
        fotoRamo: ramoFormFoto.value,
        idCategoriaRamo: ramoFormIdCategoria.value!,
        disponible: ramoFormDisponible.value,
        stock: ramoFormStock.value,
        detallesRamo: detalles,
      }

      if (ramoEditandoId.value) {
        await ramoService.actualizar(ramoEditandoId.value, payload)
      } else {
        await ramoService.crear(payload)
      }
      resetRamoForm()
      await cargarRamos()
    } catch (e) {
      ramoError.value = e instanceof Error ? e.message : 'Error al guardar ramo'
    } finally {
      ramoLoading.value = false
    }
  }

  function editarRamo(ramo: RamoResponse) {
    ramoEditandoId.value = ramo.id
    ramoFormNombre.value = ramo.nombreRamo
    ramoFormPrecio.value = ramo.precioRamo
    ramoFormDescripcion.value = ramo.descripcionRamo
    ramoFormFoto.value = ramo.fotoRamo
    ramoFormIdCategoria.value = ramo.categoria?.id ?? null
    ramoFormDisponible.value = ramo.disponible
    ramoFormStock.value = ramo.stock
    ramoFormDetalles.value = ramo.detallesRamo.map(d => ({
      idTipoFlor: d.tipoFlor.id,
      idColorFlor: d.colorFlor.id,
      cantidad: d.cantidad,
    }))
  }

  async function eliminarRamo(id: number) {
    ramoLoading.value = true
    ramoError.value = null
    try {
      await ramoService.desactivar(id)
      await cargarRamos()
    } catch (e) {
      ramoError.value = e instanceof Error ? e.message : 'Error al eliminar ramo'
    } finally {
      ramoLoading.value = false
    }
  }

  /* ─── Sección B: Categorías (eventos) ─── */
  const categorias = ref<CategoriaRamo[]>([])
  const catLoading = ref(false)
  const catError = ref<string | null>(null)

  const catFormNombre = ref('')
  const catEditandoId = ref<number | null>(null)

  const esEdicionCat = computed(() => catEditandoId.value !== null)

  function resetCatForm() {
    catEditandoId.value = null
    catFormNombre.value = ''
  }

  async function cargarCategorias() {
    catLoading.value = true
    catError.value = null
    try {
      categorias.value = await categoriaRamoService.listar()
    } catch (e) {
      catError.value = e instanceof Error ? e.message : 'Error al cargar categorías'
    } finally {
      catLoading.value = false
    }
  }

  async function guardarCategoria() {
    catLoading.value = true
    catError.value = null
    try {
      if (catEditandoId.value) {
        await categoriaRamoService.actualizar(catEditandoId.value, { descripcionCategoriaRamo: catFormNombre.value })
      } else {
        await categoriaRamoService.crear({ descripcionCategoriaRamo: catFormNombre.value })
      }
      resetCatForm()
      await Promise.all([
        cargarCategorias(),
        cargarCategoriasDisponibles(),
      ])
    } catch (e) {
      catError.value = e instanceof Error ? e.message : 'Error al guardar categoría'
    } finally {
      catLoading.value = false
    }
  }

  function editarCategoria(cat: CategoriaRamo) {
    catEditandoId.value = cat.id
    catFormNombre.value = cat.descripcionCategoriaRamo
  }

  async function eliminarCategoria(id: number) {
    catLoading.value = true
    catError.value = null
    try {
      await categoriaRamoService.eliminar(id)
      await Promise.all([
        cargarCategorias(),
        cargarCategoriasDisponibles(),
      ])
    } catch (e) {
      catError.value = e instanceof Error ? e.message : 'Error al eliminar categoría'
    } finally {
      catLoading.value = false
    }
  }

  /* ─── Sección C: Opciones personalizadas ─── */
  const opcionSeleccionada = ref<'color' | 'flor' | null>(null)

  /* Colores */
  const colores = ref<ColorFlor[]>([])
  const colLoading = ref(false)
  const colError = ref<string | null>(null)

  const colFormNombre = ref('')
  const colEditandoId = ref<number | null>(null)

  const esEdicionCol = computed(() => colEditandoId.value !== null)

  function resetColForm() {
    colEditandoId.value = null
    colFormNombre.value = ''
  }

  async function cargarColores() {
    colLoading.value = true
    colError.value = null
    try {
      colores.value = await colorFlorService.listar()
    } catch (e) {
      colError.value = e instanceof Error ? e.message : 'Error al cargar colores'
    } finally {
      colLoading.value = false
    }
  }

  async function guardarColor() {
    colLoading.value = true
    colError.value = null
    try {
      if (colEditandoId.value) {
        await colorFlorService.actualizar(colEditandoId.value, { descripcionColor: colFormNombre.value })
      } else {
        await colorFlorService.crear({ descripcionColor: colFormNombre.value })
      }
      resetColForm()
      await Promise.all([
        cargarColores(),
        cargarColoresDisponibles(),
      ])
    } catch (e) {
      colError.value = e instanceof Error ? e.message : 'Error al guardar color'
    } finally {
      colLoading.value = false
    }
  }

  function editarColor(color: ColorFlor) {
    colEditandoId.value = color.id
    colFormNombre.value = color.descripcionColor
  }

  async function eliminarColor(id: number) {
    colLoading.value = true
    colError.value = null
    try {
      await colorFlorService.eliminar(id)
      await Promise.all([
        cargarColores(),
        cargarColoresDisponibles(),
      ])
    } catch (e) {
      colError.value = e instanceof Error ? e.message : 'Error al eliminar color'
    } finally {
      colLoading.value = false
    }
  }

  /* Flores */
  const flores = ref<TipoFlor[]>([])
  const florLoading = ref(false)
  const florError = ref<string | null>(null)

  const florFormDescripcion = ref('')
  const florFormPrecio = ref<number | null>(null)
  const florFormPorcentaje = ref<number | null>(null)
  const florEditandoId = ref<number | null>(null)

  const esEdicionFlor = computed(() => florEditandoId.value !== null)

  function resetFlorForm() {
    florEditandoId.value = null
    florFormDescripcion.value = ''
    florFormPrecio.value = null
    florFormPorcentaje.value = null
  }

  async function cargarFlores() {
    florLoading.value = true
    florError.value = null
    try {
      flores.value = await tipoFlorService.listar()
    } catch (e) {
      florError.value = e instanceof Error ? e.message : 'Error al cargar flores'
    } finally {
      florLoading.value = false
    }
  }

  async function guardarFlor() {
    florLoading.value = true
    florError.value = null
    try {
      const payload = {
        descripcionFlor: florFormDescripcion.value,
        precioUnidad: florFormPrecio.value!,
        porcentajePorMayor: florFormPorcentaje.value!,
      }
      if (florEditandoId.value) {
        await tipoFlorService.actualizar(florEditandoId.value, payload)
      } else {
        await tipoFlorService.crear(payload)
      }
      resetFlorForm()
      await Promise.all([
        cargarFlores(),
        cargarTiposFlorDisponibles(),
      ])
    } catch (e) {
      florError.value = e instanceof Error ? e.message : 'Error al guardar flor'
    } finally {
      florLoading.value = false
    }
  }

  function editarFlor(flor: TipoFlor) {
    florEditandoId.value = flor.id
    florFormDescripcion.value = flor.descripcionFlor
    florFormPrecio.value = flor.precioUnidad
    florFormPorcentaje.value = flor.porcentajePorMayor
  }

  async function eliminarFlor(id: number) {
    florLoading.value = true
    florError.value = null
    try {
      await tipoFlorService.eliminar(id)
      await Promise.all([
        cargarFlores(),
        cargarTiposFlorDisponibles(),
      ])
    } catch (e) {
      florError.value = e instanceof Error ? e.message : 'Error al eliminar flor'
    } finally {
      florLoading.value = false
    }
  }

  /* ─── Datos compartidos para dropdowns ─── */
  const categoriasDisponibles = ref<CategoriaRamo[]>([])
  const tiposFlorDisponibles = ref<TipoFlor[]>([])
  const coloresDisponibles = ref<ColorFlor[]>([])

  async function cargarCategoriasDisponibles() {
    categoriasDisponibles.value = await categoriaRamoService.listar()
  }

  async function cargarTiposFlorDisponibles() {
    tiposFlorDisponibles.value = await tipoFlorService.listar()
  }

  async function cargarColoresDisponibles() {
    coloresDisponibles.value = await colorFlorService.listar()
  }

  async function cargarDatosCompartidos() {
    const [cats, tipos, cols] = await Promise.all([
      categoriaRamoService.listar(),
      tipoFlorService.listar(),
      colorFlorService.listar(),
    ])
    categoriasDisponibles.value = cats
    tiposFlorDisponibles.value = tipos
    coloresDisponibles.value = cols
  }

  const categoriasVisibles = computed(() =>
    categorias.value.filter(c => !/personalizado/i.test(c.descripcionCategoriaRamo))
  )

  const categoriasDisponiblesVisibles = computed(() =>
    categoriasDisponibles.value.filter(c => !/personalizado/i.test(c.descripcionCategoriaRamo))
  )

  const ramosVisibles = computed(() =>
    ramos.value.filter(r => r.disponible !== false)
      .filter(r => !r.categoria || !/personalizado/i.test(r.categoria.descripcionCategoriaRamo))
  )

  async function cargarTodo() {
    await Promise.all([
      cargarRamos(),
      cargarCategorias(),
      cargarColores(),
      cargarFlores(),
      cargarDatosCompartidos(),
    ])
  }

  return {
    ramos, ramoLoading, ramoError,
    ramoEditandoId, ramoFormNombre, ramoFormPrecio, ramoFormDescripcion,
    ramoFormFoto, ramoFormIdCategoria, ramoFormDisponible, ramoFormStock, ramoFormDetalles,
    esEdicionRamo,
    resetRamoForm, agregarLineaDetalle, eliminarLineaDetalle,
    cargarRamos, guardarRamo, editarRamo, eliminarRamo,

    categorias, catLoading, catError,
    catFormNombre, catEditandoId, esEdicionCat,
    resetCatForm,
    cargarCategorias, guardarCategoria, editarCategoria, eliminarCategoria,

    opcionSeleccionada,
    colores, colLoading, colError,
    colFormNombre, colEditandoId, esEdicionCol,
    resetColForm,
    cargarColores, guardarColor, editarColor, eliminarColor,
    flores, florLoading, florError,
    florFormDescripcion, florFormPrecio, florFormPorcentaje,
    florEditandoId, esEdicionFlor,
    resetFlorForm,
    cargarFlores, guardarFlor, editarFlor, eliminarFlor,

    categoriasDisponibles, tiposFlorDisponibles, coloresDisponibles,
    categoriasVisibles, categoriasDisponiblesVisibles, ramosVisibles,
    cargarDatosCompartidos, cargarTodo,
    cargarCategoriasDisponibles, cargarTiposFlorDisponibles, cargarColoresDisponibles,
  }
})
