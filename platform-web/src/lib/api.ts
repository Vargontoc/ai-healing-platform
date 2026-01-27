import { useAuthStore } from '@/stores/auth'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const API_KEY = import.meta.env.VITE_API_KEY || 'general-key-456'

export interface ApiError {
  message: string
  status: number
}

class ApiClient {
  private baseURL: string
  private apiKey: string

  constructor(baseURL: string, apiKey: string) {
    this.baseURL = baseURL
    this.apiKey = apiKey
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {},
    retryCount: number = 0
  ): Promise<T> {
    const authStore = useAuthStore()

    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      'X-API-KEY': this.apiKey,
      ...(options.headers as Record<string, string>)
    }

    if (authStore.accessToken) {
      headers['Authorization'] = `Bearer ${authStore.accessToken}`
    }

    const response = await fetch(`${this.baseURL}${endpoint}`, {
      ...options,
      headers
    })

    if (!response.ok) {
      const error: ApiError = {
        message: `HTTP error ${response.status}`,
        status: response.status
      }

      // Only try to refresh token once
      if (response.status === 401 && retryCount === 0) {
        const refreshed = await authStore.refreshAccessToken()
        if (refreshed) {
          // Retry the request once
          return this.request<T>(endpoint, options, retryCount + 1)
        } else {
          // Logout user
          authStore.clearAuth()
        }
      }

      throw error
    }

    return response.json()
  }

  async get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'GET' })
  }

  async post<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined
    })
  }

  async put<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined
    })
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' })
  }
}

export const apiClient = new ApiClient(API_BASE_URL, API_KEY)
