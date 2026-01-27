import { apiClient } from '@/lib/api'
import type { DocumentPageResponse, DocumentFilters, Document } from '@/types/documents'

export const documentsApi = {
  getDocuments: async (filters: DocumentFilters = {}): Promise<DocumentPageResponse> => {
    const params = new URLSearchParams()

    if (filters.type) {
      params.append('type', filters.type)
    }
    if (filters.serviceId) {
      params.append('serviceId', filters.serviceId)
    }
    if (filters.status) {
      params.append('status', filters.status)
    }
    if (filters.search) {
      params.append('search', filters.search)
    }
    if (filters.page !== undefined) {
      params.append('page', filters.page.toString())
    }
    if (filters.size !== undefined) {
      params.append('size', filters.size.toString())
    }

    const queryString = params.toString()
    const endpoint = queryString ? `/api/v1/documents?${queryString}` : '/api/v1/documents'

    return apiClient.get<DocumentPageResponse>(endpoint)
  },

  getDocument: async (id: string): Promise<Document> => {
    return apiClient.get<Document>(`/api/v1/documents/${id}`)
  },

  uploadDocument: async (file: File, serviceId: string, version: string): Promise<string> => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('serviceId', serviceId)
    formData.append('version', version)

    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/v1/documents`, {
      method: 'POST',
      headers: {
        'X-API-KEY': import.meta.env.VITE_API_KEY || 'general-key-456'
      },
      body: formData
    })

    if (!response.ok) {
      throw new Error(`Upload failed: ${response.statusText}`)
    }

    return response.text()
  }
}
