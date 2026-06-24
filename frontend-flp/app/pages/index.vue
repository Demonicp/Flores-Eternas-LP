<template>
  <div class="min-h-screen bg-bg-page">
    <TheHeader />

    <div class="flex max-w-7xl mx-auto">
      <div class="flex-1 min-w-0">
        <CatalogoPublico />
      </div>
      <div class="hidden lg:block flex-shrink-0 sticky top-0 self-start max-h-screen overflow-y-auto">
        <CarritoLateral />
      </div>
    </div>

    <section id="conocenos" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 md:py-20">
      <div class="bg-white rounded-2xl p-8 md:p-12 shadow-sm">
        <div class="flex flex-col md:flex-row items-center gap-8 md:gap-12">
          <div class="flex-1 text-center md:text-left">
            <h2 class="font-serif text-2xl md:text-3xl text-text-primary font-medium mb-4">
              Conócenos
            </h2>
            <p class="text-text-primary/70 text-sm md:text-base leading-relaxed mb-6">
              Descubre nuestros arreglos florales eternos y déjate inspirar. Visita nuestro perfil de Instagram para ver más productos, promociones y novedades.
            </p>
            <a
              href="https://www.instagram.com/floreseternaslp_palmira/"
              target="_blank"
              rel="noopener noreferrer"
              class="inline-flex items-center gap-3 bg-gradient-to-r from-purple-500 via-pink-500 to-orange-400 text-white px-6 py-3 rounded-full text-sm font-medium hover:opacity-90 transition-opacity shadow-md"
            >
              <Icon icon="mdi:instagram" class="text-xl" />
              @floreseternaslp_palmira
            </a>
          </div>
          <div class="flex-shrink-0">
            <div class="w-40 h-40 md:w-48 md:h-48 rounded-full bg-gradient-to-br from-purple-400 via-pink-400 to-orange-300 flex items-center justify-center shadow-lg">
              <Icon icon="mdi:flower-tulip" class="text-6xl md:text-7xl text-white" />
            </div>
          </div>
        </div>
      </div>
    </section>

    <footer class="border-t border-border-soft py-8 text-center text-sm text-text-primary/50">
      <p>&copy; {{ new Date().getFullYear() }} Flores Eternas LP &mdash; Palmira, Valle del Cauca</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import TheHeader from '~/components/catalogo/TheHeader.vue'
import CatalogoPublico from '~/components/catalogo/CatalogoPublico.vue'
import CarritoLateral from '~/components/catalogo/CarritoLateral.vue'
</script>
