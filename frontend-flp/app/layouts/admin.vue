<template>
  <div class="min-h-screen bg-cover bg-center bg-fixed" style="background-image: url('/assets/images/fondoAdmin.jpeg'); background-color: #FFFCF6;">
    <header class="relative">
      <div class="bg-transparent">
        <nav class="w-full grid grid-cols-[1fr_auto_1fr] items-center px-2 sm:px-4 py-3 sm:py-6">
          <div class="flex flex-wrap gap-x-2 sm:gap-x-6 gap-y-1 min-w-0">
            <NuxtLink
              to="/admin/pedidos"
              class="text-white text-[13px] sm:text-[15px] md:text-[17px] tracking-wide whitespace-nowrap"
              active-class="font-bold"
            >
              Pedidos
            </NuxtLink>
            <NuxtLink
              to="/admin/inventario"
              class="text-white text-[13px] sm:text-[15px] md:text-[17px] tracking-wide whitespace-nowrap"
              active-class="font-bold"
            >
              Inventario
            </NuxtLink>
            <NuxtLink
              to="/admin/catalogo"
              class="text-white text-[13px] sm:text-[15px] md:text-[17px] tracking-wide whitespace-nowrap"
              active-class="font-bold"
            >
              Catálogo
            </NuxtLink>
            <NuxtLink
              to="/admin/eventos"
              class="text-white text-[13px] sm:text-[15px] md:text-[17px] tracking-wide whitespace-nowrap"
              active-class="font-bold"
            >
              Eventos
            </NuxtLink>
            <NuxtLink
              to="/admin/personalizados"
              class="text-white text-[13px] sm:text-[15px] md:text-[17px] tracking-wide whitespace-nowrap"
              active-class="font-bold"
            >
              Personalizados
            </NuxtLink>
          </div>

          <NuxtLink to="/admin/pedidos">
            <img src="/assets/images/flplogowhite.png" alt="Flores Eternas" class="h-12 sm:h-20 md:h-36 w-auto justify-self-center" width="1536" height="1024" />
          </NuxtLink>

          <div class="flex items-center gap-2 sm:gap-6 justify-self-end">
            <span class="text-white text-[13px] sm:text-[15px] md:text-[17px] tracking-wide hidden md:inline">Empleados</span>
            <span class="text-white text-[13px] sm:text-[15px] md:text-[17px] tracking-wide hidden md:inline">Clientes</span>
            <span class="text-white text-[11px] sm:text-[13px] md:text-[17px] tracking-wide hidden md:inline">Cierre de caja</span>
            <Icon icon="mdi:account-circle-outline" class="text-white text-xl sm:text-2xl cursor-default" />
          </div>
        </nav>

        <div class="text-center pb-12 sm:pb-16 md:pb-32 pt-6 sm:pt-8 md:pt-14">
          <h1
            class="text-white font-serif text-2xl sm:text-3xl md:text-4xl md:text-[43px] font-light"
            style="text-shadow: 0 2px 8px rgba(0,0,0,0.4);"
          >
            ¡Bienvenida de nuevo, luisa!
          </h1>
        </div>
      </div>
    </header>

    <main class="relative -mt-12 px-6 pb-6">
      <div class="max-w-6xl mx-auto">
        <slot />
      </div>
    </main>
  </div>
  <Toast />
</template>

<script setup lang="ts">
import Toast from '~/components/shared/Toast.vue'
</script>
