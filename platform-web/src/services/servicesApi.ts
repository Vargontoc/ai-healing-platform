import { apiClient } from '@/lib/api'
import type { ServiceListResponse, ServiceInfo } from '@/types/services'

export const servicesApi = {
  getServices: async (): Promise<ServiceListResponse> => {
    return apiClient.get<ServiceListResponse>('/api/v1/services')
  },

  getService: async (name: string): Promise<ServiceInfo> => {
    const response = await apiClient.get<ServiceListResponse>('/api/v1/services')
    const service = response.services.find(s => s.name === name)
    if (!service) {
      throw new Error(`Service ${name} not found`)
    }
    return service
  }
}
