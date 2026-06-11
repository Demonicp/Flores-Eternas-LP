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
      <div class="flex items-center justify-between h-40 md:h-48">

        <nav class="flex items-center gap-6 flex-1">
          <a
            href="#"
            class="text-sm text-text-primary/80 hover:text-text-primary transition-colors duration-200 hidden sm:block font-medium"
          >
            Entrega inmediata
          </a>
          <a
            href="#"
            class="text-sm text-text-primary/80 hover:text-text-primary transition-colors duration-200 hidden sm:block font-medium"
          >
            Personalizado
          </a>
        </nav>

        <div class="flex-shrink-0">
          <img
            src="/assets/images/flplogoblack.png"
            alt="Flores Eternas LP"
            class="h-16 md:h-20 w-auto object-contain"
          />
        </div>

        <nav class="flex items-center gap-4 flex-1 justify-end">
          <a
            href="#"
            class="text-sm text-text-primary/80 hover:text-text-primary transition-colors duration-200 hidden sm:block font-medium"
          >
            Conócenos
          </a>
          <button class="text-text-primary hover:text-text-primary/70 transition-colors duration-200 p-1" aria-label="Buscar">
            <Icon icon="mdi:magnify" class="text-xl" />
          </button>
          <button class="text-text-primary hover:text-text-primary/70 transition-colors duration-200 p-1" aria-label="Perfil">
            <Icon icon="mdi:account-outline" class="text-xl" />
          </button>
          <button class="text-text-primary hover:text-text-primary/70 transition-colors duration-200 p-1 relative" aria-label="Carrito" @click="cartStore.toggleCarrito()">
            <Icon icon="mdi:cart-outline" class="text-xl" />
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
</script>
