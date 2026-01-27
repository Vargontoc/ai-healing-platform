<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { useIncident } from '@/composables/useIncidents'
import { incidentsApi } from '@/services/incidentsApi'
import {
  AlertCircle,
  Calendar,
  Clock,
  Activity,
  ChevronRight,
  Home,
  ArrowLeft,
  UserCircle,
  Settings,
  X
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const queryClient = useQueryClient()

const incidentId = computed(() => Number(route.params.id))

const { data: incident, isLoading, isError } = useIncident(incidentId)

// Modal states
const showStatusModal = ref(false)
const showAssignModal = ref(false)

// Form states
const selectedStatus = ref<'OPEN' | 'CLOSED' | 'RESOLVED' | 'REOPENED'>('OPEN')
const statusComment = ref('')
const assigneeName = ref('')

// Mutations
const updateStatusMutation = useMutation({
  mutationFn: (data: { id: number; status: string; comment: string }) =>
    incidentsApi.updateStatus(data.id, { status: data.status as any, comment: data.comment }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['incident', incidentId.value] })
    queryClient.invalidateQueries({ queryKey: ['incidents'] })
    showStatusModal.value = false
    statusComment.value = ''
  }
})

const assignIncidentMutation = useMutation({
  mutationFn: (data: { id: number; assignedTo: string }) =>
    incidentsApi.assignIncident(data.id, { assignedTo: data.assignedTo }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['incident', incidentId.value] })
    queryClient.invalidateQueries({ queryKey: ['incidents'] })
    showAssignModal.value = false
    assigneeName.value = ''
  }
})

const openStatusModal = () => {
  if (incident.value) {
    selectedStatus.value = incident.value.status
    statusComment.value = ''
    showStatusModal.value = true
  }
}

const openAssignModal = () => {
  if (incident.value?.assignedTo) {
    assigneeName.value = incident.value.assignedTo
  } else {
    assigneeName.value = ''
  }
  showAssignModal.value = true
}

const submitStatusUpdate = () => {
  if (!incident.value) return
  updateStatusMutation.mutate({
    id: incident.value.id,
    status: selectedStatus.value,
    comment: statusComment.value
  })
}

const submitAssignment = () => {
  if (!incident.value || !assigneeName.value.trim()) return
  assignIncidentMutation.mutate({
    id: incident.value.id,
    assignedTo: assigneeName.value.trim()
  })
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat(t('common.locale'), {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(date)
}

const formatRelativeTime = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMinutes = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMinutes < 1) return t('incidentDetail.justNow')
  if (diffMinutes < 60) return t('incidentDetail.minutesAgo', { minutes: diffMinutes })
  if (diffHours < 24) return t('incidentDetail.hoursAgo', { hours: diffHours })
  return t('incidentDetail.daysAgo', { days: diffDays })
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'OPEN':
      return 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
    case 'REOPENED':
      return 'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400'
    case 'RESOLVED':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    case 'CLOSED':
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}

const getAvailableStatuses = () => {
  if (!incident.value) return []
  const current = incident.value.status

  switch (current) {
    case 'OPEN':
    case 'REOPENED':
      return ['CLOSED', 'RESOLVED']
    case 'CLOSED':
    case 'RESOLVED':
      return ['REOPENED']
    default:
      return []
  }
}

const goBack = () => {
  router.push({ name: 'incidents' })
}
</script>

<template>
  <div>
    <!-- Breadcrumb Navigation -->
    <nav class="flex items-center gap-2 text-sm text-muted-foreground mb-6">
      <router-link
        to="/"
        class="inline-flex items-center gap-1 hover:text-foreground transition-colors"
      >
        <Home :size="14" aria-hidden="true" />
        {{ t('nav.dashboard') }}
      </router-link>
      <ChevronRight :size="14" aria-hidden="true" />
      <router-link
        to="/incidents"
        class="hover:text-foreground transition-colors"
      >
        {{ t('nav.incidents') }}
      </router-link>
      <ChevronRight :size="14" aria-hidden="true" />
      <span class="text-foreground" aria-current="page">
        {{ t('incidentDetail.incident') }} #{{ incidentId }}
      </span>
    </nav>

    <!-- Back Button -->
    <button
      @click="goBack"
      class="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors mb-6"
      :aria-label="t('incidentDetail.backToIncidents')"
    >
      <ArrowLeft :size="16" aria-hidden="true" />
      {{ t('incidentDetail.backToIncidents') }}
    </button>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex items-center justify-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="isError" class="bg-destructive/10 border border-destructive rounded-lg p-6 flex items-center gap-3">
      <AlertCircle :size="24" class="text-destructive" aria-hidden="true" />
      <div>
        <h2 class="text-lg font-semibold text-destructive">{{ t('incidentDetail.errorTitle') }}</h2>
        <p class="text-sm text-destructive/80">{{ t('incidentDetail.errorMessage') }}</p>
      </div>
    </div>

    <!-- Incident Details -->
    <div v-else-if="incident" class="space-y-6">
      <!-- Header -->
      <div class="bg-card border border-border rounded-lg p-6">
        <div class="flex items-start justify-between mb-4">
          <div class="flex-1">
            <h1 class="text-3xl font-bold text-foreground mb-2" role="heading" aria-level="1">
              {{ incident.errorType }}
            </h1>
            <p class="text-lg text-muted-foreground">
              {{ t('incidentDetail.affectedService') }}: <span class="font-semibold text-foreground">{{ incident.serviceName }}</span>
            </p>
          </div>
          <span
            :class="getStatusClass(incident.status)"
            class="px-3 py-1 rounded-full text-sm font-medium"
          >
            {{ t(`incidents.status.${incident.status.toLowerCase()}`) }}
          </span>
        </div>

        <!-- Metrics Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
          <div class="bg-muted/50 rounded-lg p-4">
            <div class="flex items-center gap-2 text-muted-foreground mb-1">
              <Calendar :size="16" aria-hidden="true" />
              <span class="text-sm">{{ t('incidentDetail.firstDetected') }}</span>
            </div>
            <p class="text-lg font-semibold text-foreground">{{ formatDate(incident.detectedAt) }}</p>
            <p class="text-xs text-muted-foreground mt-1">{{ formatRelativeTime(incident.detectedAt) }}</p>
          </div>

          <div class="bg-muted/50 rounded-lg p-4">
            <div class="flex items-center gap-2 text-muted-foreground mb-1">
              <Clock :size="16" aria-hidden="true" />
              <span class="text-sm">{{ t('incidentDetail.lastSeen') }}</span>
            </div>
            <p class="text-lg font-semibold text-foreground">{{ formatDate(incident.lastSeen) }}</p>
            <p class="text-xs text-muted-foreground mt-1">{{ formatRelativeTime(incident.lastSeen) }}</p>
          </div>

          <div class="bg-muted/50 rounded-lg p-4">
            <div class="flex items-center gap-2 text-muted-foreground mb-1">
              <Activity :size="16" aria-hidden="true" />
              <span class="text-sm">{{ t('incidentDetail.occurrences') }}</span>
            </div>
            <p class="text-lg font-semibold text-foreground">{{ incident.occurrences }}</p>
            <p class="text-xs text-muted-foreground mt-1">{{ t('incidentDetail.timesReported') }}</p>
          </div>
        </div>

        <!-- Assignment and Update Info -->
        <div v-if="incident.assignedTo || incident.updatedBy || incident.updatedAt" class="mt-6 pt-6 border-t border-border">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
            <div v-if="incident.assignedTo">
              <p class="text-muted-foreground">{{ t('incidentDetail.assignedTo') }}</p>
              <p class="font-medium text-foreground mt-1">{{ incident.assignedTo }}</p>
            </div>
            <div v-if="incident.updatedBy">
              <p class="text-muted-foreground">{{ t('incidentDetail.updatedBy') }}</p>
              <p class="font-medium text-foreground mt-1">{{ incident.updatedBy }}</p>
            </div>
            <div v-if="incident.updatedAt">
              <p class="text-muted-foreground">{{ t('incidentDetail.lastUpdated') }}</p>
              <p class="font-medium text-foreground mt-1">{{ formatDate(incident.updatedAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions Section -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h2 class="text-xl font-semibold text-foreground mb-4" role="heading" aria-level="2">
          {{ t('incidentDetail.actions') }}
        </h2>
        <div class="flex flex-wrap gap-3">
          <button
            @click="openStatusModal"
            class="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
          >
            <Settings :size="18" aria-hidden="true" />
            {{ t('incidentDetail.updateStatus') }}
          </button>
          <button
            @click="openAssignModal"
            class="inline-flex items-center gap-2 px-4 py-2 bg-secondary text-secondary-foreground rounded-lg hover:bg-secondary/90 transition-colors"
          >
            <UserCircle :size="18" aria-hidden="true" />
            {{ t('incidentDetail.assignIncident') }}
          </button>
        </div>
      </div>

      <!-- Description Section -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h2 class="text-xl font-semibold text-foreground mb-4" role="heading" aria-level="2">
          {{ t('incidentDetail.errorDetails') }}
        </h2>
        <div class="bg-muted/50 rounded-lg p-4">
          <pre class="text-sm text-foreground whitespace-pre-wrap break-words font-mono">{{ incident.description }}</pre>
        </div>
      </div>

      <!-- Timeline Section (Placeholder for future implementation) -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h2 class="text-xl font-semibold text-foreground mb-4" role="heading" aria-level="2">
          {{ t('incidentDetail.timeline') }}
        </h2>
        <div class="space-y-3">
          <!-- First Occurrence -->
          <div class="flex gap-3">
            <div class="flex flex-col items-center">
              <div class="w-3 h-3 rounded-full bg-primary"></div>
              <div class="w-0.5 h-full bg-border mt-1"></div>
            </div>
            <div class="flex-1 pb-4">
              <p class="text-sm font-medium text-foreground">{{ t('incidentDetail.firstOccurrence') }}</p>
              <p class="text-xs text-muted-foreground">{{ formatDate(incident.detectedAt) }}</p>
              <p class="text-sm text-muted-foreground mt-1">{{ t('incidentDetail.incidentCreated') }}</p>
            </div>
          </div>

          <!-- Last Occurrence -->
          <div class="flex gap-3">
            <div class="flex flex-col items-center">
              <div class="w-3 h-3 rounded-full bg-primary"></div>
            </div>
            <div class="flex-1">
              <p class="text-sm font-medium text-foreground">{{ t('incidentDetail.lastOccurrence') }}</p>
              <p class="text-xs text-muted-foreground">{{ formatDate(incident.lastSeen) }}</p>
              <p class="text-sm text-muted-foreground mt-1">
                {{ t('incidentDetail.totalOccurrences', { count: incident.occurrences }) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Update Status Modal -->
    <div
      v-if="showStatusModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showStatusModal = false"
    >
      <div class="bg-card border border-border rounded-lg p-6 max-w-md w-full">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-xl font-semibold text-foreground">{{ t('incidentDetail.updateStatus') }}</h3>
          <button
            @click="showStatusModal = false"
            class="text-muted-foreground hover:text-foreground transition-colors"
          >
            <X :size="20" aria-hidden="true" />
          </button>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-foreground mb-2">
              {{ t('incidentDetail.currentStatus') }}
            </label>
            <div :class="getStatusClass(incident?.status || '')" class="inline-block px-3 py-1 rounded-full text-sm font-medium">
              {{ incident ? t(`incidents.status.${incident.status.toLowerCase()}`) : '' }}
            </div>
          </div>

          <div>
            <label for="newStatus" class="block text-sm font-medium text-foreground mb-2">
              {{ t('incidentDetail.newStatus') }}
            </label>
            <select
              id="newStatus"
              v-model="selectedStatus"
              class="w-full px-3 py-2 bg-background border border-border rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option v-for="status in getAvailableStatuses()" :key="status" :value="status">
                {{ t(`incidents.status.${status.toLowerCase()}`) }}
              </option>
            </select>
          </div>

          <div>
            <label for="statusComment" class="block text-sm font-medium text-foreground mb-2">
              {{ t('incidentDetail.comment') }} ({{ t('common.optional') }})
            </label>
            <textarea
              id="statusComment"
              v-model="statusComment"
              rows="3"
              class="w-full px-3 py-2 bg-background border border-border rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
              :placeholder="t('incidentDetail.commentPlaceholder')"
            ></textarea>
          </div>

          <div class="flex gap-3 justify-end">
            <button
              @click="showStatusModal = false"
              class="px-4 py-2 bg-muted text-foreground rounded-lg hover:bg-muted/80 transition-colors"
            >
              {{ t('common.cancel') }}
            </button>
            <button
              @click="submitStatusUpdate"
              :disabled="updateStatusMutation.isPending.value"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ updateStatusMutation.isPending.value ? t('common.updating') : t('common.update') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Assign Incident Modal -->
    <div
      v-if="showAssignModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      @click.self="showAssignModal = false"
    >
      <div class="bg-card border border-border rounded-lg p-6 max-w-md w-full">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-xl font-semibold text-foreground">{{ t('incidentDetail.assignIncident') }}</h3>
          <button
            @click="showAssignModal = false"
            class="text-muted-foreground hover:text-foreground transition-colors"
          >
            <X :size="20" aria-hidden="true" />
          </button>
        </div>

        <div class="space-y-4">
          <div v-if="incident?.assignedTo">
            <label class="block text-sm font-medium text-foreground mb-2">
              {{ t('incidentDetail.currentAssignee') }}
            </label>
            <p class="text-foreground bg-muted/50 px-3 py-2 rounded-lg">{{ incident.assignedTo }}</p>
          </div>

          <div>
            <label for="assigneeName" class="block text-sm font-medium text-foreground mb-2">
              {{ t('incidentDetail.assignTo') }}
            </label>
            <input
              id="assigneeName"
              v-model="assigneeName"
              type="text"
              class="w-full px-3 py-2 bg-background border border-border rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              :placeholder="t('incidentDetail.assignPlaceholder')"
            />
          </div>

          <div class="flex gap-3 justify-end">
            <button
              @click="showAssignModal = false"
              class="px-4 py-2 bg-muted text-foreground rounded-lg hover:bg-muted/80 transition-colors"
            >
              {{ t('common.cancel') }}
            </button>
            <button
              @click="submitAssignment"
              :disabled="assignIncidentMutation.isPending.value || !assigneeName.trim()"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ assignIncidentMutation.isPending.value ? t('common.assigning') : t('common.assign') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
