<template>
  <header class="relative w-full bg-bg-page overflow-hidden">
    <div class="absolute inset-0">
      <img
        src="/assets/images/FondoProvicional.png"
        alt=""
        class="w-full h-full object-cover"
      />
      <div class="absolute inset-0 bg-white/60" />
    </div>

    <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-[1fr_auto_1fr] items-center min-h-[200px]">

        <nav class="flex items-center gap-6">
          <NuxtLink
            to="/"
            class="text-[17px] tracking-wide text-black hover:text-black/80 transition-colors duration-200 hidden sm:block font-poppins"
            exact-active-class="font-bold"
            active-class=""
          >
            Entrega inmediata
          </NuxtLink>
          <NuxtLink
            to="/flor/SeleccionFlor"
            class="text-[17px] tracking-wide text-black hover:text-black/80 transition-colors duration-200 hidden sm:block font-poppins"
            :class="{ 'font-bold': esPersonalizado }"
          >
            Personalizado
          </NuxtLink>
          <a
            href="#"
            class="text-[17px] tracking-wide text-black hover:text-black/80 transition-colors duration-200 hidden sm:block font-poppins"
          >
            Conócenos
          </a>
        </nav>

        <div class="flex-shrink-0 justify-self-center">
          <NuxtLink to="/">
            <img
              src="/assets/images/flplogoblack.png"
              alt="Flores Eternas LP"
              class="h-36 w-auto object-contain cursor-pointer"
            />
          </NuxtLink>
        </div>

        <nav class="flex items-center gap-6 justify-self-end">
          <button class="text-black hover:text-black/70 transition-colors duration-200 p-1" aria-label="Buscar">
            <Icon icon="mdi:magnify" class="text-2xl" />
          </button>
          <button class="text-black hover:text-black/70 transition-colors duration-200 p-1" aria-label="Perfil">
            <Icon icon="mdi:account-outline" class="text-2xl" />
          </button>
          <button class="text-black hover:text-black/70 transition-colors duration-200 p-1 relative" aria-label="Carrito" @click="cartStore.toggleCarrito()">
            <Icon icon="mdi:cart-outline" class="text-2xl" />
            <span v-if="cartStore.totalItems > 0" class="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-btn-primary text-btn-primary-text text-[10px] flex items-center justify-center font-bold leading-none">
              {{ cartStore.totalItems > 9 ? '9+' : cartStore.totalItems }}
            </span>
          </button>
        </nav>

      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useCartStore } from '../../stores/cart.store'

const cartStore = useCartStore()
const route = useRoute()
const esPersonalizado = computed(() => route.path.startsWith('/flor'))
</script>
