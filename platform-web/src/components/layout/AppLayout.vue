<script setup lang="ts">
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const sidebarOpen = ref(true)

function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}
</script>

<template>
  <div class="flex h-screen bg-background">
    <Sidebar :open="sidebarOpen" @toggle="toggleSidebar" />
    <div class="flex flex-col flex-1 overflow-hidden">
      <Header @toggle-sidebar="toggleSidebar" />
      <main class="flex-1 overflow-y-auto p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>
