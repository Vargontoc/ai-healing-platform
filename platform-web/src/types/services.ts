export interface ServiceInfo {
  name: string
  status: 'UP' | 'DOWN'
  instances: number
  urls: string[]
}

export interface ServiceListResponse {
  services: ServiceInfo[]
}

export interface ServiceHealth {
  status: 'UP' | 'DOWN' | 'UNKNOWN'
  details?: Record<string, any>
}

export interface ServiceMetrics {
  uptime: number // milliseconds
  totalRequests: number
  failedRequests: number
}

export interface ServiceDetail extends ServiceInfo {
  health: ServiceHealth
  metrics: ServiceMetrics
}
