import { useQuery } from '@tanstack/vue-query'
import { servicesApi } from '@/services/servicesApi'
import { unref, type MaybeRef } from 'vue'

export function useServices(refetchInterval: number = 30000) {
  return useQuery({
    queryKey: ['services'],
    queryFn: servicesApi.getServices,
    refetchInterval, // Auto-refresh every 30 seconds
    refetchIntervalInBackground: true,
    staleTime: 0
  })
}

export function useService(name: MaybeRef<string>, refetchInterval: number = 30000) {
  return useQuery({
    queryKey: ['service', name],
    queryFn: () => servicesApi.getService(unref(name)),
    enabled: () => !!unref(name),
    refetchInterval,
    refetchIntervalInBackground: true,
    staleTime: 0
  })
}
