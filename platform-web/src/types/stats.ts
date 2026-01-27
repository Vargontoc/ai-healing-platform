export interface IncidentStats {
  totalIncidents: number
  byStatus: Record<string, number>
  byService: Record<string, number>
}

export interface ServiceStats {
  totalServices: number
  upServices: number
  downServices: number
  totalInstances: number
}

export interface DashboardStats {
  incidents: IncidentStats
  services: ServiceStats
}
