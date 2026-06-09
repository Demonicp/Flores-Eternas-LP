<!--
  @author esteban
  Página de registro del administrador.
  Formulario para crear la primera cuenta de administrador del sistema.
  Solo funciona cuando no existe ningún otro administrador registrado.
  Protegida por middleware: si ya hay sesión, redirige a /admin/dashboard.
-->
<template>
  <div class="min-h-screen" style="background-color: #FFFCF6;">
    <!-- Header con imagen de fondo -->
    <header
      class="relative bg-cover bg-center"
      style="background-image: url('/assets/images/landing.jpg'); background-position: center 35%;"
    >
      <div class="bg-black/30">
        <nav class="max-w-6xl mx-auto flex items-center justify-between px-4 py-6">
          <NuxtLink to="/">
            <img
              src="/assets/images/flplogowhite.png"
              alt="Flores Eternas"
              class="h-12 object-contain"
            />
          </NuxtLink>
          <div class="flex items-center gap-6">
            <span class="text-white font-['Poppins'] text-[17px] tracking-wide">
              ¿Ya tienes cuenta?
            </span>
            <NuxtLink
              to="/admin/login"
              class="px-4 py-2 rounded-lg bg-[#FFEDE3] text-[#83572E] font-['Poppins'] text-sm font-medium hover:opacity-90 transition-opacity"
            >
              Inicia Sesión
            </NuxtLink>
          </div>
        </nav>

        <div class="text-center pb-24 pt-16">
          <h1
            class="text-white font-['Radley'] text-4xl md:text-[43px] font-light"
            style="text-shadow: 0 2px 8px rgba(0,0,0,0.4);"
          >
            ¡Bienvenida!
          </h1>
          <p
            class="text-white/90 font-['Poppins'] text-lg mt-2"
            style="text-shadow: 0 1px 4px rgba(0,0,0,0.3);"
          >
            Crea tu cuenta para comenzar a gestionar tu negocio
          </p>
        </div>
      </div>
    </header>

    <!-- Formulario de registro -->
    <main class="relative -mt-16 px-4 pb-12">
      <div class="max-w-md mx-auto">
        <UiCard :elevated="true" :padding="8">
          <form @submit.prevent="handleRegistro" class="space-y-5">
            <h2 class="font-['Radley'] text-2xl text-[#83572E] text-center mb-2">
              Crear Cuenta
            </h2>

            <!-- Nombre completo -->
            <UiInput
              v-model="form.nombre"
              type="text"
              label="Nombre completo"
              placeholder="Tu nombre"
              :error="errores.nombre"
              required
            />

            <!-- Correo -->
            <UiInput
              v-model="form.correo"
              type="email"
              label="Correo electrónico"
              placeholder="tu@email.com"
              :error="errores.correo"
              required
            />

            <!-- Contraseña -->
            <UiInput
              v-model="form.contrasena"
              type="password"
              label="Contraseña"
              placeholder="Mínimo 6 caracteres"
              :error="errores.contrasena"
              required
            />

            <!-- Confirmar contraseña -->
            <UiInput
              v-model="form.confirmar"
              type="password"
              label="Confirmar contraseña"
              placeholder="Repite tu contraseña"
              :error="errores.confirmar"
              required
            />

            <!-- Mensaje de error del servidor -->
            <div
              v-if="store.error"
              class="bg-red-50 border border-red-200 rounded-lg px-4 py-3 text-sm text-red-600 font-['Poppins']"
            >
              {{ store.error }}
            </div>

            <!-- Botón submit -->
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
          </form>
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