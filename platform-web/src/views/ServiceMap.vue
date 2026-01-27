<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useServices } from '@/composables/useServices'
import { ServiceInfo } from '@/types/services'
import { CheckCircle2, XCircle, RefreshCw, Filter } from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()
const { data, isLoading, isError, error, refetch } = useServices()

const statusFilter = ref<'all' | 'UP' | 'DOWN'>('all')

const filteredServices = computed<ServiceInfo[]>(() => {
  if (!data.value?.services) return []

  if (statusFilter.value === 'all') {
    return data.value.services
  }

  return data.value.services.filter(service => service.status === statusFilter.value)
})

const statusCounts = computed(() => {
  if (!data.value?.services) return { all: 0, up: 0, down: 0 }

  return {
    all: data.value.services.length,
    up: data.value.services.filter(s => s.status === 'UP').length,
    down: data.value.services.filter(s => s.status === 'DOWN').length
  }
})

function getStatusColor(status: string): string {
  return status === 'UP' ? 'text-green-600' : 'text-red-600'
}

function getStatusBgColor(status: string): string {
  return status === 'UP' ? 'bg-green-100' : 'bg-red-100'
}

function navigateToDetail(serviceName: string) {
  router.push({ name: 'service-detail', params: { name: serviceName } })
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-foreground">
        {{ t('serviceMap.title') }}
      </h1>
      <button
        @click="refetch()"
        class="flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
      >
        <RefreshCw :size="18" />
        <span>{{ t('common.refresh') }}</span>
      </button>
    </div>

    <!-- Filter Section -->
    <div class="bg-card border border-border rounded-lg p-4 mb-6">
      <div class="flex items-center gap-2 mb-3">
        <Filter :size="18" class="text-muted-foreground" />
        <span class="text-sm font-medium text-foreground">{{ t('common.filter') }}</span>
      </div>
      <div class="flex gap-2">
        <button
          @click="statusFilter = 'all'"
          :class="[
            'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
            statusFilter === 'all'
              ? 'bg-primary text-primary-foreground'
              : 'bg-muted text-muted-foreground hover:bg-muted/80'
          ]"
        >
          {{ t('common.all') }} ({{ statusCounts.all }})
        </button>
        <button
          @click="statusFilter = 'UP'"
          :class="[
            'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
            statusFilter === 'UP'
              ? 'bg-green-600 text-white'
              : 'bg-muted text-muted-foreground hover:bg-muted/80'
          ]"
        >
          {{ t('serviceMap.healthy') }} ({{ statusCounts.up }})
        </button>
        <button
          @click="statusFilter = 'DOWN'"
          :class="[
            'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
            statusFilter === 'DOWN'
              ? 'bg-red-600 text-white'
              : 'bg-muted text-muted-foreground hover:bg-muted/80'
          ]"
        >
          {{ t('serviceMap.down') }} ({{ statusCounts.down }})
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="bg-card border border-border rounded-lg p-12 text-center">
      <RefreshCw :size="48" class="mx-auto mb-4 text-muted-foreground animate-spin" />
      <p class="text-muted-foreground">{{ t('common.loading') }}</p>
    </div>

    <!-- Error State -->
    <div v-else-if="isError" class="bg-card border border-border rounded-lg p-12 text-center">
      <XCircle :size="48" class="mx-auto mb-4 text-destructive" />
      <p class="text-destructive font-medium mb-2">{{ t('common.error') }}</p>
      <p class="text-sm text-muted-foreground">{{ error?.message }}</p>
    </div>

    <!-- Services Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="service in filteredServices"
        :key="service.name"
        class="bg-card border border-border rounded-lg p-6 hover:shadow-lg transition-shadow cursor-pointer"
        @click="navigateToDetail(service.name)"
        role="button"
        tabindex="0"
        @keydown.enter="navigateToDetail(service.name)"
        :aria-label="`View details for ${service.name}`"
      >
        <!-- Service Header -->
        <div class="flex items-start justify-between mb-4">
          <div class="flex-1">
            <h3 class="text-lg font-semibold text-foreground mb-1">
              {{ service.name }}
            </h3>
            <div class="flex items-center gap-2">
              <component
                :is="service.status === 'UP' ? CheckCircle2 : XCircle"
                :size="16"
                :class="getStatusColor(service.status)"
              />
              <span
                :class="[
                  'text-sm font-medium px-2 py-1 rounded',
                  getStatusBgColor(service.status),
                  getStatusColor(service.status)
                ]"
              >
                {{ service.status }}
              </span>
            </div>
          </div>
        </div>

        <!-- Service Details -->
        <div class="space-y-2">
          <div class="flex items-center justify-between text-sm">
            <span class="text-muted-foreground">{{ t('serviceMap.instances') }}:</span>
            <span class="font-medium text-foreground">{{ service.instances }}</span>
          </div>

          <div v-if="service.urls.length > 0" class="pt-2 border-t border-border">
            <p class="text-sm text-muted-foreground mb-2">URLs:</p>
            <div class="space-y-1">
              <a
                v-for="(url, index) in service.urls"
                :key="index"
                :href="url"
                target="_blank"
                class="block text-xs text-primary hover:underline truncate"
                @click.stop
              >
                {{ url }}
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-if="filteredServices.length === 0"
        class="col-span-full bg-card border border-border rounded-lg p-12 text-center"
      >
        <p class="text-muted-foreground">
          {{ t('serviceMap.noServices') }}
        </p>
      </div>
    </div>
  </div>
</template>
