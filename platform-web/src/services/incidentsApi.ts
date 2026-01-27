import { apiClient } from '@/lib/api'
import type {
  IncidentPageResponse,
  IncidentFilters,
  Incident,
  IncidentStatusUpdate,
  IncidentAssignment
} from '@/types/incidents'

export const incidentsApi = {
  getIncidents: async (filters: IncidentFilters = {}): Promise<IncidentPageResponse> => {
    const params = new URLSearchParams()

    if (filters.status) {
      params.append('status', filters.status)
    }
    if (filters.serviceName) {
      params.append('serviceName', filters.serviceName)
    }
    if (filters.page !== undefined) {
      params.append('page', filters.page.toString())
    }
    if (filters.size !== undefined) {
      params.append('size', filters.size.toString())
    }

    const queryString = params.toString()
    const endpoint = queryString ? `/api/v1/incidents?${queryString}` : '/api/v1/incidents'

    return apiClient.get<IncidentPageResponse>(endpoint)
  },

  getIncident: async (id: number): Promise<Incident> => {
    return apiClient.get<Incident>(`/api/v1/incidents/${id}`)
  },

  updateStatus: async (id: number, update: IncidentStatusUpdate): Promise<Incident> => {
    return apiClient.put<Incident>(`/api/v1/incidents/${id}/status`, update)
  },

  assignIncident: async (id: number, assignment: IncidentAssignment): Promise<Incident> => {
    return apiClient.put<Incident>(`/api/v1/incidents/${id}/assign`, assignment)
  }
}
