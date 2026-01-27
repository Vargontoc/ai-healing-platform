import { useQuery } from '@tanstack/vue-query'
import { incidentsApi } from '@/services/incidentsApi'
import type { IncidentFilters } from '@/types/incidents'
import type { MaybeRef } from 'vue'
import { unref } from 'vue'

export function useIncidents(filters: MaybeRef<IncidentFilters> = {}) {
  return useQuery({
    queryKey: ['incidents', filters],
    queryFn: () => incidentsApi.getIncidents(unref(filters)),
    staleTime: 30000 // 30 seconds
  })
}

export function useIncident(id: MaybeRef<number>) {
  return useQuery({
    queryKey: ['incident', id],
    queryFn: () => incidentsApi.getIncident(unref(id)),
    enabled: () => !!unref(id)
  })
}
