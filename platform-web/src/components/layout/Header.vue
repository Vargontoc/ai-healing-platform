<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { useTheme } from '@/composables/useTheme'
import { Menu, LogOut, Globe, Sun, Moon } from 'lucide-vue-next'
import { ref } from 'vue'

const emit = defineEmits<{
  toggleSidebar: []
}>()

const { t, locale } = useI18n()
const authStore = useAuthStore()
const router = useRouter()
const { theme, toggleTheme } = useTheme()

const showLanguageMenu = ref(false)

const languages = [
  { code: 'en', name: 'English' },
  { code: 'es', name: 'Español' },
  { code: 'fr', name: 'Français' },
  { code: 'pt', name: 'Português' }
]

function changeLanguage(lang: string) {
  locale.value = lang
  localStorage.setItem('locale', lang)
  showLanguageMenu.value = false
}

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<template>
  <header class="h-16 border-b border-border bg-card flex items-center justify-between px-6">
    <button
      @click="emit('toggleSidebar')"
      class="p-2 rounded-lg hover:bg-accent transition-colors md:hidden"
    >
      <Menu :size="20" />
    </button>

    <div class="flex-1"></div>

    <div class="flex items-center gap-4">
      <div class="relative">
        <button
          @click="showLanguageMenu = !showLanguageMenu"
          class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-accent transition-colors"
        >
          <Globe :size="18" />
          <span class="text-sm font-medium uppercase">{{ locale }}</span>
        </button>

        <div
          v-if="showLanguageMenu"
          class="absolute right-0 mt-2 w-40 bg-popover border border-border rounded-lg shadow-lg py-1 z-50"
        >
          <button
            v-for="lang in languages"
            :key="lang.code"
            @click="changeLanguage(lang.code)"
            :class="[
              'w-full text-left px-4 py-2 text-sm hover:bg-accent transition-colors',
              locale === lang.code ? 'bg-accent' : ''
            ]"
          >
            {{ lang.name }}
          </button>
        </div>
      </div>

      <button
        @click="toggleTheme"
        class="p-2 rounded-lg hover:bg-accent transition-colors"
        :title="theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'"
      >
        <Sun v-if="theme === 'dark'" :size="18" />
        <Moon v-else :size="18" />
      </button>

      <div class="flex items-center gap-2">
        <span class="text-sm text-muted-foreground">
          {{ authStore.user?.name || authStore.user?.email }}
        </span>
        <button
          @click="handleLogout"
          class="p-2 rounded-lg hover:bg-accent transition-colors"
          :title="t('nav.logout')"
        >
          <LogOut :size="18" />
        </button>
      </div>
    </div>
  </header>
</template>
