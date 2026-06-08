# Nuxt.js Best Practices - MVVM Pattern

## Overview

This skill enforces a clean architecture for Nuxt 3/4 projects following the **MVVM pattern** (Model-View-ViewModel) adapted to Nuxt's conventions. Applicable to **any project type**: e-commerce, SaaS, blog, dashboard, etc.

---

## Folder Structure

```
app/
├── pages/                    # VIEW Layer - Routes
│   ├── index.vue            # Landing page (/)
│   ├── items/
│   │   ├── index.vue        # /items
│   │   └── [id].vue         # /items/:id
│   └── admin/
│       └── index.vue        # /admin (protected)
│
├── composables/             # VIEWMODEL Layer - Reactive logic
│   ├── useItem.ts           # Item/Entity business logic
│   ├── useAuth.ts           # Authentication logic
│   └── useCart.ts           # Cart/Basket state + logic
│
├── services/                # MODEL Layer - Data access
│   ├── api.ts               # Base HTTP client
│   ├── item.service.ts      # Item API calls
│   └── auth.service.ts      # Auth API calls
│
├── models/                  # MODEL Layer - TypeScript contracts
│   ├── index.ts             # All interfaces/exports
│   ├── item.model.ts        # Item, Category, etc.
│   └── user.model.ts        # User, Order, etc.
│
├── components/
│   ├── ui/                  # Generic reusable components (dumb)
│   │   ├── Button.vue       # Props: variant, size, disabled, loading
│   │   ├── Input.vue        # Props: type, placeholder, error
│   │   ├── Modal.vue        # Props: open, title
│   │   └── Card.vue         # Props: elevated, padding
│   │
│   └── features/            # Domain-specific components (smart)
│       ├── ItemCard.vue     # Knows about items, uses useCart
│       ├── HeroCarousel.vue # Knows about items
│       └── CartDrawer.vue   # Knows about cart store
│
├── stores/                  # GLOBAL STATE (Pinia)
│   ├── cart.store.ts        # Cart persistence
│   └── auth.store.ts        # User session
│
├── layouts/                 # Page templates
│   ├── default.vue          # Public layout (header, footer)
│   └── admin.vue            # Admin layout (sidebar)
│
├── plugins/                 # Library setup
│   ├── pinia.ts             # Pinia configuration
│   └── auth.ts              # Token interceptor
│
└── utils/                   # Pure helper functions
    ├── constants.ts         # APP_CONFIG, pagination limits
    ├── formatters.ts        # formatPrice, formatDate
    └── validators.ts        # validateEmail, validatePhone
```

---

## Layer Responsibilities

### 1. VIEW Layer (`pages/`)

**Responsibility:** Orchestrate UI. Knows WHAT to show, not HOW to fetch.

**Rules:**
- Use composables for all data fetching
- Pass data to feature components via props
- Emit events to parent for actions
- NEVER call `fetch()` or `api.get()` directly
- Use `definePageMeta()` for layout, middleware, SEO

```vue
<!-- GOOD -->
<script setup>
const { items, loading, fetchItems } = useItem()
onMounted(() => fetchItems())
</script>

<template>
  <ItemGrid :items="items" :loading="loading" />
</template>

<!-- BAD -->
<script setup>
const items = ref([])
onMounted(async () => {
  const res = await fetch('/api/items')  // ❌ Direct fetch
  items.value = res.data
})
</script>
```

### 2. VIEWMODEL Layer (`composables/`)

**Responsibility:** Reactive state + business logic. The "employee" that manages data.

**Rules:**
- Export a single composable per domain (e.g., `useItem`, `useAuth`)
- Use `ref`, `reactive`, `computed` for state
- Call services for data, never fetch directly
- Return `{ state, computed, methods }` object
- NO HTML, NO CSS, NO Tailwind classes

```typescript
// GOOD: useItem.ts
export function useItem() {
  const items = ref<Item[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const totalPages = computed(() => Math.ceil(items.value.length / 12))

  async function fetchItems(page = 1) {
    loading.value = true
    error.value = null
    try {
      const response = await itemService.getAll(page)
      items.value = response.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error'
    } finally {
      loading.value = false
    }
  }

  return { items, loading, error, totalPages, fetchItems }
}
```

### 3. MODEL Layer

#### Services (`services/`)

**Responsibility:** HTTP communication. Pure async functions, no state.

**Rules:**
- One service per domain
- Only `api.get()`, `api.post()`, `api.put()`, `api.delete()`
- NO `ref`, `reactive`, `computed`
- Return typed promises

```typescript
// GOOD: item.service.ts
export const itemService = {
  getAll(page: number, limit = 12) {
    return api.get<PaginatedResponse<Item>>(`/items?page=${page}&limit=${limit}`)
  },
  getById(id: number) {
    return api.get<ApiResponse<Item>>(`/items/${id}`)
  },
  create(item: CreateItemDto) {
    return api.post<ApiResponse<Item>>('/items', item)
  }
}
```

#### Models (`models/`)

**Responsibility:** TypeScript interfaces/contracts only.

```typescript
// GOOD: models/item.model.ts
export interface Item {
  id: number
  name: string
  description: string
  price: number
  imageUrl: string
  stock: number
  category: string
}

export interface CreateItemDto {
  name: string
  description: string
  price: number
  category: string
}
```

### 4. Components

#### UI Components (`components/ui/`)

**Responsibility:** Pure presentation, zero business logic.

**Rules:**
- Receive all data via props
- Emit events for actions
- NO composables, NO stores, NO `useRoute`, NO `useRouter`
- Generic naming: `Button`, `Card`, `Modal`, `Input`, `Badge`, `Spinner`

```vue
<!-- GOOD: Button.vue -->
<script setup lang="ts">
interface Props {
  variant?: 'primary' | 'secondary' | 'danger'
  disabled?: boolean
}
const props = withDefaults(defineProps<Props>(), { variant: 'primary' })
const emit = defineEmits<{ click: [event: MouseEvent] }>()
</script>

<template>
  <button :class="['btn', `btn-${props.variant}`]" :disabled="props.disabled" @click="emit('click', $event)">
    <slot />
  </button>
</template>
```

#### Feature Components (`components/features/`)

**Responsibility:** Domain-aware components. Know about your business.

**Rules:**
- CAN use composables/stores
- Receive domain data via props
- Emit business events to parent
- Named by domain: `ItemCard`, `CartDrawer`, `UserProfile`

```vue
<!-- GOOD: ItemCard.vue -->
<script setup lang="ts">
import { useCartStore } from '~/stores/cart.store'

const props = defineProps<{ item: Item }>()
const emit = defineEmits<{ 'add-to-cart': [itemId: number] }>()
const cartStore = useCartStore()

function handleAdd() {
  cartStore.addItem(props.item)
  emit('add-to-cart', props.item.id)
}
</script>
```

### 5. Global State (`stores/`)

**Responsibility:** State that persists across pages.

**When to use stores instead of composables:**
- Data needed by multiple unrelated pages
- Data must survive navigation
- Complex state with multiple mutations
- Persistence (localStorage, cookies)

```typescript
// GOOD: cart.store.ts
import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const isOpen = ref(false)

  const total = computed(() => items.value.reduce((sum, i) => sum + i.price * i.qty, 0))

  function addItem(item: Item) { ... }
  function removeItem(id: number) { ... }
  function clear() { items.value = [] }

  return { items, isOpen, total, addItem, removeItem, clear }
})
```

### 6. Utils (`utils/`)

**Responsibility:** Pure functions with no side effects.

**Rules:**
- No state, no API calls, no reactivity
- Static helpers: formatters, validators, constants

```typescript
// GOOD: utils/formatters.ts
export function formatPrice(price: number, currency = 'USD'): string {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(price)
}

export function formatDate(date: string | Date): string {
  return new Intl.DateTimeFormat('en-US', { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(date))
}

// GOOD: utils/validators.ts
export function validateEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

export function validatePhone(phone: string): boolean {
  return /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/.test(phone)
}
```

---

## Data Fetching Rules

| Scenario | Use |
|----------|-----|
| Page data (SSR/SEO) | `useAsyncData(key, () => service.get())` |
| Simple GET | `useFetch('/api/...')` |
| Imperative call (button click) | `$fetch('/api/...')` |
| Client-only data | `useFetch(..., { server: false })` |

```typescript
// SSR Page (SEO important) - in pages/
const { data, pending, error } = await useAsyncData('items', () => itemService.getAll())

// Imperative (event handler)
async function handleSubmit() {
  const result = await $fetch('/api/orders', { method: 'POST', body: formData })
}
```

---

## Naming Conventions

| Item | Convention | Example |
|------|------------|---------|
| Pages | kebab-case | `my-profile.vue`, `order-details.vue` |
| Composables | camelCase, `use` prefix | `useItem.ts`, `useAuth.ts` |
| Services | camelCase, `.service` suffix | `item.service.ts`, `order.service.ts` |
| Stores | camelCase, `.store` suffix | `cart.store.ts`, `auth.store.ts` |
| Models | PascalCase | `Item.ts`, `UserOrder.ts` |
| UI Components | PascalCase | `BaseButton.vue`, `IconBox.vue` |
| Feature Components | PascalCase, domain prefix | `ItemCard.vue`, `UserAvatar.vue` |
| Utils | camelCase | `formatters.ts`, `validators.ts` |

---

## File-Based Routing Quick Reference

| File | Route |
|------|-------|
| `pages/index.vue` | `/` |
| `pages/items/index.vue` | `/items` |
| `pages/items/[id].vue` | `/items/:id` |
| `pages/items/[category]/[slug].vue` | `/items/:category/:slug` |
| `pages/[...slug].vue` | Catch-all (`/a`, `/a/b`, `/a/b/c`) |
| `pages/admin/index.vue` | `/admin` |

---

## Middleware Usage

```typescript
// middleware/auth.global.ts - Protects all /admin/* routes
export default defineNuxtRouteMiddleware((to) => {
  const { isLoggedIn } = useAuth()
  if (!isLoggedIn.value && to.path.startsWith('/admin')) {
    return navigateTo('/login')
  }
})
```

---

## SEO Checklist

- [ ] `definePageMeta({ prerender: true })` for static pages
- [ ] `useHead()` or `head()` in `definePageMeta` for dynamic meta
- [ ] Use semantic HTML (`<main>`, `<section>`, `<article>`)
- [ ] Alt text on all images
- [ ] Structured data (Schema.org) for entities

---

## Common Pitfalls

1. **❌ Fetching in template directly**
   ```vue
   <!-- BAD -->
   <div v-for="item in $fetch('/api/items')">{{ item.name }}</div>
   ```

2. **❌ Business logic in pages**
   ```vue
   <!-- BAD: pages should only orchestrate -->
   <script setup>
   const items = ref([])
   const filtered = computed(() => items.value.filter(item => item.category === selected.value))
   // Filtering logic belongs in composable
   </script>
   ```

3. **❌ Smart components in `ui/`**
   ```vue
   <!-- BAD: ItemCard knows about business, should be in features/ -->
   <script setup>
   import { useCartStore } from '~/stores/cart' // ❌ in ui/
   </script>
   ```

4. **❌ State in services**
   ```typescript
   // BAD: Services should be stateless
   const items = ref([]) // ❌
   export const itemService = {
     async getAll() { ... }
   }
   ```

---

## Testing Strategy

| Layer | What to test | Tool |
|-------|-------------|------|
| Composables | Logic, state changes | Vitest |
| Services | HTTP responses, error handling | Mock fetch |
| Components | Render, props, events | Vitest + Vue Test Utils |
| Pages | Route params, SEO meta | Playwright (E2E) |

---

## Adapt to Your Project

| This skill uses | Replace with your domain |
|-----------------|--------------------------|
| `Item`, `ItemCard` | `Product`, `ProductCard` (e-commerce) / `Post`, `PostCard` (blog) / `Event`, `EventCard` (calendar) |
| `item.service.ts` | `product.service.ts` / `post.service.ts` / `event.service.ts` |
| `CartItem` | `LineItem` (Shopify-style) / `CartProduct` |
| `Category` | `Tag`, `Topic`, `Section` |
| `useCartStore` | `useBasketStore`, `useWishlistStore` |

---

## Resources

- [Nuxt 3 Docs](https://nuxt.com/docs)
- [Vue Composition API](https://vuejs.org/guide/extras/composition-api-faq.html)
- [Pinia Docs](https://pinia.vuejs.org/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/)