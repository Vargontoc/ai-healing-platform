<script setup lang="ts">
import SkeletonLoader from '@/components/ui/SkeletonLoader.vue'

interface Props {
  rows?: number
  columns?: number
}

withDefaults(defineProps<Props>(), {
  rows: 5,
  columns: 6
})
</script>

<template>
  <div class="bg-card border border-border rounded-lg overflow-hidden">
    <!-- Table Header -->
    <div class="border-b border-border bg-muted/50">
      <div class="grid gap-4 p-4" :style="{ gridTemplateColumns: `repeat(${columns}, 1fr)` }">
        <SkeletonLoader
          v-for="col in columns"
          :key="`header-${col}`"
          variant="text"
          height="1.25rem"
          :animated="true"
        />
      </div>
    </div>

    <!-- Table Body -->
    <div class="divide-y divide-border">
      <div
        v-for="row in rows"
        :key="`row-${row}`"
        class="grid gap-4 p-4"
        :style="{ gridTemplateColumns: `repeat(${columns}, 1fr)` }"
      >
        <SkeletonLoader
          v-for="col in columns"
          :key="`cell-${row}-${col}`"
          variant="text"
          :animated="true"
        />
      </div>
    </div>
  </div>
</template>
