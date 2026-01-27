import { apiClient } from '@/lib/api'
import type { IncidentStats } from '@/types/stats'

export const statsApi = {
  getIncidentStats: async (): Promise<IncidentStats> => {
    return apiClient.get<IncidentStats>('/api/v1/incidents/stats')
  }
}
