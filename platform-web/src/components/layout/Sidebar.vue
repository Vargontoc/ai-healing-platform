<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import {
  LayoutDashboard,
  Network,
  Bot,
  AlertCircle,
  BookOpen,
  ChevronLeft
} from 'lucide-vue-next'

interface Props {
  open: boolean
}

defineProps<Props>()
const emit = defineEmits<{
  toggle: []
}>()

const { t } = useI18n()
const route = useRoute()

const navItems = computed(() => [
  {
    name: 'dashboard',
    label: t('nav.dashboard'),
    icon: LayoutDashboard,
    path: '/'
  },
  {
    name: 'service-map',
    label: t('nav.serviceMap'),
    icon: Network,
    path: '/service-map'
  },
  {
    name: 'agent-console',
    label: t('nav.agentConsole'),
    icon: Bot,
    path: '/agent-console'
  },
  {
    name: 'incidents',
    label: t('nav.incidents'),
    icon: AlertCircle,
    path: '/incidents'
  },
  {
    name: 'knowledge',
    label: t('nav.knowledge'),
    icon: BookOpen,
    path: '/knowledge'
  }
])

function isActive(path: string) {
  return route.path === path
}
</script>

<template>
  <aside
    :class="[
      'bg-card border-r border-border transition-all duration-300',
      open ? 'w-64' : 'w-20'
    ]"
  >
    <div class="flex flex-col h-full">
      <div class="flex items-center justify-between h-16 px-4 border-b border-border">
        <h1 v-if="open" class="text-lg font-semibold text-foreground">
          {{ t('app.title') }}
        </h1>
        <button
          @click="emit('toggle')"
          class="p-2 rounded-lg hover:bg-accent transition-colors"
          :class="{ 'mx-auto': !open }"
        >
          <ChevronLeft
            :size="20"
            :class="['transition-transform', { 'rotate-180': !open }]"
          />
        </button>
      </div>

      <nav class="flex-1 px-2 py-4 space-y-1">
        <router-link
          v-for="item in navItems"
          :key="item.name"
          :to="item.path"
          :class="[
            'flex items-center gap-3 px-3 py-2 rounded-lg transition-colors',
            isActive(item.path)
              ? 'bg-primary text-primary-foreground'
              : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
          ]"
        >
          <component :is="item.icon" :size="20" />
          <span v-if="open" class="text-sm font-medium">
            {{ item.label }}
          </span>
        </router-link>
      </nav>
    </div>
  </aside>
</template>
