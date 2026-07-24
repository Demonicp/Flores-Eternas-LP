export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  components: {
    dirs: [
      { path: '~/components', pathPrefix: false },
    ],
  },

  modules: ['@pinia/nuxt'],

  css: ['assets/css/main.css'],

  postcss: {
    plugins: {
      '@tailwindcss/postcss': {},
    },
  },

  runtimeConfig: {
    public: {
      apiBase: 'http://localhost:8080',
    },
  },
})
