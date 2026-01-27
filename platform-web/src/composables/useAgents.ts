import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { agentsApi } from '@/services/agentsApi'
import { unref, type MaybeRef } from 'vue'

/**
 * Get all agents with auto-refresh
 */
export function useAgents(refetchInterval: number = 5000) {
  return useQuery({
    queryKey: ['agents'],
    queryFn: agentsApi.getAgents,
    refetchInterval,
    refetchIntervalInBackground: true,
    staleTime: 0
  })
}

/**
 * Get single agent details
 */
export function useAgent(id: MaybeRef<string>) {
  return useQuery({
    queryKey: ['agent', id],
    queryFn: () => agentsApi.getAgent(unref(id)),
    enabled: !!unref(id)
  })
}

/**
 * Get agent logs with pagination
 */
export function useAgentLogs(
  id: MaybeRef<string>,
  page: MaybeRef<number> = 0,
  size: MaybeRef<number> = 100
) {
  return useQuery({
    queryKey: ['agentLogs', id, page, size],
    queryFn: () => agentsApi.getAgentLogs(unref(id), unref(page), unref(size)),
    enabled: !!unref(id),
    refetchInterval: 5000, // Auto-refresh logs every 5 seconds
    refetchIntervalInBackground: true
  })
}

/**
 * Start agent mutation
 */
export function useStartAgent() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => agentsApi.startAgent(id),
    onSuccess: () => {
      // Invalidate agents list and specific agent query
      queryClient.invalidateQueries({ queryKey: ['agents'] })
      queryClient.invalidateQueries({ queryKey: ['agent'] })
    }
  })
}

/**
 * Stop agent mutation
 */
export function useStopAgent() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => agentsApi.stopAgent(id),
    onSuccess: () => {
      // Invalidate agents list and specific agent query
      queryClient.invalidateQueries({ queryKey: ['agents'] })
      queryClient.invalidateQueries({ queryKey: ['agent'] })
    }
  })
}
