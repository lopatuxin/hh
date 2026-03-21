export const WorkFormat = {
  OFFICE: 'OFFICE',
  REMOTE: 'REMOTE',
  HYBRID: 'HYBRID',
} as const
export type WorkFormat = (typeof WorkFormat)[keyof typeof WorkFormat]

export const Currency = {
  RUR: 'RUR',
  USD: 'USD',
  EUR: 'EUR',
} as const
export type Currency = (typeof Currency)[keyof typeof Currency]

export const ApplyStatus = {
  APPLIED: 'APPLIED',
  FILTERED: 'FILTERED',
  ACTION_REQUIRED: 'ACTION_REQUIRED',
} as const
export type ApplyStatus = (typeof ApplyStatus)[keyof typeof ApplyStatus]

export interface Salary {
  from: number | null
  to: number | null
  currency: Currency | null
}

export interface ApplyCriteria {
  areaId: number
  salaryFrom: number
  currency: Currency
  experience: string
  keywords: string[]
  excludedCompanies: string[]
  workFormats: WorkFormat[]
  requiredTitleGroups: string[][]
  excludedTitleWords: string[]
  limit: number
}

export interface ApplyResult {
  found: number
  skipped: number
  applied: number
  failed: number
  durationMs: number
}

export interface ProcessedVacancy {
  vacancyId: string
  title: string
  company: string
  salary: Salary | null
  status: ApplyStatus
  filterReason: string | null
}

export interface ApplyStatusResponse {
  running: boolean
  found: number
  filtered: number
  applied: number
  failed: number
  processedVacancies: ProcessedVacancy[]
}

export interface DailyStats {
  date: string
  applied: number
  filtered: number
}

export interface StatsResponse {
  today: number
  week: number
  month: number
  total: number
  dailyStats: DailyStats[]
}

export interface RunEntry {
  id: number
  startedAt: string
  finishedAt: string | null
  found: number
  filtered: number
  applied: number
  failed: number
  durationMs: number
}

export interface HistoryEntry {
  vacancyId: string
  title: string | null
  company: string
  status: ApplyStatus
  appliedAt: string
  url: string
}

export interface HistoryPageResponse {
  content: HistoryEntry[]
  totalPages: number
  totalElements: number
  page: number
  size: number
}

export interface SettingsDto {
  resumeId: string
  delayMinMs: number
  delayMaxMs: number
  maxPerDay: number
  headless: boolean
}

export interface AuthStatusResponse {
  exists: boolean
  lastModified: string | null
}
