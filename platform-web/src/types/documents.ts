export type DocumentType = 'PDF' | 'MARKDOWN' | 'TXT' | 'DOCX' | 'HTML'
export type DocumentStatus = 'ANALYZED' | 'PENDING' | 'ERROR'

export interface Document {
  id: string
  name: string
  type: DocumentType | string
  serviceId: string
  version: string
  status: DocumentStatus | string
  size: number
  uploadedAt: string
  analyzedAt: string | null
}

export interface DocumentPageResponse {
  content: Document[]
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

export interface DocumentFilters {
  type?: string
  serviceId?: string
  status?: string
  search?: string
  page?: number
  size?: number
}
