import { useQuery } from '@tanstack/vue-query'
import { statsApi } from '@/services/statsApi'
import { servicesApi } from '@/services/servicesApi'
import { computed } from 'vue'
import type { ServiceStats } from '@/types/stats'

export function useIncidentStats(refetchInterval: number = 30000) {
  return useQuery({
    queryKey: ['incidentStats'],
    queryFn: statsApi.getIncidentStats,
    refetchInterval,
    refetchIntervalInBackground: true,
    staleTime: 0
  })
}

export function useServiceStats(refetchInterval: number = 30000) {
  const { data: servicesData, ...rest } = useQuery({
    queryKey: ['serviceStats'],
    queryFn: servicesApi.getServices,
    refetchInterval,
    refetchIntervalInBackground: true,
    staleTime: 0
  })

  const stats = computed<ServiceStats | null>(() => {
    if (!servicesData.value) return null

    const services = servicesData.value.services
    const totalServices = services.length
    const upServices = services.filter(s => s.status === 'UP').length
    const downServices = services.filter(s => s.status === 'DOWN').length
    const totalInstances = services.reduce((sum, s) => sum + s.instances, 0)

    return {
      totalServices,
      upServices,
      downServices,
      totalInstances
    }
  })

  return {
    data: stats,
    ...rest
  }
}
