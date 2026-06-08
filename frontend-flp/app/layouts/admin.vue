<template>
  <div class="min-h-screen" style="background-color: #FFFCF6;">
    <header
      class="relative bg-cover bg-center"
      style="background-image: url('/assets/images/FondoProvicional.png'); background-position: center 35%;"
    >
      <div class="bg-black/20">
        <nav class="max-w-6xl mx-auto flex items-center justify-between px-4 py-6">
          <div class="flex items-center gap-6">
            <NuxtLink
              to="/admin/pedidos"
              class="text-white text-[17px] tracking-wide hover:underline"
              active-class="font-bold underline"
            >
              Pedidos
            </NuxtLink>
            <NuxtLink
              to="/admin/inventario"
              class="text-white text-[17px] tracking-wide hover:underline"
              active-class="font-bold underline"
            >
              Inventario
            </NuxtLink>
            <NuxtLink
              to="/admin/negocio"
              class="text-white text-[17px] tracking-wide hover:underline"
              active-class="font-bold underline"
            >
              Administrar Negocio
            </NuxtLink>
          </div>

          <span class="text-white font-serif text-[29px] tracking-[0.2em] cursor-default">
            FLORES ETERNAS
          </span>

          <div class="flex items-center gap-6">
            <span class="text-white text-[17px] tracking-wide">Empleados</span>
            <span class="text-white text-[17px] tracking-wide">Clientes</span>
            <span class="text-white text-[17px] tracking-wide">Cierre de caja</span>
            <Icon icon="mdi:account-circle-outline" class="text-white text-2xl cursor-default" />
          </div>
        </nav>

        <div class="text-center pb-32 pt-14">
          <h1
            class="text-white font-serif text-4xl md:text-[43px] font-light"
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
</template>
