<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from './components/layout/AppLayout.vue'
import ErrorBoundary from './components/ErrorBoundary.vue'

const route = useRoute()

const isLoginPage = computed(() => route.name === 'login')
</script>

<template>
  <ErrorBoundary>
    <div v-if="isLoginPage">
      <router-view v-slot="{ Component }">
        <transition
          name="fade"
          mode="out-in"
          appear
        >
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
    <AppLayout v-else>
      <router-view v-slot="{ Component }">
        <transition
          name="page"
          mode="out-in"
          appear
        >
          <component :is="Component" />
        </transition>
      </router-view>
    </AppLayout>
  </ErrorBoundary>
</template>

<style scoped>
/* Page transition */
.page-enter-active {
  transition: all 0.2s ease-out;
}

.page-leave-active {
  transition: all 0.15s ease-in;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Fade transition for login */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
