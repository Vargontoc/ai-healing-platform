<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'text' | 'circular' | 'rectangular' | 'card'
  width?: string
  height?: string
  rows?: number
  animated?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'rectangular',
  width: '100%',
  height: '1rem',
  rows: 1,
  animated: true
})

const skeletonClass = computed(() => {
  const baseClass = 'bg-muted'
  const animationClass = props.animated ? 'animate-pulse' : ''

  let variantClass = ''
  switch (props.variant) {
    case 'text':
      variantClass = 'h-4 rounded'
      break
    case 'circular':
      variantClass = 'rounded-full aspect-square'
      break
    case 'rectangular':
      variantClass = 'rounded-md'
      break
    case 'card':
      variantClass = 'rounded-lg'
      break
  }

  return `${baseClass} ${variantClass} ${animationClass}`.trim()
})

const style = computed(() => ({
  width: props.width,
  height: props.height
}))
</script>

<template>
  <div v-if="rows > 1" class="space-y-2">
    <div
      v-for="index in rows"
      :key="index"
      :class="skeletonClass"
      :style="style"
      role="status"
      aria-label="Loading..."
    />
  </div>
  <div
    v-else
    :class="skeletonClass"
    :style="style"
    role="status"
    aria-label="Loading..."
  />
</template>
