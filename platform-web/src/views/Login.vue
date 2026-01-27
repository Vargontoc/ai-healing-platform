<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  if (!email.value || !password.value) {
    error.value = 'Please fill in all fields'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await authStore.login(email.value, password.value)
    router.push('/')
  } catch (err) {
    error.value = 'Invalid credentials'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-background flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <div class="bg-card border border-border rounded-lg shadow-lg p-8">
        <div class="text-center mb-8">
          <h1 class="text-2xl font-bold text-foreground mb-2">
            {{ t('auth.welcome') }}
          </h1>
          <p class="text-sm text-muted-foreground">
            {{ t('auth.description') }}
          </p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-4">
          <div>
            <label for="email" class="block text-sm font-medium text-foreground mb-1">
              {{ t('auth.email') }}
            </label>
            <input
              id="email"
              v-model="email"
              type="email"
              class="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
              :placeholder="t('auth.email')"
            />
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-foreground mb-1">
              {{ t('auth.password') }}
            </label>
            <input
              id="password"
              v-model="password"
              type="password"
              class="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
              :placeholder="t('auth.password')"
            />
          </div>

          <div v-if="error" class="text-sm text-destructive">
            {{ error }}
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-primary text-primary-foreground py-2 rounded-lg font-medium hover:bg-primary/90 transition-colors disabled:opacity-50"
          >
            {{ loading ? t('auth.loggingIn') : t('auth.loginButton') }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
