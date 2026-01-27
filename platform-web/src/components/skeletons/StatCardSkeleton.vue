<script setup lang="ts">
import SkeletonLoader from '@/components/ui/SkeletonLoader.vue'
</script>

<template>
  <div class="bg-card border border-border rounded-lg p-6 space-y-4">
    <!-- Title -->
    <div class="flex items-center justify-between">
      <SkeletonLoader variant="text" width="40%" height="1.5rem" />
      <SkeletonLoader variant="circular" width="2rem" height="2rem" />
    </div>

    <!-- Main stat -->
    <SkeletonLoader variant="text" width="30%" height="2.5rem" />

    <!-- Sub stats -->
    <div class="space-y-2">
      <div class="flex justify-between">
        <SkeletonLoader variant="text" width="35%" />
        <SkeletonLoader variant="text" width="20%" />
      </div>
      <div class="flex justify-between">
        <SkeletonLoader variant="text" width="40%" />
        <SkeletonLoader variant="text" width="20%" />
      </div>
      <div class="flex justify-between">
        <SkeletonLoader variant="text" width="30%" />
        <SkeletonLoader variant="text" width="20%" />
      </div>
    </div>
  </div>
</template>
