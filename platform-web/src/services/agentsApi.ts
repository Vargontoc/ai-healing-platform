import { apiClient } from '@/lib/api'
import type {
  AgentInstance,
  AgentListResponse,
  AgentLogsResponse,
  AgentActionResponse
} from '@/types/agents'

export const agentsApi = {
  /**
   * Get all agents
   */
  getAgents: async (): Promise<AgentListResponse> => {
    return apiClient.get<AgentListResponse>('/api/v1/agents')
  },

  /**
   * Get agent details by ID
   */
  getAgent: async (id: string): Promise<AgentInstance> => {
    return apiClient.get<AgentInstance>(`/api/v1/agents/${id}`)
  },

  /**
   * Start an agent
   */
  startAgent: async (id: string): Promise<AgentActionResponse> => {
    return apiClient.post<AgentActionResponse>(`/api/v1/agents/${id}/start`)
  },

  /**
   * Stop an agent
   */
  stopAgent: async (id: string): Promise<AgentActionResponse> => {
    return apiClient.post<AgentActionResponse>(`/api/v1/agents/${id}/stop`)
  },

  /**
   * Get agent logs
   */
  getAgentLogs: async (
    id: string,
    page: number = 0,
    size: number = 100
  ): Promise<AgentLogsResponse> => {
    const params = new URLSearchParams()
    params.append('page', page.toString())
    params.append('size', size.toString())
    params.append('sort', 'timestamp,desc')

    return apiClient.get<AgentLogsResponse>(`/api/v1/agents/${id}/logs?${params.toString()}`)
  }
}
