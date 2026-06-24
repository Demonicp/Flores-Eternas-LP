<template>
  <div v-if="store.modoCheckout === 'confirm'" class="flex-1 overflow-y-auto px-5 py-6">
    <div class="flex flex-col items-center text-center gap-4 py-8">
      <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center">
        <Icon icon="mdi:check-circle-outline" class="text-3xl text-green-600" />
      </div>
      <p class="text-text-primary font-medium text-lg">{{ store.respuestaPedido?.mensaje }}</p>
      <div class="w-full text-left space-y-2 text-sm text-text-primary/80 mt-4 bg-bg-card rounded-xl p-4">
        <p><strong>Pedido #{{ store.respuestaPedido?.id }}</strong></p>
        <p>Total: ${{ formatoPrecio(store.respuestaPedido?.total ?? 0) }} COP</p>
        <p>Pagado: ${{ formatoPrecio(store.respuestaPedido?.montoPagado ?? 0) }} COP</p>
        <p v-if="(store.respuestaPedido?.montoPendiente ?? 0) > 0">Pendiente: ${{ formatoPrecio(store.respuestaPedido?.montoPendiente ?? 0) }} COP</p>
        <p>Entrega: {{ store.respuestaPedido?.fechaEntrega }}</p>
      </div>
    </div>
    <button class="w-full mt-6 bg-btn-primary text-btn-primary-text px-4 py-3 rounded-full text-sm font-medium hover:opacity-90 transition-opacity" @click="$emit('cerrarYLimpiar')">
      Seguir comprando
    </button>
  </div>

  <template v-else-if="store.items.length === 0 && store.personalizados.length === 0">
    <div class="flex-1 flex flex-col items-center justify-center text-text-primary/50 gap-3 px-5">
      <Icon icon="mdi:cart-outline" class="text-5xl" />
      <p class="text-sm">Tu carrito está vacío</p>
    </div>
  </template>

  <template v-else-if="store.modoCheckout === 'cart'">
    <div class="flex-1 overflow-y-auto px-5 py-4 space-y-4">
      <!-- Items predefinidos -->
      <div v-for="item in store.items" :key="item.ramo.id" class="flex gap-3 items-center">
        <div class="w-16 h-16 rounded-lg bg-bg-card flex items-center justify-center overflow-hidden flex-shrink-0">
          <img v-if="item.ramo.foto" :src="item.ramo.foto" :alt="item.ramo.nombre" class="w-full h-full object-cover" />
          <Icon v-else icon="mdi:flower-tulip-outline" class="text-2xl text-text-primary/30" />
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-text-primary truncate">{{ item.ramo.nombre }}</p>
          <p class="text-xs text-text-primary/60">${{ formatoPrecio(item.ramo.precio) }} COP c/u</p>
        </div>
        <div class="flex items-center gap-2">
          <button class="w-7 h-7 rounded-full border border-gray-200 flex items-center justify-center text-text-primary/60 hover:border-gray-400 transition-colors" @click="store.cambiarCantidad(item.ramo.id, -1)">
            <Icon icon="mdi:minus" class="text-sm" />
          </button>
          <span class="w-6 text-center text-sm font-medium">{{ item.cantidad }}</span>
          <button class="w-7 h-7 rounded-full border border-gray-200 flex items-center justify-center text-text-primary/60 hover:border-gray-400 transition-colors" @click="store.cambiarCantidad(item.ramo.id, 1)">
            <Icon icon="mdi:plus" class="text-sm" />
          </button>
        </div>
        <button class="text-text-primary/40 hover:text-red-500 transition-colors p-1" @click="store.eliminarItem(item.ramo.id)">
          <Icon icon="mdi:delete-outline" class="text-lg" />
        </button>
      </div>

      <!-- Items personalizados -->
      <div v-for="p in store.personalizados" :key="p.id" class="flex gap-3 items-center bg-bg-card/50 rounded-lg p-2">
        <div class="w-16 h-16 rounded-lg bg-gradient-to-br from-purple-200 to-pink-200 flex items-center justify-center flex-shrink-0">
          <Icon icon="mdi:flower-tulip" class="text-2xl text-purple-600" />
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-text-primary truncate">{{ p.nombre }}</p>
          <p class="text-xs text-text-primary/60">{{ p.flores.length }} flor(es){{ p.adiciones.length > 0 ? ` + ${p.adiciones.length} adición(es)` : '' }}</p>
          <p class="text-xs text-text-primary/70 font-medium">${{ formatoPrecio(p.precio) }} COP</p>
        </div>
        <button class="text-text-primary/40 hover:text-red-500 transition-colors p-1" @click="store.eliminarPersonalizado(p.id)">
          <Icon icon="mdi:delete-outline" class="text-lg" />
        </button>
      </div>
    </div>
    <div class="border-t border-border-soft px-5 py-4 space-y-3">
      <div class="flex justify-between text-sm font-medium text-text-primary">
        <span>Subtotal</span>
        <span>${{ formatoPrecio(store.subtotal) }} COP</span>
      </div>
      <button class="w-full bg-btn-primary text-btn-primary-text px-4 py-3 rounded-full text-sm font-medium hover:opacity-90 transition-opacity" @click="store.modoCheckout = 'checkout'">
        Continuar
      </button>
    </div>
  </template>

  <template v-else>
    <div class="flex-1 overflow-y-auto px-5 py-4 space-y-5">
      <!-- Resumen -->
      <div class="bg-gradient-to-r from-bg-card/60 to-transparent rounded-xl px-4 py-3 border border-border-soft/50">
        <p class="font-semibold text-text-primary text-base">Resumen del pedido</p>
        <p class="text-text-primary/70 text-sm mt-0.5">{{ store.totalItems }} producto(s) — <span class="font-medium text-text-primary">${{ formatoPrecio(store.subtotal) }} COP</span></p>
      </div>

      <!-- Formulario -->
      <div class="space-y-4">
        <div v-for="field in campos" :key="field.key">
          <label :for="'field-' + field.key" class="text-sm font-medium text-text-primary/80 block mb-1.5 flex items-center gap-1.5">
            <Icon :icon="field.icon" class="text-base text-text-primary/60" />
            {{ field.label }}
            <span v-if="field.obligatorio" class="text-red-400 text-xs">*</span>
          </label>
          <input
            :id="'field-' + field.key"
            v-model="store.checkoutForm[field.key]"
            :type="field.type"
            :placeholder="field.placeholder"
            :min="field.key === 'fechaEntrega' ? hoyStr : undefined"
            class="w-full px-4 py-3 border-2 rounded-xl text-[15px] transition-all duration-200 bg-white outline-none"
            :class="fieldClase(field.key)"
            @blur="validarCampo(field.key)"
            @input="limpiarError(field.key)"
            @focus="fieldFocus = field.key"
          />
          <p v-if="fieldErrors[field.key]" class="text-xs text-red-500 mt-1 flex items-center gap-1">
            <Icon icon="mdi:alert-circle-outline" class="text-sm" />
            {{ fieldErrors[field.key] }}
          </p>
        </div>

        <p v-if="store.errorMsg && !Object.keys(fieldErrors).length" class="text-sm text-red-500 flex items-center gap-1.5 bg-red-50 rounded-lg px-3 py-2">
          <Icon icon="mdi:alert-circle-outline" class="text-base" />
          {{ store.errorMsg }}
        </p>
      </div>
    </div>

    <!-- Botones -->
    <div class="border-t border-border-soft px-5 py-4 space-y-2.5 bg-gradient-to-t from-bg-page/50 to-transparent">
      <button
        class="w-full bg-btn-primary text-btn-primary-text px-4 py-3.5 rounded-xl text-[15px] font-semibold hover:opacity-90 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md active:scale-[0.98]"
        :disabled="store.cargando"
        @click="handleSubmit"
      >
        <div class="flex items-center justify-center gap-2">
          <Icon v-if="store.cargando" icon="mdi:loading" class="text-lg animate-spin" />
          <Icon v-else icon="mdi:check-circle-outline" class="text-lg" />
          <span>{{ store.cargando ? 'Procesando...' : 'Realizar Pedido' }}</span>
        </div>
      </button>
      <button class="w-full text-sm text-text-primary/50 hover:text-text-primary transition-colors py-1.5 flex items-center justify-center gap-1" @click="store.modoCheckout = 'cart'">
        <Icon icon="mdi:arrow-left" class="text-base" />
        Volver al carrito
      </button>
    </div>
  </template>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useCartStore } from '../../stores/cart.store'

const store = useCartStore()

const emit = defineEmits<{
  realizarPedido: []
  cerrarYLimpiar: []
}>()

const hoyStr = computed(() => new Date().toISOString().split('T')[0])
const fieldFocus = ref<string | null>(null)
const fieldErrors = ref<Record<string, string>>({})

type CampoKey = 'nombre' | 'email' | 'cedula' | 'telefono' | 'direccion' | 'fechaEntrega'

const camposBase: { key: CampoKey; label: string; icon: string; type: string; placeholder: string; obligatorio: boolean }[] = [
  { key: 'nombre', label: 'Nombre completo', icon: 'mdi:account-outline', type: 'text', placeholder: '¿Quién recibe el ramo?', obligatorio: true },
  { key: 'email', label: 'Correo electrónico', icon: 'mdi:email-outline', type: 'email', placeholder: 'correo@ejemplo.com', obligatorio: true },
  { key: 'cedula', label: 'Cédula', icon: 'mdi:card-account-details-outline', type: 'text', placeholder: 'Número de cédula', obligatorio: false },
  { key: 'telefono', label: 'Teléfono', icon: 'mdi:phone-outline', type: 'tel', placeholder: '300 123 4567', obligatorio: false },
  { key: 'direccion', label: 'Dirección de entrega', icon: 'mdi:map-marker-outline', type: 'text', placeholder: 'Calle 123 #45-67', obligatorio: true },
  { key: 'fechaEntrega', label: 'Fecha de entrega', icon: 'mdi:calendar-outline', type: 'date', placeholder: '', obligatorio: true },
]

const campos = computed(() =>
  camposBase.filter(c => c.key !== 'cedula' && c.key !== 'telefono' || store.tienePersonalizados)
)

function fieldClase(key: string): string {
  const base = 'border-gray-200 focus:border-btn-primary focus:ring-2 focus:ring-btn-primary/30 '
  if (fieldErrors.value[key]) return base + 'border-red-300 focus:border-red-400 focus:ring-red-200'
  if (fieldFocus.value === key) return base + 'border-btn-primary'
  return base
}

function validarCampo(key: string) {
  const val = String(store.checkoutForm[key as CampoKey] ?? '').trim()
  const label = camposBase.find(c => c.key === key)?.label ?? key
  if (!val) {
    fieldErrors.value[key] = `${label} es obligatorio.`
    return false
  }
  if (key === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) {
    fieldErrors.value[key] = 'Correo electrónico no válido.'
    return false
  }
  if (key === 'telefono' && !/^[0-9+\s\-()]{7,15}$/.test(val)) {
    fieldErrors.value[key] = 'Teléfono no válido.'
    return false
  }
  if (key === 'cedula' && !/^[0-9]{4,15}$/.test(val)) {
    fieldErrors.value[key] = 'Cédula no válida (solo números).'
    return false
  }
  delete fieldErrors.value[key]
  return true
}

function limpiarError(key: string) {
  if (fieldErrors.value[key]) {
    delete fieldErrors.value[key]
  }
}

function validarTodo(): boolean {
  let ok = true
  for (const c of campos.value) {
    if (!validarCampo(c.key)) ok = false
  }
  if (store.tienePersonalizados) {
    for (const extra of ['cedula', 'telefono'] as CampoKey[]) {
      if (!validarCampo(extra)) ok = false
    }
  }
  return ok
}

function handleSubmit() {
  if (!validarTodo()) return
  emit('realizarPedido')
}

function formatoPrecio(valor: number): string {
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(valor)
}
</script>
