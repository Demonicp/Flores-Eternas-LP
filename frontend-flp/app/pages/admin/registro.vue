<!--
  @author esteban
  Página de registro del administrador.
  Formulario para crear la primera cuenta de administrador del sistema.
  Solo funciona cuando no existe ningún otro administrador registrado.
  Protegida por middleware: si ya hay sesión, redirige a /admin/dashboard.
-->
<template>
  <div class="min-h-screen" style="background-color: #FFFCF6;">
    <header
      class="relative bg-cover bg-center h-80"
      style="background-image: url('/assets/images/FondoPruebaLogin.jpeg'); background-position: center 35%;"
    >
      <div class="bg-black/30 h-full">
        <nav class="max-w-6xl mx-auto flex items-center justify-between px-4 py-6 h-full">
          <div class="flex-1" />
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
          <div class="max-w-xs mx-auto flex flex-col justify-center py-8">
            <form @submit.prevent="handleRegistro" class="space-y-8">
              <UiInput
                v-model="form.nombre"
                type="text"
                placeholder="Nombre completo"
                :error="errores.nombre"
              />

              <UiInput
                v-model="form.correo"
                type="email"
                placeholder="Correo electrónico"
                :error="errores.correo"
              />

              <UiInput
                v-model="form.contrasena"
                type="password"
                placeholder="Contraseña"
                :error="errores.contrasena"
              />

              <UiInput
                v-model="form.confirmar"
                type="password"
                placeholder="Confirmar contraseña"
                :error="errores.confirmar"
              />

              <div
                v-if="store.error"
                class="bg-red-50 border border-red-200 rounded-lg px-4 py-3 text-sm text-red-600 font-['Poppins']"
              >
                {{ store.error }}
              </div>

              <div class="flex justify-center pt-4">
                <UiButton
                  type="submit"
                  variant="primary"
                  size="lg"
                  :loading="store.loading"
                  :disabled="!formValido || store.loading"
                  class="w-full"
                >
                  {{ store.loading ? 'Creando cuenta...' : 'Crear Cuenta' }}
                </UiButton>
              </div>
            </form>
          </div>
        </UiCard>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
/**
 * @author esteban
 * Página de registro del administrador.
 * Utiliza el store de autenticación para crear la cuenta
 * y redirigir al login en caso de éxito.
 */

import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth.store'

definePageMeta({ layout: false })

const store = useAuthStore()
const router = useRouter()

/**
 * @author esteban
 * URL del logo que funciona tanto en local como en producción.
 * Usa new URL() para resolución correcta en ambos entornos.
 */
const logoUrl = computed(() => {
  if (typeof window !== 'undefined') {
    return new URL('/assets/images/flplogowhite.png', window.location.origin).href
  }
  return '/assets/images/flplogowhite.png'
})

/**
 * @author esteban
 * Datos del formulario de registro.
 */
const form = reactive({
  nombre: '',
  correo: '',
  contrasena: '',
  confirmar: '',
})

/**
 * @author esteban
 * Errores de validación de campos.
 */
const errores = reactive({
  nombre: '',
  correo: '',
  contrasena: '',
  confirmar: '',
})

/**
 * @author esteban
 * Valida el formulario antes de enviar.
 * @returns true si el formulario es válido, false en caso contrario.
 */
function validarFormulario(): boolean {
  errores.nombre = ''
  errores.correo = ''
  errores.contrasena = ''
  errores.confirmar = ''

  let valido = true

  if (!form.nombre.trim()) {
    errores.nombre = 'El nombre es obligatorio'
    valido = false
  }

  if (!form.correo.trim()) {
    errores.correo = 'El correo es obligatorio'
    valido = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.correo)) {
    errores.correo = 'Formato de correo inválido'
    valido = false
  }

  if (!form.contrasena) {
    errores.contrasena = 'La contraseña es obligatoria'
    valido = false
  } else if (form.contrasena.length < 6) {
    errores.contrasena = 'La contraseña debe tener al menos 6 caracteres'
    valido = false
  }

  if (form.contrasena !== form.confirmar) {
    errores.confirmar = 'Las contraseñas no coinciden'
    valido = false
  }

  return valido
}

/**
 * @author esteban
 * Computed que verifica si el formulario tiene datos mínimos para habilitar el botón.
 */
const formValido = computed(() => {
  return (
    form.nombre.trim().length > 0 &&
    form.correo.trim().length > 0 &&
    form.contrasena.length >= 6 &&
    form.confirmar.length > 0
  )
})

/**
 * @author esteban
 * Maneja el envío del formulario de registro.
 * Valida los campos y llama al store para registrarse.
 */
async function handleRegistro() {
  if (!validarFormulario()) return

  const success = await store.registrar({
    correo: form.correo,
    contrasena: form.contrasena,
    nombre: form.nombre,
  })

  if (success) {
    router.push('/admin/login')
  }
}
</script>