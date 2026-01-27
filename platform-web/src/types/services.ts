export interface ServiceInfo {
  name: string
  status: 'UP' | 'DOWN'
  instances: number
  urls: string[]
}

export interface ServiceListResponse {
  services: ServiceInfo[]
}
