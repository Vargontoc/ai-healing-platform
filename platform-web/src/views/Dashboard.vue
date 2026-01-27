<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useIncidentStats, useServiceStats } from '@/composables/useStats'
import { Server, AlertCircle, CheckCircle2, Clock, Activity, RefreshCw } from 'lucide-vue-next'
import { computed } from 'vue'
import StatCardSkeleton from '@/components/skeletons/StatCardSkeleton.vue'

const { t } = useI18n()

const { data: incidentStats, isLoading: loadingIncidents, isError: errorIncidents, refetch: refetchIncidents } = useIncidentStats()
const { data: serviceStats, isLoading: loadingServices, isError: errorServices, refetch: refetchServices } = useServiceStats()

const incidentStatusData = computed(() => {
  if (!incidentStats.value) return []
  return Object.entries(incidentStats.value.byStatus).map(([status, count]) => ({
    status,
    count
  }))
})

const topServicesWithIncidents = computed(() => {
  if (!incidentStats.value) return []
  return Object.entries(incidentStats.value.byService)
    .map(([service, count]) => ({ service, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 5)
})

const refreshAll = () => {
  refetchIncidents()
  refetchServices()
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-foreground" role="heading" aria-level="1">
        {{ t('dashboard.title') }}
      </h1>
      <button
        @click="refreshAll"
        class="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2"
        :aria-label="t('common.refresh')"
      >
        <RefreshCw :size="16" />
        {{ t('common.refresh') }}
      </button>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Services Widget -->
      <div class="bg-card border border-border rounded-lg p-6" role="region" :aria-label="t('dashboard.services.title')">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-semibold text-foreground flex items-center gap-2">
            <Server :size="24" aria-hidden="true" />
            {{ t('dashboard.services.title') }}
          </h2>
          <Activity :size="20" class="text-muted-foreground" aria-hidden="true" />
        </div>

        <StatCardSkeleton v-if="loadingServices" />

        <div v-else-if="errorServices" class="text-center py-8" role="alert" aria-live="polite">
          <AlertCircle :size="32" class="mx-auto mb-2 text-destructive" aria-hidden="true" />
          <p class="text-sm text-muted-foreground">{{ t('common.error') }}</p>
        </div>

        <div v-else-if="serviceStats">
          <div class="grid grid-cols-2 gap-4 mb-4">
            <div class="bg-muted/50 rounded-lg p-4">
              <div class="text-2xl font-bold text-foreground">{{ serviceStats.totalServices }}</div>
              <div class="text-sm text-muted-foreground">{{ t('dashboard.services.total') }}</div>
            </div>
            <div class="bg-muted/50 rounded-lg p-4">
              <div class="text-2xl font-bold text-foreground">{{ serviceStats.totalInstances }}</div>
              <div class="text-sm text-muted-foreground">{{ t('dashboard.services.instances') }}</div>
            </div>
          </div>

          <div class="space-y-3">
            <div class="flex items-center justify-between p-3 bg-green-50 dark:bg-green-900/20 rounded-lg">
              <div class="flex items-center gap-2">
                <CheckCircle2 :size="20" class="text-green-600 dark:text-green-400" />
                <span class="font-medium text-green-900 dark:text-green-100">{{ t('dashboard.services.up') }}</span>
              </div>
              <span class="text-2xl font-bold text-green-600 dark:text-green-400">{{ serviceStats.upServices }}</span>
            </div>

            <div class="flex items-center justify-between p-3 bg-red-50 dark:bg-red-900/20 rounded-lg">
              <div class="flex items-center gap-2">
                <AlertCircle :size="20" class="text-red-600 dark:text-red-400" />
                <span class="font-medium text-red-900 dark:text-red-100">{{ t('dashboard.services.down') }}</span>
              </div>
              <span class="text-2xl font-bold text-red-600 dark:text-red-400">{{ serviceStats.downServices }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Incidents Widget -->
      <div class="bg-card border border-border rounded-lg p-6" role="region" :aria-label="t('dashboard.incidents.title')">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-semibold text-foreground flex items-center gap-2">
            <AlertCircle :size="24" aria-hidden="true" />
            {{ t('dashboard.incidents.title') }}
          </h2>
        </div>

        <StatCardSkeleton v-if="loadingIncidents" />

        <div v-else-if="errorIncidents" class="text-center py-8" role="alert" aria-live="polite">
          <AlertCircle :size="32" class="mx-auto mb-2 text-destructive" aria-hidden="true" />
          <p class="text-sm text-muted-foreground">{{ t('common.error') }}</p>
        </div>

        <div v-else-if="incidentStats">
          <div class="bg-muted/50 rounded-lg p-4 mb-4">
            <div class="text-3xl font-bold text-foreground">{{ incidentStats.totalIncidents }}</div>
            <div class="text-sm text-muted-foreground">{{ t('dashboard.incidents.total') }}</div>
          </div>

          <div class="space-y-3 mb-4">
            <div
              v-for="item in incidentStatusData"
              :key="item.status"
              class="flex items-center justify-between p-3 rounded-lg"
              :class="{
                'bg-red-50 dark:bg-red-900/20': item.status === 'OPEN',
                'bg-gray-50 dark:bg-gray-800/50': item.status === 'CLOSED',
                'bg-green-50 dark:bg-green-900/20': item.status === 'RESOLVED'
              }"
            >
              <div class="flex items-center gap-2">
                <AlertCircle v-if="item.status === 'OPEN'" :size="18" class="text-red-600 dark:text-red-400" />
                <Clock v-else-if="item.status === 'CLOSED'" :size="18" class="text-gray-600 dark:text-gray-400" />
                <CheckCircle2 v-else :size="18" class="text-green-600 dark:text-green-400" />
                <span class="font-medium capitalize" :class="{
                  'text-red-900 dark:text-red-100': item.status === 'OPEN',
                  'text-gray-900 dark:text-gray-100': item.status === 'CLOSED',
                  'text-green-900 dark:text-green-100': item.status === 'RESOLVED'
                }">
                  {{ t(`incidents.status.${item.status.toLowerCase()}`) }}
                </span>
              </div>
              <span class="text-xl font-bold" :class="{
                'text-red-600 dark:text-red-400': item.status === 'OPEN',
                'text-gray-600 dark:text-gray-400': item.status === 'CLOSED',
                'text-green-600 dark:text-green-400': item.status === 'RESOLVED'
              }">
                {{ item.count }}
              </span>
            </div>
          </div>

          <div v-if="topServicesWithIncidents.length > 0" class="border-t border-border pt-4">
            <h3 class="text-sm font-medium text-muted-foreground mb-3">{{ t('dashboard.incidents.topServices') }}</h3>
            <div class="space-y-2">
              <div
                v-for="service in topServicesWithIncidents"
                :key="service.service"
                class="flex items-center justify-between text-sm"
              >
                <span class="text-foreground truncate">{{ service.service }}</span>
                <span class="font-medium text-muted-foreground ml-2">{{ service.count }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Auto-refresh indicator -->
    <div class="mt-4 text-center text-sm text-muted-foreground">
      {{ t('dashboard.autoRefresh') }}
    </div>
  </div>
</template>
