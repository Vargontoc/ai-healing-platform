<script setup lang="ts">
import SkeletonLoader from '@/components/ui/SkeletonLoader.vue'

interface Props {
  cards?: number
  columns?: 1 | 2 | 3
}

withDefaults(defineProps<Props>(), {
  cards: 6,
  columns: 3
})
</script>

<template>
  <div
    class="grid gap-4"
    :class="{
      'grid-cols-1': columns === 1,
      'grid-cols-1 md:grid-cols-2': columns === 2,
      'grid-cols-1 md:grid-cols-2 lg:grid-cols-3': columns === 3
    }"
  >
    <div
      v-for="card in cards"
      :key="`card-${card}`"
      class="bg-card border border-border rounded-lg p-4 space-y-3"
    >
      <!-- Header -->
      <div class="flex items-start justify-between">
        <div class="flex items-center gap-2 flex-1">
          <SkeletonLoader variant="circular" width="1.25rem" height="1.25rem" />
          <SkeletonLoader variant="text" width="60%" height="1.25rem" />
        </div>
        <SkeletonLoader variant="rectangular" width="5rem" height="1.5rem" />
      </div>

      <!-- Content -->
      <div class="space-y-2">
        <div class="flex justify-between">
          <SkeletonLoader variant="text" width="30%" />
          <SkeletonLoader variant="text" width="40%" />
        </div>
        <div class="flex justify-between">
          <SkeletonLoader variant="text" width="35%" />
          <SkeletonLoader variant="text" width="35%" />
        </div>
        <div class="flex justify-between">
          <SkeletonLoader variant="text" width="25%" />
          <SkeletonLoader variant="text" width="50%" />
        </div>
      </div>
    </div>
  </div>
</template>
