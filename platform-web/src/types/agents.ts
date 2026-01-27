export type AgentStatus = 'RUNNING' | 'STOPPED' | 'ERROR'
export type AgentType = 'INCIDENT_ANALYZER' | 'AUTO_FIX'
export type LogLevel = 'INFO' | 'WARN' | 'ERROR'

export interface AgentConfiguration {
  autoRestart: boolean
  maxConcurrentTasks: number
  timeout: number
}

export interface AgentMetrics {
  totalExecutions: number
  successRate: number
  averageExecutionTime: number
  lastError: string | null
}

export interface AgentInstance {
  id: string
  name: string
  status: AgentStatus
  type: AgentType
  createdAt: string
  lastExecution: string | null
  configuration: AgentConfiguration
  metrics: AgentMetrics
}

export interface AgentLog {
  id: number
  agentId: string
  timestamp: string
  level: LogLevel
  message: string
  stackTrace: string | null
  metadata: string | null // JSON string
}

export interface AgentListResponse {
  content: AgentInstance[]
}

export interface AgentLogsResponse {
  logs: AgentLog[]
  total: number
  hasMore: boolean
  page: number
  size: number
}

export interface AgentActionResponse {
  id: string
  message: string
  status: AgentStatus
}
