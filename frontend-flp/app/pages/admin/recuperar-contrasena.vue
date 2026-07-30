<template>
  <div class="min-h-screen" style="background-color: #FFFCF6;">
    <header
      class="relative bg-cover bg-center h-95"
      style="background-image: url('/assets/images/FondoPruebaLogin.jpeg'); background-position: center 35%;"
    >
      <div class="bg-black/30 h-full">
        <nav class="max-w-6xl mx-auto flex items-center justify-between px-4 py-6 h-full">
          <div class="flex-1" />
        </nav>
      </div>
    </header>

    <div class="flex justify-center -mt-90 relative z-10">
      <img
        :src="logoUrl"
        alt="Flores Eternas"
        class="h-80 object-contain"
      />
    </div>

    <main class="relative -mt-10 px-4 pb-12">
      <div class="max-w-sm mx-auto">
        <UiCard :elevated="true" :padding="8">
          <div class="max-w-xs mx-auto flex flex-col justify-center py-6">

            <!-- ─── ESTADO 1: pedir codigo ─── -->
            <template v-if="paso === 1">
              <h2 class="text-lg font-['Poppins'] text-[#83572E] text-center mb-6">
                Recuperar contraseña
              </h2>

              <form @submit.prevent="solicitarCodigo" class="space-y-6">
                <UiInput
                  v-model="form.correo"
                  type="email"
                  label="Correo electrónico"
                  placeholder="tu@correo.com"
                  :error="errores.correo"
                />

                <div
                  v-if="errorVisible"
                  class="bg-red-50 border border-red-200 rounded-lg px-4 py-3 text-sm text-red-600 font-['Poppins']"
                >
                  {{ errorVisible }}
                </div>

                <div class="flex justify-center">
                  <UiButton
                    type="submit"
                    variant="primary"
                    size="lg"
                    :loading="loading"
                    :disabled="!correoValido || loading"
                    class="w-full"
                  >
                    {{ loading ? 'Enviando...' : 'Enviar código' }}
                  </UiButton>
                </div>

                <div class="flex justify-center">
                  <button
                    type="button"
                    class="text-sm text-[#83572E] hover:underline transition-colors font-['Poppins']"
                    @click="volverALogin"
                  >
                    ← Volver a iniciar sesión
                  </button>
                </div>
              </form>
            </template>

            <!-- ─── ESTADO 2: codigo + nueva contrasena ─── -->
            <template v-else-if="paso === 2">
              <h2 class="text-lg font-['Poppins'] text-[#83572E] text-center mb-2">
                Restablecer contraseña
              </h2>
              <p class="text-xs text-text-primary/70 text-center mb-6 font-['Poppins']">
                Si el correo está registrado, te enviamos un código de 6 dígitos.
                Revisa también tu carpeta de spam.
              </p>

              <form @submit.prevent="cambiarContrasena" class="space-y-6">
                <!-- OTP de 6 cuadraditos -->
                <div>
                  <label class="block text-sm font-medium font-['Poppins'] text-[#83572E] mb-2">
                    Código de verificación
                    <span class="text-red-500">*</span>
                  </label>
                  <div class="flex gap-2 justify-between" @paste="onPaste">
                    <input
                      v-for="(_, i) in otp"
                      :key="i"
                      :ref="(el) => setOtpRef(i, el as HTMLInputElement | null)"
                      v-model="otp[i]"
                      type="text"
                      inputmode="numeric"
                      maxlength="1"
                      pattern="[0-9]"
                      autocomplete="one-time-code"
                      :disabled="loading"
                      class="w-10 h-12 text-center text-lg font-mono rounded-lg border border-black bg-white text-[#83572E] focus:outline-none focus:ring-2 focus:ring-[#FFEDE3] focus:border-[#FFEDE3] disabled:opacity-50 transition-all"
                      @input="onOtpInput(i, $event)"
                      @keydown="onOtpKeydown(i, $event)"
                    />
                  </div>
                  <p
                    v-if="errores.codigo"
                    class="text-xs text-red-500 font-['Poppins'] mt-1"
                  >
                    {{ errores.codigo }}
                  </p>
                </div>

                <!-- Nueva contrasena con toggle mostrar/ocultar -->
                <div>
                  <label class="block text-sm font-medium font-['Poppins'] text-[#83572E] mb-1">
                    Nueva contraseña
                    <span class="text-red-500">*</span>
                  </label>
                  <div class="relative">
                    <input
                      v-model="form.nuevaContrasena"
                      :type="mostrarContrasena ? 'text' : 'password'"
                      maxlength="100"
                      :disabled="loading"
                      class="w-full rounded-lg border border-black bg-white px-3 py-2.5 pr-10 text-sm font-['Poppins'] text-[#83572E] placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-[#FFEDE3] focus:border-[#FFEDE3] disabled:opacity-50 transition-all"
                      placeholder="Mínimo 8 caracteres, letras y números"
                    />
                    <button
                      type="button"
                      :disabled="loading"
                      class="absolute right-2 top-1/2 -translate-y-1/2 p-1 text-[#83572E] hover:text-[#FFEDE3] disabled:opacity-50"
                      :aria-label="mostrarContrasena ? 'Ocultar contraseña' : 'Mostrar contraseña'"
                      @click="mostrarContrasena = !mostrarContrasena"
                    >
                      <Icon :icon="mostrarContrasena ? 'mdi:eye-off-outline' : 'mdi:eye-outline'" class="text-lg" />
                    </button>
                  </div>
                  <ul
                    v-if="form.nuevaContrasena"
                    class="text-xs font-['Poppins'] mt-2 space-y-0.5"
                  >
                    <li :class="cumpleLongitud ? 'text-green-600' : 'text-gray-500'">
                      {{ cumpleLongitud ? '✓' : '·' }} Mínimo 8 caracteres
                    </li>
                    <li :class="cumpleMezcla ? 'text-green-600' : 'text-gray-500'">
                      {{ cumpleMezcla ? '✓' : '·' }} Mezcla letras y números
                    </li>
                  </ul>
                </div>

                <div
                  v-if="errorVisible"
                  class="bg-red-50 border border-red-200 rounded-lg px-4 py-3 text-sm text-red-600 font-['Poppins']"
                >
                  {{ errorVisible }}
                </div>

                <div class="flex justify-center">
                  <UiButton
                    type="submit"
                    variant="primary"
                    size="lg"
                    :loading="loading"
                    :disabled="!formularioCompleto || loading"
                    class="w-full"
                  >
                    {{ loading ? 'Procesando...' : 'Aceptar' }}
                  </UiButton>
                </div>

                <div class="flex justify-center">
                  <button
                    type="button"
                    class="text-sm text-[#83572E] hover:underline transition-colors font-['Poppins']"
                    :disabled="loading"
                    @click="volverAPaso1"
                  >
                    ← Cambiar correo
                  </button>
                </div>
              </form>
            </template>

            <!-- ─── ESTADO 3: exito ─── -->
            <template v-else>
              <div class="flex flex-col items-center text-center py-4">
                <Icon icon="mdi:check-circle" class="text-5xl text-green-500 mb-3" />
                <h2 class="text-lg font-['Poppins'] text-[#83572E] mb-2">
                  Recuperación exitosa
                </h2>
                <p class="text-sm text-[#83572E]/70 font-['Poppins'] mb-6">
                  Tu contraseña fue cambiada con éxito. Ya podés iniciar sesión con tus nuevas credenciales.
                </p>
                <UiButton
                  variant="primary"
                  size="lg"
                  class="w-full"
                  @click="volverALogin"
                >
                  Iniciar sesión
                </UiButton>
              </div>
            </template>

          </div>
        </UiCard>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
/**
 * @author esteban
 * Pagina de recuperacion de contrasena para la cuenta administradora.
 * Tiene 3 estados manejados con la variable reactiva `paso`:
 *   1. Solicitud de codigo (ingreso de correo)
 *   2. Ingreso de codigo OTP de 6 digitos + nueva contrasena
 *   3. Confirmacion de exito con boton para volver al login
 *
 * El componente OTP de 6 cuadraditos esta inline para mantener
 * todo el flujo de UI en un solo archivo (consistente con la
 * pagina de login y su layout simple).
 */
import { computed, nextTick, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '~/services/auth.service'

definePageMeta({ layout: false })

const router = useRouter()

const paso = ref<1 | 2 | 3>(1)
const loading = ref(false)
const errorVisible = ref('')
const mostrarContrasena = ref(false)

const form = reactive({
  correo: '',
  nuevaContrasena: '',
})

const errores = reactive({
  correo: '',
  codigo: '',
})

const otp = ref<string[]>(['', '', '', '', '', ''])
const otpRefs = ref<(HTMLInputElement | null)[]>([])

function setOtpRef(i: number, el: HTMLInputElement | null) {
  otpRefs.value[i] = el
}

/* ─── Validaciones reactivas ─── */

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const correoValido = computed(() => emailRegex.test(form.correo.trim()))

const otpCompleto = computed(() => otp.value.every((d) => d.length === 1))

const cumpleLongitud = computed(() => form.nuevaContrasena.length >= 8)
const cumpleMezcla = computed(
  () => /[A-Za-z]/.test(form.nuevaContrasena) && /\d/.test(form.nuevaContrasena)
)
const contrasenaValida = computed(() => cumpleLongitud.value && cumpleMezcla.value)

const formularioCompleto = computed(
  () => otpCompleto.value && contrasenaValida.value
)

const logoUrl = computed(() => {
  if (typeof window !== 'undefined') {
    return new URL('/assets/images/flplogowhite.png', window.location.origin).href
  }
  return '/assets/images/flplogowhite.png'
})

/* ─── Logica de UI: navegacion entre pasos ─── */

function volverALogin() {
  router.push('/admin/login')
}

function resetear() {
  paso.value = 1
  errorVisible.value = ''
  form.correo = ''
  form.nuevaContrasena = ''
  otp.value = ['', '', '', '', '', '']
  errores.correo = ''
  errores.codigo = ''
  mostrarContrasena.value = false
}

function volverAPaso1() {
  paso.value = 1
  errorVisible.value = ''
  otp.value = ['', '', '', '', '', '']
  errores.codigo = ''
}

/* ─── Paso 1: solicitar codigo ─── */

async function solicitarCodigo() {
  errores.correo = ''
  errorVisible.value = ''

  if (!correoValido.value) {
    errores.correo = 'Formato de correo inválido'
    return
  }

  loading.value = true
  try {
    await authService.solicitarCodigoRecuperacion({ correo: form.correo.trim() })
    paso.value = 2
    await nextTick()
    otpRefs.value[0]?.focus()
  } catch (e) {
    errorVisible.value =
      e instanceof Error ? e.message : 'No se pudo enviar el código. Intenta nuevamente.'
  } finally {
    loading.value = false
  }
}

/* ─── Paso 2: codigo OTP ─── */

function focusSiguiente(i: number) {
  const next = otpRefs.value[i + 1]
  if (next) next.focus()
}

function focusAnterior(i: number) {
  const prev = otpRefs.value[i - 1]
  if (prev) prev.focus()
}

function onOtpInput(i: number, event: Event) {
  const target = event.target as HTMLInputElement
  const valor = target.value.replace(/\D/g, '')
  otp.value[i] = valor.slice(-1)
  if (otp.value[i] && i < 5) {
    focusSiguiente(i)
  }
}

function onOtpKeydown(i: number, event: KeyboardEvent) {
  if (event.key === 'Backspace' && !otp.value[i] && i > 0) {
    focusAnterior(i)
  } else if (event.key === 'ArrowLeft' && i > 0) {
    event.preventDefault()
    focusAnterior(i)
  } else if (event.key === 'ArrowRight' && i < 5) {
    event.preventDefault()
    focusSiguiente(i)
  }
}

function onPaste(event: ClipboardEvent) {
  event.preventDefault()
  const data = event.clipboardData?.getData('text') ?? ''
  const digitos = data.replace(/\D/g, '').slice(0, 6).split('')
  for (let i = 0; i < 6; i++) {
    otp.value[i] = digitos[i] ?? ''
  }
  const lastFilled = Math.min(digitos.length, 6) - 1
  const targetIdx = lastFilled >= 0 ? Math.min(lastFilled + 1, 5) : 0
  nextTick(() => otpRefs.value[targetIdx]?.focus())
}

/* ─── Paso 2: cambiar contrasena ─── */

async function cambiarContrasena() {
  errores.codigo = ''
  errorVisible.value = ''

  if (!otpCompleto.value) {
    errores.codigo = 'Ingresa los 6 dígitos del código'
    return
  }
  if (!contrasenaValida.value) {
    errorVisible.value = 'La contraseña debe tener mínimo 8 caracteres y mezclar letras y números'
    return
  }

  const codigo = otp.value.join('')
  loading.value = true
  try {
    await authService.cambiarContrasena({
      correo: form.correo.trim(),
      codigo,
      nuevaContrasena: form.nuevaContrasena,
    })
    paso.value = 3
  } catch (e) {
    errorVisible.value =
      e instanceof Error ? e.message : 'No se pudo restablecer la contraseña. Intenta nuevamente.'
  } finally {
    loading.value = false
  }
}
</script>
