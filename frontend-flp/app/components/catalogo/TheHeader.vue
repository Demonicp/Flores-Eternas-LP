<template>
  <header
    class="relative bg-cover bg-center min-h-[120px] sm:min-h-[200px] w-full"
    style="background-image: url('/assets/images/FondoPruebaHeader.png'); background-position: center 35%; background-color: #5a3a2a;"
  >
    <div class="bg-black/30">
      <div class="relative z-10 w-full px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col md:grid md:grid-cols-[1fr_auto_1fr] items-center min-h-[120px] sm:min-h-[200px] gap-1 md:gap-0">

          <nav class="flex items-center justify-center md:justify-start gap-3 md:gap-6 order-2 md:order-1 mt-1 md:mt-0">
            <NuxtLink
              to="/"
              class="text-[14px] md:text-[17px] tracking-wide text-white hover:text-white/80 transition-colors duration-200 hidden sm:block"
              exact-active-class="font-bold"
              active-class=""
            >
              Entrega inmediata
            </NuxtLink>
            <NuxtLink
              to="/flor/SeleccionFlor"
              class="text-[14px] md:text-[17px] tracking-wide text-white hover:text-white/80 transition-colors duration-200 hidden sm:block"
              :class="{ 'font-bold': esPersonalizado }"
            >
              Personalizado
            </NuxtLink>
            <a
              href="#conocenos"
              @click="scrollAConocemos"
              class="text-[14px] md:text-[17px] tracking-wide text-white hover:text-white/80 transition-colors duration-200 hidden sm:block"
            >
              Conócenos
            </a>
          </nav>

          <div class="flex-shrink-0 justify-self-center order-1 md:order-2">
            <NuxtLink to="/">
              <img
                src="/assets/images/flplogowhite.png"
                alt="Flores Eternas LP"
                class="h-16 sm:h-24 md:h-36 w-auto object-contain cursor-pointer"
              />
            </NuxtLink>
          </div>

          <nav class="flex items-center justify-center md:justify-end gap-3 md:gap-6 justify-self-end order-3 md:order-3 mt-1 md:mt-0">
            <button class="text-white hover:text-white/80 transition-colors duration-200 p-1" aria-label="Buscar" @click="searchOpen = true">
              <Icon icon="mdi:magnify" class="text-xl md:text-2xl" />
            </button>
            <button class="text-white hover:text-white/80 transition-colors duration-200 p-1" aria-label="Perfil">
              <Icon icon="mdi:account-outline" class="text-xl md:text-2xl" />
            </button>
            <button class="text-white hover:text-white/80 transition-colors duration-200 p-1 relative" aria-label="Carrito" @click="cartStore.abrirOverlay()">
              <Icon icon="mdi:cart-outline" class="text-xl md:text-2xl" />
              <span v-if="cartStore.totalItems > 0" class="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-btn-primary text-btn-primary-text text-[10px] flex items-center justify-center font-bold leading-none">
                {{ cartStore.totalItems > 9 ? '9+' : cartStore.totalItems }}
              </span>
            </button>
          </nav>

        </div>
      </div>
    </div>
  </header>
  <SearchModal v-model:abierto="searchOpen" />
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useCartStore } from '../../stores/cart.store'
import SearchModal from './SearchModal.vue'

const cartStore = useCartStore()
const route = useRoute()
const esPersonalizado = computed(() => route.path.startsWith('/flor'))
const searchOpen = ref(false)

function scrollAConocemos(event: MouseEvent) {
  event.preventDefault()
  const el = document.getElementById('conocenos')
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  } else {
    window.location.hash = 'conocenos'
  }
}
</script>
