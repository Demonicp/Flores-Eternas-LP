<template>
  <Teleport to="body">
    <div class="fixed top-4 right-4 z-[9999] flex flex-col gap-2 min-w-[280px]">
      <TransitionGroup name="toast">
        <div
          v-for="t in toasts"
          :key="t.id"
          class="px-4 py-3 rounded-lg shadow-lg text-sm font-medium text-white transition-all duration-300 flex items-center gap-2"
          :class="{
            'bg-green-600': t.type === 'success',
            'bg-red-600': t.type === 'error',
            'bg-amber-500': t.type === 'warning',
          }"
        >
          <Icon v-if="t.type === 'success'" icon="mdi:check-circle-outline" class="text-lg shrink-0" />
          <Icon v-else-if="t.type === 'error'" icon="mdi:alert-circle-outline" class="text-lg shrink-0" />
          <Icon v-else icon="mdi:alert-outline" class="text-lg shrink-0" />
          {{ t.message }}
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { useToast } from '~/composables/useToast'
const { toasts } = useToast()
</script>

<style scoped>
.toast-enter-active { animation: toastIn 0.3s ease-out; }
.toast-leave-active { animation: toastOut 0.25s ease-in; }
@keyframes toastIn {
  from { opacity: 0; transform: translateX(100%); }
  to { opacity: 1; transform: translateX(0); }
}
@keyframes toastOut {
  from { opacity: 1; transform: translateX(0); }
  to { opacity: 0; transform: translateX(100%); }
}
</style>
