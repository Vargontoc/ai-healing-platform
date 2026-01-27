import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { documentsApi } from '@/services/documentsApi'
import type { DocumentFilters } from '@/types/documents'
import type { MaybeRef } from 'vue'
import { unref } from 'vue'

export function useDocuments(filters: MaybeRef<DocumentFilters> = {}) {
  return useQuery({
    queryKey: ['documents', filters],
    queryFn: () => documentsApi.getDocuments(unref(filters)),
    staleTime: 30000 // 30 seconds
  })
}

export function useDocument(id: MaybeRef<string>) {
  return useQuery({
    queryKey: ['document', id],
    queryFn: () => documentsApi.getDocument(unref(id)),
    enabled: () => !!unref(id)
  })
}

export function useUploadDocument() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ file, serviceId, version }: { file: File; serviceId: string; version: string }) =>
      documentsApi.uploadDocument(file, serviceId, version),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['documents'] })
    }
  })
}
