<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useService } from '@/composables/useServices'
import { useIncidents } from '@/composables/useIncidents'
import { useDocuments } from '@/composables/useDocuments'
import {
  AlertCircle,
  CheckCircle2,
  Server,
  ChevronRight,
  Home,
  ArrowLeft,
  FileText,
  Activity,
  Globe
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const serviceName = computed(() => String(route.params.name))

const { data: service, isLoading: serviceLoading, isError: serviceError } = useService(serviceName)

const { data: incidentsData, isLoading: incidentsLoading } = useIncidents(
  computed(() => ({ serviceName: serviceName.value, page: 0, size: 10 }))
)

const { data: documentsData, isLoading: documentsLoading } = useDocuments(
  computed(() => ({ serviceId: serviceName.value, page: 0, size: 10 }))
)

const getStatusClass = (status: string) => {
  switch (status) {
    case 'UP':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    case 'DOWN':
      return 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}

const getStatusIcon = (status: string) => {
  return status === 'UP' ? CheckCircle2 : AlertCircle
}

const getIncidentStatusClass = (status: string) => {
  switch (status) {
    case 'OPEN':
      return 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
    case 'RESOLVED':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    case 'CLOSED':
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
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

const formatBytes = (bytes: number) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

const goBack = () => {
  router.push({ name: 'service-map' })
}

const navigateToIncident = (incidentId: number) => {
  router.push({ name: 'incident-detail', params: { id: incidentId } })
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
        to="/service-map"
        class="hover:text-foreground transition-colors"
      >
        {{ t('nav.serviceMap') }}
      </router-link>
      <ChevronRight :size="14" aria-hidden="true" />
      <span class="text-foreground" aria-current="page">
        {{ serviceName }}
      </span>
    </nav>

    <!-- Back Button -->
    <button
      @click="goBack"
      class="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors mb-6"
      :aria-label="t('serviceDetail.backToServices')"
    >
      <ArrowLeft :size="16" aria-hidden="true" />
      {{ t('serviceDetail.backToServices') }}
    </button>

    <!-- Loading State -->
    <div v-if="serviceLoading" class="flex items-center justify-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="serviceError" class="bg-destructive/10 border border-destructive rounded-lg p-6 flex items-center gap-3">
      <AlertCircle :size="24" class="text-destructive" aria-hidden="true" />
      <div>
        <h2 class="text-lg font-semibold text-destructive">{{ t('serviceDetail.errorTitle') }}</h2>
        <p class="text-sm text-destructive/80">{{ t('serviceDetail.errorMessage') }}</p>
      </div>
    </div>

    <!-- Service Details -->
    <div v-else-if="service" class="space-y-6">
      <!-- Header -->
      <div class="bg-card border border-border rounded-lg p-6">
        <div class="flex items-start justify-between mb-4">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
              <Server :size="24" class="text-primary" aria-hidden="true" />
            </div>
            <div>
              <h1 class="text-3xl font-bold text-foreground" role="heading" aria-level="1">
                {{ service.name }}
              </h1>
              <p class="text-sm text-muted-foreground">
                {{ t('serviceDetail.microservice') }}
              </p>
            </div>
          </div>
          <span
            :class="getStatusClass(service.status)"
            class="inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium"
          >
            <component :is="getStatusIcon(service.status)" :size="14" aria-hidden="true" />
            {{ service.status }}
          </span>
        </div>

        <!-- Metrics -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6">
          <div class="bg-muted/50 rounded-lg p-4">
            <div class="flex items-center gap-2 text-muted-foreground mb-1">
              <Activity :size="16" aria-hidden="true" />
              <span class="text-sm">{{ t('serviceDetail.activeInstances') }}</span>
            </div>
            <p class="text-2xl font-semibold text-foreground">{{ service.instances }}</p>
          </div>

          <div class="bg-muted/50 rounded-lg p-4">
            <div class="flex items-center gap-2 text-muted-foreground mb-1">
              <AlertCircle :size="16" aria-hidden="true" />
              <span class="text-sm">{{ t('serviceDetail.openIncidents') }}</span>
            </div>
            <p class="text-2xl font-semibold text-foreground">
              {{ incidentsData?.content.filter(i => i.status === 'OPEN').length || 0 }}
            </p>
          </div>
        </div>
      </div>

      <!-- Instances List -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h2 class="text-xl font-semibold text-foreground mb-4 flex items-center gap-2" role="heading" aria-level="2">
          <Globe :size="20" aria-hidden="true" />
          {{ t('serviceDetail.instances') }}
        </h2>
        <div v-if="service.urls.length > 0" class="space-y-2">
          <div
            v-for="(url, index) in service.urls"
            :key="index"
            class="bg-muted/50 rounded-md p-3 font-mono text-sm flex items-center gap-2"
          >
            <div class="w-2 h-2 rounded-full bg-green-500"></div>
            <code class="text-foreground">{{ url }}</code>
          </div>
        </div>
        <div v-else class="text-muted-foreground text-sm">
          {{ t('serviceDetail.noInstances') }}
        </div>
      </div>

      <!-- Recent Incidents -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h2 class="text-xl font-semibold text-foreground mb-4 flex items-center gap-2" role="heading" aria-level="2">
          <AlertCircle :size="20" aria-hidden="true" />
          {{ t('serviceDetail.recentIncidents') }}
        </h2>

        <!-- Loading State -->
        <div v-if="incidentsLoading" class="flex items-center justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>

        <!-- Incidents List -->
        <div v-else-if="incidentsData && incidentsData.content.length > 0" class="space-y-2">
          <div
            v-for="incident in incidentsData.content"
            :key="incident.id"
            class="bg-muted/50 rounded-lg p-4 hover:bg-accent/50 transition-colors cursor-pointer"
            @click="navigateToIncident(incident.id)"
            role="button"
            tabindex="0"
            @keydown.enter="navigateToIncident(incident.id)"
            :aria-label="`View incident ${incident.id}`"
          >
            <div class="flex items-start justify-between mb-2">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-sm font-medium text-foreground">#{{ incident.id }}</span>
                  <code class="px-2 py-0.5 bg-muted rounded text-xs text-foreground">
                    {{ incident.errorType }}
                  </code>
                </div>
                <p class="text-sm text-muted-foreground truncate">{{ incident.description }}</p>
              </div>
              <span :class="getIncidentStatusClass(incident.status)" class="px-2 py-1 rounded-full text-xs font-medium ml-2">
                {{ t(`incidents.status.${incident.status.toLowerCase()}`) }}
              </span>
            </div>
            <div class="flex items-center gap-4 text-xs text-muted-foreground">
              <span>{{ formatDate(incident.detectedAt) }}</span>
              <span>{{ incident.occurrences }} {{ t('serviceDetail.occurrences') }}</span>
            </div>
          </div>

          <!-- View All Link -->
          <router-link
            :to="{ name: 'incidents', query: { serviceName: service.name } }"
            class="inline-flex items-center gap-1 text-sm text-primary hover:underline mt-2"
          >
            {{ t('serviceDetail.viewAllIncidents') }}
            <ChevronRight :size="14" aria-hidden="true" />
          </router-link>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-8">
          <CheckCircle2 :size="48" class="mx-auto mb-2 text-green-500" aria-hidden="true" />
          <p class="text-muted-foreground">{{ t('serviceDetail.noIncidents') }}</p>
        </div>
      </div>

      <!-- Knowledge Base Documents -->
      <div class="bg-card border border-border rounded-lg p-6">
        <h2 class="text-xl font-semibold text-foreground mb-4 flex items-center gap-2" role="heading" aria-level="2">
          <FileText :size="20" aria-hidden="true" />
          {{ t('serviceDetail.documentation') }}
        </h2>

        <!-- Loading State -->
        <div v-if="documentsLoading" class="flex items-center justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>

        <!-- Documents Grid -->
        <div v-else-if="documentsData && documentsData.content.length > 0" class="space-y-2">
          <div
            v-for="doc in documentsData.content"
            :key="doc.id"
            class="bg-muted/50 rounded-lg p-4"
          >
            <div class="flex items-start justify-between mb-2">
              <div class="flex-1">
                <h3 class="text-sm font-medium text-foreground mb-1">{{ doc.name }}</h3>
                <div class="flex items-center gap-3 text-xs text-muted-foreground">
                  <span class="px-2 py-0.5 bg-muted rounded">{{ doc.type }}</span>
                  <span>{{ formatBytes(doc.size) }}</span>
                  <span>v{{ doc.version }}</span>
                </div>
              </div>
              <span
                :class="doc.status === 'ANALYZED' ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'"
                class="px-2 py-1 rounded-full text-xs font-medium ml-2"
              >
                {{ t(`knowledge.status.${doc.status.toLowerCase()}`) }}
              </span>
            </div>
            <p class="text-xs text-muted-foreground">
              {{ t('serviceDetail.uploaded') }}: {{ formatDate(doc.uploadedAt) }}
            </p>
          </div>

          <!-- View All Link -->
          <router-link
            :to="{ name: 'knowledge', query: { serviceId: service.name } }"
            class="inline-flex items-center gap-1 text-sm text-primary hover:underline mt-2"
          >
            {{ t('serviceDetail.viewAllDocuments') }}
            <ChevronRight :size="14" aria-hidden="true" />
          </router-link>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-8">
          <FileText :size="48" class="mx-auto mb-2 text-muted-foreground" aria-hidden="true" />
          <p class="text-muted-foreground">{{ t('serviceDetail.noDocuments') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
