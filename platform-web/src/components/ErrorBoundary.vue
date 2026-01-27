<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'
import { useI18n } from 'vue-i18n'
import { AlertTriangle, RefreshCw } from 'lucide-vue-next'

const { t } = useI18n()

const error = ref<Error | null>(null)
const errorInfo = ref<string>('')

onErrorCaptured((err, _instance, info) => {
  error.value = err
  errorInfo.value = info
  console.error('Error captured by ErrorBoundary:', err, info)
  return false
})

const resetError = () => {
  error.value = null
  errorInfo.value = ''
  window.location.reload()
}
</script>

<template>
  <div v-if="error" class="min-h-screen flex items-center justify-center bg-background p-4">
    <div class="max-w-md w-full bg-card border border-border rounded-lg p-8 text-center">
      <AlertTriangle :size="64" class="mx-auto mb-4 text-destructive" />

      <h1 class="text-2xl font-bold text-foreground mb-2">
        {{ t('common.error') }}
      </h1>

      <p class="text-muted-foreground mb-4">
        Something went wrong. Please try refreshing the page.
      </p>

      <div v-if="error" class="mb-6 p-4 bg-destructive/10 rounded-md text-left">
        <p class="text-sm font-mono text-destructive break-words">
          {{ error.message }}
        </p>
        <p v-if="errorInfo" class="text-xs font-mono text-muted-foreground mt-2">
          {{ errorInfo }}
        </p>
      </div>

      <button
        @click="resetError"
        class="inline-flex items-center gap-2 px-6 py-3 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors"
      >
        <RefreshCw :size="18" />
        Reload Page
      </button>
    </div>
  </div>

  <slot v-else />
</template>
