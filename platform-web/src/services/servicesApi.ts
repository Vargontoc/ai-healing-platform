import { apiClient } from '@/lib/api'
import type { ServiceListResponse, ServiceDetail } from '@/types/services'

export const servicesApi = {
  getServices: async (): Promise<ServiceListResponse> => {
    return apiClient.get<ServiceListResponse>('/api/v1/services')
  },

  getService: async (name: string): Promise<ServiceDetail> => {
    return apiClient.get<ServiceDetail>(`/api/v1/services/${name}`)
  }
}
