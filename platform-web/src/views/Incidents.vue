<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useIncidents } from '@/composables/useIncidents'
import type { IncidentFilters } from '@/types/incidents'
import { AlertCircle, CheckCircle2, Clock, RefreshCw } from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()

const filters = ref<IncidentFilters>({
  page: 0,
  size: 10
})

const { data, isLoading, isError, error, refetch } = useIncidents(filters)

const statusFilter = ref<'all' | 'OPEN' | 'CLOSED' | 'RESOLVED'>('all')

const filteredData = computed(() => {
  if (!data.value) return null

  if (statusFilter.value === 'all') {
    return data.value
  }

  return {
    ...data.value,
    content: data.value.content.filter(incident => incident.status === statusFilter.value),
    numberOfElements: data.value.content.filter(incident => incident.status === statusFilter.value).length
  }
})

const statusCounts = computed(() => {
  if (!data.value) return { all: 0, OPEN: 0, CLOSED: 0, RESOLVED: 0 }

  const counts = {
    all: data.value.totalElements,
    OPEN: 0,
    CLOSED: 0,
    RESOLVED: 0
  }

  data.value.content.forEach(incident => {
    counts[incident.status]++
  })

  return counts
})

const getStatusBadgeClass = (status: string) => {
  const baseClasses = 'inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium'

  switch (status) {
    case 'OPEN':
      return `${baseClasses} bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300`
    case 'CLOSED':
      return `${baseClasses} bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-300`
    case 'RESOLVED':
      return `${baseClasses} bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300`
    default:
      return `${baseClasses} bg-gray-100 text-gray-800`
  }
}

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'OPEN':
      return AlertCircle
    case 'CLOSED':
      return Clock
    case 'RESOLVED':
      return CheckCircle2
    default:
      return AlertCircle
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat(t('common.locale'), {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

const nextPage = () => {
  if (data.value && !data.value.last) {
    filters.value.page = (filters.value.page ?? 0) + 1
  }
}

const previousPage = () => {
  if (data.value && !data.value.first) {
    filters.value.page = Math.max(0, (filters.value.page ?? 0) - 1)
  }
}

const setPageSize = (size: number) => {
  filters.value.size = size
  filters.value.page = 0
}

const navigateToDetail = (incidentId: number) => {
  router.push({ name: 'incident-detail', params: { id: incidentId } })
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-foreground">
        {{ t('incidents.title') }}
      </h1>
      <button
        @click="() => refetch()"
        class="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors"
      >
        <RefreshCw :size="16" />
        {{ t('common.refresh') }}
      </button>
    </div>

    <!-- Status Filters -->
    <div class="flex gap-2 mb-6">
      <button
        v-for="status in ['all', 'OPEN', 'CLOSED', 'RESOLVED']"
        :key="status"
        @click="statusFilter = status as typeof statusFilter"
        :class="[
          'px-4 py-2 rounded-md text-sm font-medium transition-colors',
          statusFilter === status
            ? 'bg-primary text-primary-foreground'
            : 'bg-card text-foreground hover:bg-accent'
        ]"
      >
        {{ status === 'all' ? t('common.all') : t(`incidents.status.${status.toLowerCase()}`) }}
        <span class="ml-1 text-xs opacity-75">
          ({{ statusCounts[status as keyof typeof statusCounts] }})
        </span>
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="bg-card border border-border rounded-lg p-8 text-center">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
      <p class="text-muted-foreground">{{ t('common.loading') }}</p>
    </div>

    <!-- Error State -->
    <div v-else-if="isError" class="bg-card border border-border rounded-lg p-8 text-center">
      <AlertCircle :size="48" class="mx-auto mb-4 text-destructive" />
      <p class="text-destructive font-medium mb-2">{{ t('common.error') }}</p>
      <p class="text-muted-foreground text-sm">{{ error?.message }}</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="!filteredData || filteredData.content.length === 0" class="bg-card border border-border rounded-lg p-8 text-center">
      <AlertCircle :size="48" class="mx-auto mb-4 text-muted-foreground" />
      <p class="text-muted-foreground">{{ t('incidents.noIncidents') }}</p>
    </div>

    <!-- Incidents Table -->
    <div v-else class="bg-card border border-border rounded-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-muted border-b border-border">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {{ t('incidents.table.id') }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {{ t('incidents.table.service') }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {{ t('incidents.table.errorType') }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {{ t('incidents.table.description') }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {{ t('incidents.table.status') }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {{ t('incidents.table.detected') }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {{ t('incidents.table.occurrences') }}
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-border">
            <tr
              v-for="incident in filteredData.content"
              :key="incident.id"
              class="hover:bg-accent/50 transition-colors cursor-pointer"
              @click="navigateToDetail(incident.id)"
              role="button"
              tabindex="0"
              @keydown.enter="navigateToDetail(incident.id)"
              :aria-label="`View details for incident ${incident.id}`"
            >
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-foreground">
                #{{ incident.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-foreground">
                {{ incident.serviceName }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-foreground">
                <code class="px-2 py-1 bg-muted rounded text-xs">{{ incident.errorType }}</code>
              </td>
              <td class="px-6 py-4 text-sm text-muted-foreground max-w-md truncate">
                {{ incident.description }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusBadgeClass(incident.status)">
                  <component :is="getStatusIcon(incident.status)" :size="14" />
                  {{ t(`incidents.status.${incident.status.toLowerCase()}`) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                {{ formatDate(incident.detectedAt) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-center">
                <span class="inline-flex items-center justify-center w-8 h-8 rounded-full bg-primary/10 text-primary text-sm font-medium">
                  {{ incident.occurrences }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="px-6 py-4 border-t border-border flex items-center justify-between">
        <div class="flex items-center gap-2 text-sm text-muted-foreground">
          <span>{{ t('incidents.pagination.rowsPerPage') }}:</span>
          <select
            :value="filters.size"
            @change="(e) => setPageSize(Number((e.target as HTMLSelectElement).value))"
            class="px-2 py-1 bg-background border border-border rounded-md text-foreground"
          >
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-sm text-muted-foreground">
            {{ t('incidents.pagination.page') }} {{ (filters.page ?? 0) + 1 }} {{ t('incidents.pagination.of') }} {{ filteredData.totalPages }}
          </span>
          <div class="flex gap-1">
            <button
              @click="previousPage"
              :disabled="filteredData.first"
              :class="[
                'px-3 py-1 rounded-md text-sm font-medium transition-colors',
                filteredData.first
                  ? 'bg-muted text-muted-foreground cursor-not-allowed'
                  : 'bg-primary text-primary-foreground hover:bg-primary/90'
              ]"
            >
              {{ t('incidents.pagination.previous') }}
            </button>
            <button
              @click="nextPage"
              :disabled="filteredData.last"
              :class="[
                'px-3 py-1 rounded-md text-sm font-medium transition-colors',
                filteredData.last
                  ? 'bg-muted text-muted-foreground cursor-not-allowed'
                  : 'bg-primary text-primary-foreground hover:bg-primary/90'
              ]"
            >
              {{ t('incidents.pagination.next') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
