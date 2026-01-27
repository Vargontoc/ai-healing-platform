<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDocuments, useUploadDocument } from '@/composables/useDocuments'
import type { DocumentFilters } from '@/types/documents'
import { Search, FileText, Upload, RefreshCw, CheckCircle2, Clock, AlertCircle, File } from 'lucide-vue-next'
import CardGridSkeleton from '@/components/skeletons/CardGridSkeleton.vue'

const { t } = useI18n()

const filters = ref<DocumentFilters>({
  page: 0,
  size: 10
})

const searchQuery = ref('')
const typeFilter = ref<'all' | string>('all')
const statusFilter = ref<'all' | string>('all')
const showUploadModal = ref(false)

const { data, isLoading, isError, error, refetch } = useDocuments(filters)
const uploadMutation = useUploadDocument()

// Upload form
const uploadFile = ref<File | null>(null)
const uploadServiceId = ref('')
const uploadVersion = ref('')

const filteredData = computed(() => {
  if (!data.value) return null

  let result = data.value.content

  if (typeFilter.value !== 'all') {
    result = result.filter(doc => doc.type === typeFilter.value)
  }

  if (statusFilter.value !== 'all') {
    result = result.filter(doc => doc.status === statusFilter.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(doc => doc.name.toLowerCase().includes(query))
  }

  return {
    ...data.value,
    content: result,
    numberOfElements: result.length
  }
})

const documentTypes = computed(() => {
  if (!data.value) return []
  const types = new Set(data.value.content.map(doc => doc.type))
  return Array.from(types)
})

const getStatusBadgeClass = (status: string) => {
  const baseClasses = 'inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium'

  switch (status) {
    case 'ANALYZED':
      return `${baseClasses} bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300`
    case 'PENDING':
      return `${baseClasses} bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-300`
    case 'ERROR':
      return `${baseClasses} bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300`
    default:
      return `${baseClasses} bg-gray-100 text-gray-800`
  }
}

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'ANALYZED':
      return CheckCircle2
    case 'PENDING':
      return Clock
    case 'ERROR':
      return AlertCircle
    default:
      return File
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

const formatSize = (bytes: number) => {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

const handleSearch = () => {
  filters.value = {
    ...filters.value,
    search: searchQuery.value || undefined,
    page: 0
  }
  refetch()
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

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    uploadFile.value = target.files[0]
  }
}

const isUploadDisabled = computed(() => {
  return !uploadFile.value || !uploadServiceId.value || !uploadVersion.value || uploadMutation.isPending.value
})

const handleUpload = async () => {
  if (!uploadFile.value || !uploadServiceId.value || !uploadVersion.value) {
    return
  }

  try {
    await uploadMutation.mutateAsync({
      file: uploadFile.value,
      serviceId: uploadServiceId.value,
      version: uploadVersion.value
    })

    // Reset form
    uploadFile.value = null
    uploadServiceId.value = ''
    uploadVersion.value = ''
    showUploadModal.value = false

    // Refresh list
    refetch()
  } catch (err) {
    console.error('Upload failed:', err)
  }
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-foreground" role="heading" aria-level="1">
        {{ t('knowledge.title') }}
      </h1>
      <div class="flex items-center gap-2">
        <button
          @click="() => refetch()"
          class="inline-flex items-center gap-2 px-4 py-2 bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80 transition-colors focus:outline-none focus:ring-2 focus:ring-secondary focus:ring-offset-2"
          :aria-label="t('common.refresh')"
        >
          <RefreshCw :size="16" aria-hidden="true" />
          {{ t('common.refresh') }}
        </button>
        <button
          @click="showUploadModal = true"
          class="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2"
          :aria-label="t('knowledge.upload')"
        >
          <Upload :size="16" aria-hidden="true" />
          {{ t('knowledge.upload') }}
        </button>
      </div>
    </div>

    <!-- Search and Filters -->
    <div class="bg-card border border-border rounded-lg p-4 mb-6" role="search">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <label for="document-search" class="sr-only">{{ t('knowledge.search') }}</label>
          <div class="relative">
            <Search :size="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" aria-hidden="true" />
            <input
              id="document-search"
              v-model="searchQuery"
              type="text"
              :placeholder="t('knowledge.searchPlaceholder')"
              @keyup.enter="handleSearch"
              class="w-full pl-10 pr-4 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              :aria-label="t('knowledge.searchPlaceholder')"
            />
          </div>
        </div>

        <label for="type-filter" class="sr-only">{{ t('knowledge.type') }}</label>
        <select
          id="type-filter"
          v-model="typeFilter"
          class="px-4 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          :aria-label="t('knowledge.filters.allTypes')"
        >
          <option value="all">{{ t('knowledge.filters.allTypes') }}</option>
          <option v-for="type in documentTypes" :key="type" :value="type">
            {{ type }}
          </option>
        </select>

        <label for="status-filter" class="sr-only">{{ t('knowledge.status') }}</label>
        <select
          id="status-filter"
          v-model="statusFilter"
          class="px-4 py-2 bg-background border border-border rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          :aria-label="t('knowledge.filters.allStatus')"
        >
          <option value="all">{{ t('knowledge.filters.allStatus') }}</option>
          <option value="ANALYZED">{{ t('knowledge.status.analyzed') }}</option>
          <option value="PENDING">{{ t('knowledge.status.pending') }}</option>
          <option value="ERROR">{{ t('knowledge.status.error') }}</option>
        </select>
      </div>
    </div>

    <!-- Loading State -->
    <CardGridSkeleton v-if="isLoading" :cards="6" :columns="3" />

    <!-- Error State -->
    <div v-else-if="isError" class="bg-card border border-border rounded-lg p-8 text-center" role="alert" aria-live="polite">
      <AlertCircle :size="48" class="mx-auto mb-4 text-destructive" aria-hidden="true" />
      <p class="text-destructive font-medium mb-2">{{ t('common.error') }}</p>
      <p class="text-muted-foreground text-sm">{{ error?.message }}</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="!filteredData || filteredData.content.length === 0" class="bg-card border border-border rounded-lg p-8 text-center" role="status">
      <FileText :size="48" class="mx-auto mb-4 text-muted-foreground" aria-hidden="true" />
      <p class="text-muted-foreground">{{ t('knowledge.noResults') }}</p>
    </div>

    <!-- Documents Grid -->
    <div v-else>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
        <div
          v-for="doc in filteredData.content"
          :key="doc.id"
          class="bg-card border border-border rounded-lg p-4 hover:shadow-md transition-shadow"
        >
          <div class="flex items-start justify-between mb-3">
            <div class="flex items-center gap-2">
              <FileText :size="20" class="text-primary" />
              <h3 class="font-medium text-foreground truncate">{{ doc.name }}</h3>
            </div>
            <span :class="getStatusBadgeClass(doc.status)">
              <component :is="getStatusIcon(doc.status)" :size="12" />
              {{ t(`knowledge.status.${doc.status.toLowerCase()}`) }}
            </span>
          </div>

          <div class="space-y-2 text-sm text-muted-foreground">
            <div class="flex justify-between">
              <span>{{ t('knowledge.type') }}:</span>
              <span class="font-medium text-foreground">{{ doc.type }}</span>
            </div>
            <div class="flex justify-between">
              <span>{{ t('knowledge.size') }}:</span>
              <span class="font-medium text-foreground">{{ formatSize(doc.size) }}</span>
            </div>
            <div class="flex justify-between">
              <span>{{ t('knowledge.service') }}:</span>
              <span class="font-medium text-foreground truncate">{{ doc.serviceId }}</span>
            </div>
            <div class="flex justify-between">
              <span>{{ t('knowledge.uploaded') }}:</span>
              <span class="font-medium text-foreground">{{ formatDate(doc.uploadedAt) }}</span>
            </div>
            <div v-if="doc.analyzedAt" class="flex justify-between">
              <span>{{ t('knowledge.analyzed') }}:</span>
              <span class="font-medium text-foreground">{{ formatDate(doc.analyzedAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="bg-card border border-border rounded-lg px-6 py-4 flex items-center justify-between">
        <div class="flex items-center gap-2 text-sm text-muted-foreground">
          <span>{{ t('incidents.pagination.rowsPerPage') }}:</span>
          <select
            :value="filters.size"
            @change="(e) => setPageSize(Number((e.target as HTMLSelectElement).value))"
            class="px-2 py-1 bg-background border border-border rounded-md text-foreground"
          >
            <option :value="6">6</option>
            <option :value="12">12</option>
            <option :value="24">24</option>
            <option :value="48">48</option>
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

    <!-- Upload Modal -->
    <Transition name="modal">
      <div
        v-if="showUploadModal"
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 backdrop-blur-sm"
        @click.self="showUploadModal = false"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="t('knowledge.uploadDocument')"
      >
        <div class="bg-card border border-border rounded-lg p-6 w-full max-w-md modal-content">
          <h2 id="modal-title" class="text-xl font-bold text-foreground mb-4">{{ t('knowledge.uploadDocument') }}</h2>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-foreground mb-2">{{ t('knowledge.file') }}</label>
            <input
              type="file"
              @change="handleFileSelect"
              class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
            />
            <p v-if="uploadFile" class="text-sm text-muted-foreground mt-1">
              {{ uploadFile.name }} ({{ formatSize(uploadFile.size) }})
            </p>
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-2">{{ t('knowledge.serviceId') }}</label>
            <input
              v-model="uploadServiceId"
              type="text"
              class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
              placeholder="e.g., payment-service"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-2">{{ t('knowledge.version') }}</label>
            <input
              v-model="uploadVersion"
              type="text"
              class="w-full px-3 py-2 bg-background border border-border rounded-md text-foreground"
              placeholder="e.g., 1.0.0"
            />
          </div>
        </div>

        <div class="flex gap-2 mt-6">
          <button
            @click="showUploadModal = false"
            class="flex-1 px-4 py-2 bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80 transition-colors"
          >
            {{ t('common.cancel') }}
          </button>
          <button
            @click="handleUpload"
            :disabled="isUploadDisabled"
            class="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ uploadMutation.isPending.value ? t('knowledge.uploading') : t('knowledge.upload') }}
          </button>
        </div>
      </div>
    </div>
    </Transition>
  </div>
</template>

<style scoped>
/* Modal transition */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-content {
  animation: modal-slide-in 0.3s ease-out;
}

.modal-leave-active .modal-content {
  animation: modal-slide-out 0.2s ease-in;
}

@keyframes modal-slide-in {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes modal-slide-out {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.95) translateY(-20px);
  }
}
</style>
