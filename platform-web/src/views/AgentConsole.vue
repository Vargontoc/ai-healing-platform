<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAgents, useAgent, useAgentLogs, useStartAgent, useStopAgent } from '@/composables/useAgents'
import type { AgentInstance } from '@/types/agents'
import {
  Activity,
  Play,
  Square,
  RefreshCw,
  AlertCircle,
  CheckCircle2,
  Clock,
  Terminal,
  Info,
  AlertTriangle,
  XCircle,
  TrendingUp,
  Server
} from 'lucide-vue-next'
import CardGridSkeleton from '@/components/skeletons/CardGridSkeleton.vue'

const { t } = useI18n()

const selectedAgentId = ref<string | null>(null)
const logsPage = ref(0)
const logsSize = ref(50)

// Queries
const { data: agentsData, isLoading: loadingAgents, isError: errorAgents, refetch: refetchAgents } = useAgents()
const { data: selectedAgent } = useAgent(computed(() => selectedAgentId.value || ''))
const { data: logsData, isLoading: loadingLogs } = useAgentLogs(
  computed(() => selectedAgentId.value || ''),
  logsPage,
  logsSize
)

// Mutations
const startMutation = useStartAgent()
const stopMutation = useStopAgent()

const agents = computed(() => agentsData.value?.content || [])

const selectAgent = (agent: AgentInstance) => {
  selectedAgentId.value = agent.id
  logsPage.value = 0
}

const handleStart = async (agentId: string) => {
  try {
    await startMutation.mutateAsync(agentId)
  } catch (error) {
    console.error('Failed to start agent:', error)
  }
}

const handleStop = async (agentId: string) => {
  try {
    await stopMutation.mutateAsync(agentId)
  } catch (error) {
    console.error('Failed to stop agent:', error)
  }
}

const handleRefresh = () => {
  refetchAgents()
}

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'RUNNING':
      return CheckCircle2
    case 'STOPPED':
      return Clock
    case 'ERROR':
      return AlertCircle
    default:
      return Activity
  }
}

const getStatusBadgeClass = (status: string) => {
  const base = 'inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium'
  switch (status) {
    case 'RUNNING':
      return `${base} bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300`
    case 'STOPPED':
      return `${base} bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-300`
    case 'ERROR':
      return `${base} bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300`
    default:
      return `${base} bg-muted text-muted-foreground`
  }
}

const getLogLevelIcon = (level: string) => {
  switch (level) {
    case 'INFO':
      return Info
    case 'WARN':
      return AlertTriangle
    case 'ERROR':
      return XCircle
    default:
      return Terminal
  }
}

const getLogLevelClass = (level: string) => {
  switch (level) {
    case 'INFO':
      return 'text-blue-600 dark:text-blue-400'
    case 'WARN':
      return 'text-yellow-600 dark:text-yellow-400'
    case 'ERROR':
      return 'text-red-600 dark:text-red-400'
    default:
      return 'text-muted-foreground'
  }
}

const formatDate = (dateString: string | null) => {
  if (!dateString) return t('agentConsole.never')
  const date = new Date(dateString)
  return new Intl.DateTimeFormat(t('common.locale'), {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(date)
}

const formatDuration = (ms: number) => {
  if (ms < 1000) return `${ms}ms`
  if (ms < 60000) return `${(ms / 1000).toFixed(1)}s`
  return `${(ms / 60000).toFixed(1)}m`
}

const formatPercentage = (value: number) => {
  return `${(value * 100).toFixed(1)}%`
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-foreground" role="heading" aria-level="1">
        {{ t('agentConsole.title') }}
      </h1>
      <button
        @click="handleRefresh"
        class="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2"
        :aria-label="t('common.refresh')"
      >
        <RefreshCw :size="16" aria-hidden="true" />
        {{ t('common.refresh') }}
      </button>
    </div>

    <!-- Loading State -->
    <CardGridSkeleton v-if="loadingAgents" :cards="3" :columns="3" />

    <!-- Error State -->
    <div v-else-if="errorAgents" class="bg-card border border-border rounded-lg p-8 text-center" role="alert">
      <AlertCircle :size="48" class="mx-auto mb-4 text-destructive" aria-hidden="true" />
      <p class="text-destructive font-medium">{{ t('common.error') }}</p>
    </div>

    <!-- Agents Grid -->
    <div v-else-if="agents.length > 0" class="space-y-6">
      <!-- Agents List -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="agent in agents"
          :key="agent.id"
          class="bg-card border border-border rounded-lg p-4 hover:shadow-md transition-all cursor-pointer"
          :class="{ 'ring-2 ring-primary': selectedAgentId === agent.id }"
          @click="selectAgent(agent)"
          role="button"
          :aria-label="`${t('agentConsole.selectAgent')}: ${agent.name}`"
          tabindex="0"
        >
          <!-- Header -->
          <div class="flex items-start justify-between mb-3">
            <div class="flex items-center gap-2 flex-1">
              <Server :size="20" class="text-primary" aria-hidden="true" />
              <h3 class="font-medium text-foreground truncate">{{ agent.name }}</h3>
            </div>
            <span :class="getStatusBadgeClass(agent.status)">
              <component :is="getStatusIcon(agent.status)" :size="12" aria-hidden="true" />
              {{ t(`agentConsole.status.${agent.status.toLowerCase()}`) }}
            </span>
          </div>

          <!-- Type -->
          <div class="text-sm text-muted-foreground mb-3">
            {{ t(`agentConsole.type.${agent.type.toLowerCase()}`) }}
          </div>

          <!-- Metrics -->
          <div class="space-y-2 text-sm mb-4">
            <div class="flex justify-between">
              <span class="text-muted-foreground">{{ t('agentConsole.totalExecutions') }}:</span>
              <span class="font-medium text-foreground">{{ agent.metrics.totalExecutions }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-muted-foreground">{{ t('agentConsole.successRate') }}:</span>
              <span class="font-medium text-foreground">{{ formatPercentage(agent.metrics.successRate) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-muted-foreground">{{ t('agentConsole.avgTime') }}:</span>
              <span class="font-medium text-foreground">{{ formatDuration(agent.metrics.averageExecutionTime) }}</span>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex gap-2">
            <button
              v-if="agent.status !== 'RUNNING'"
              @click.stop="handleStart(agent.id)"
              :disabled="startMutation.isPending.value"
              class="flex-1 inline-flex items-center justify-center gap-1 px-3 py-1.5 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-sm focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
              :aria-label="t('agentConsole.startAgent')"
            >
              <Play :size="14" aria-hidden="true" />
              {{ t('agentConsole.start') }}
            </button>
            <button
              v-if="agent.status === 'RUNNING'"
              @click.stop="handleStop(agent.id)"
              :disabled="stopMutation.isPending.value"
              class="flex-1 inline-flex items-center justify-center gap-1 px-3 py-1.5 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-sm focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
              :aria-label="t('agentConsole.stopAgent')"
            >
              <Square :size="14" aria-hidden="true" />
              {{ t('agentConsole.stop') }}
            </button>
          </div>
        </div>
      </div>

      <!-- Agent Details Panel -->
      <div v-if="selectedAgentId && selectedAgent" class="bg-card border border-border rounded-lg p-6">
        <h2 class="text-2xl font-bold text-foreground mb-4">
          {{ selectedAgent.name }}
        </h2>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <!-- Configuration -->
          <div class="space-y-3">
            <h3 class="text-lg font-semibold text-foreground flex items-center gap-2">
              <Activity :size="20" aria-hidden="true" />
              {{ t('agentConsole.configuration') }}
            </h3>
            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-muted-foreground">{{ t('agentConsole.autoRestart') }}:</span>
                <span class="font-medium text-foreground">
                  {{ selectedAgent.configuration.autoRestart ? t('common.yes') : t('common.no') }}
                </span>
              </div>
              <div class="flex justify-between">
                <span class="text-muted-foreground">{{ t('agentConsole.maxConcurrentTasks') }}:</span>
                <span class="font-medium text-foreground">{{ selectedAgent.configuration.maxConcurrentTasks }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-muted-foreground">{{ t('agentConsole.timeout') }}:</span>
                <span class="font-medium text-foreground">{{ formatDuration(selectedAgent.configuration.timeout) }}</span>
              </div>
            </div>
          </div>

          <!-- Execution Info -->
          <div class="space-y-3">
            <h3 class="text-lg font-semibold text-foreground flex items-center gap-2">
              <TrendingUp :size="20" aria-hidden="true" />
              {{ t('agentConsole.execution') }}
            </h3>
            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-muted-foreground">{{ t('agentConsole.createdAt') }}:</span>
                <span class="font-medium text-foreground">{{ formatDate(selectedAgent.createdAt) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-muted-foreground">{{ t('agentConsole.lastExecution') }}:</span>
                <span class="font-medium text-foreground">{{ formatDate(selectedAgent.lastExecution) }}</span>
              </div>
              <div v-if="selectedAgent.metrics.lastError" class="flex flex-col gap-1">
                <span class="text-muted-foreground">{{ t('agentConsole.lastError') }}:</span>
                <span class="font-mono text-xs text-destructive break-words">{{ selectedAgent.metrics.lastError }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Logs Section -->
        <div class="border-t border-border pt-6">
          <h3 class="text-lg font-semibold text-foreground flex items-center gap-2 mb-4">
            <Terminal :size="20" aria-hidden="true" />
            {{ t('agentConsole.logs') }}
          </h3>

          <!-- Loading Logs -->
          <div v-if="loadingLogs" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-2"></div>
            <p class="text-sm text-muted-foreground">{{ t('common.loading') }}</p>
          </div>

          <!-- Logs List -->
          <div v-else-if="logsData && logsData.logs.length > 0" class="space-y-2 max-h-96 overflow-y-auto">
            <div
              v-for="log in logsData.logs"
              :key="log.id"
              class="bg-muted/50 rounded-md p-3 font-mono text-xs"
            >
              <div class="flex items-start gap-2 mb-1">
                <component
                  :is="getLogLevelIcon(log.level)"
                  :size="14"
                  :class="getLogLevelClass(log.level)"
                  aria-hidden="true"
                />
                <span :class="getLogLevelClass(log.level)" class="font-bold">{{ log.level }}</span>
                <span class="text-muted-foreground">{{ formatDate(log.timestamp) }}</span>
              </div>
              <p class="text-foreground break-words pl-5">{{ log.message }}</p>
              <pre v-if="log.stackTrace" class="mt-2 text-xs text-destructive overflow-x-auto pl-5">{{ log.stackTrace }}</pre>
            </div>

            <!-- Pagination Info -->
            <div class="text-center text-sm text-muted-foreground pt-4">
              {{ t('agentConsole.showingLogs', { count: logsData.logs.length, total: logsData.total }) }}
            </div>
          </div>

          <!-- No Logs -->
          <div v-else class="text-center py-8 text-muted-foreground">
            {{ t('agentConsole.noLogs') }}
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="bg-card border border-border rounded-lg p-8 text-center">
      <Server :size="48" class="mx-auto mb-4 text-muted-foreground" aria-hidden="true" />
      <p class="text-muted-foreground">{{ t('agentConsole.noAgents') }}</p>
    </div>
  </div>
</template>
