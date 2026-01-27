export interface Incident {
  id: number
  serviceName: string
  errorType: string
  description: string
  status: 'OPEN' | 'CLOSED' | 'RESOLVED' | 'REOPENED'
  detectedAt: string
  lastSeen: string
  occurrences: number
  assignedTo?: string
  updatedBy?: string
  updatedAt?: string
}

export interface IncidentPageResponse {
  content: Incident[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      sorted: boolean
      empty: boolean
      unsorted: boolean
    }
    offset: number
    paged: boolean
    unpaged: boolean
  }
  totalPages: number
  totalElements: number
  last: boolean
  first: boolean
  size: number
  number: number
  sort: {
    sorted: boolean
    empty: boolean
    unsorted: boolean
  }
  numberOfElements: number
  empty: boolean
}

export interface IncidentFilters {
  status?: 'OPEN' | 'CLOSED' | 'RESOLVED' | 'REOPENED'
  serviceName?: string
  page?: number
  size?: number
}

export interface IncidentStatusUpdate {
  status: 'OPEN' | 'CLOSED' | 'RESOLVED' | 'REOPENED'
  comment?: string
}

export interface IncidentAssignment {
  assignedTo: string
}
