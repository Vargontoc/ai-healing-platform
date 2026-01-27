import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/service-map',
    name: 'service-map',
    component: () => import('@/views/ServiceMap.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/services/:name',
    name: 'service-detail',
    component: () => import('@/views/ServiceDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/agent-console',
    name: 'agent-console',
    component: () => import('@/views/AgentConsole.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/incidents',
    name: 'incidents',
    component: () => import('@/views/Incidents.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/incidents/:id',
    name: 'incident-detail',
    component: () => import('@/views/IncidentDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/knowledge',
    name: 'knowledge',
    component: () => import('@/views/Knowledge.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
  } else if (to.name === 'login' && authStore.isAuthenticated) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router
