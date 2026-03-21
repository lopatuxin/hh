import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiGet, apiPost, apiPut } from './client'
import type {
  ApplyCriteria,
  ApplyResult,
  ApplyStatusResponse,
  StatsResponse,
  RunEntry,
  HistoryPageResponse,
  SettingsDto,
  AuthStatusResponse,
} from '@/types'

export function useApplyStatus(enabled: boolean) {
  return useQuery({
    queryKey: ['apply-status'],
    queryFn: () => apiGet<ApplyStatusResponse>('/apply/status'),
    refetchInterval: enabled ? 1000 : false,
  })
}

export function useRunApply() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (criteria: ApplyCriteria) => apiPost<ApplyResult>('/apply/run', criteria),
    onSettled: () => {
      void qc.invalidateQueries({ queryKey: ['apply-status'] })
      void qc.invalidateQueries({ queryKey: ['runs'] })
      void qc.invalidateQueries({ queryKey: ['stats'] })
    },
  })
}

export function useStopApply() {
  return useMutation({
    mutationFn: () => apiPost<void>('/apply/stop'),
  })
}

export function useStats(days = 30) {
  return useQuery({
    queryKey: ['stats', days],
    queryFn: () => apiGet<StatsResponse>(`/stats?days=${days}`),
  })
}

export function useRuns(limit = 20) {
  return useQuery({
    queryKey: ['runs', limit],
    queryFn: () => apiGet<RunEntry[]>(`/runs?limit=${limit}`),
  })
}

export function useHistory(params: {
  status?: string
  dateFrom?: string
  dateTo?: string
  company?: string
  page: number
  size: number
}) {
  const searchParams = new URLSearchParams()
  if (params.status) { searchParams.set('status', params.status) }
  if (params.dateFrom) { searchParams.set('dateFrom', params.dateFrom) }
  if (params.dateTo) { searchParams.set('dateTo', params.dateTo) }
  if (params.company) { searchParams.set('company', params.company) }
  searchParams.set('page', String(params.page))
  searchParams.set('size', String(params.size))

  return useQuery({
    queryKey: ['history', params],
    queryFn: () => apiGet<HistoryPageResponse>(`/history?${searchParams.toString()}`),
  })
}

export function useSettings() {
  return useQuery({
    queryKey: ['settings'],
    queryFn: () => apiGet<SettingsDto>('/settings'),
  })
}

export function useUpdateSettings() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (dto: SettingsDto) => apiPut<SettingsDto>('/settings', dto),
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: ['settings'] })
    },
  })
}

export function useAuthStatus() {
  return useQuery({
    queryKey: ['auth-status'],
    queryFn: () => apiGet<AuthStatusResponse>('/auth/status'),
  })
}

export function useStartAuth() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => apiPost<unknown>('/auth/start'),
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: ['auth-status'] })
    },
  })
}

export function useSaveAuth() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => apiPost<unknown>('/auth/save'),
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: ['auth-status'] })
    },
  })
}
