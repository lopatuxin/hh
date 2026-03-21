import { ApplyStatus } from '@/types'

const config: Record<ApplyStatus, { label: string; className: string }> = {
  [ApplyStatus.APPLIED]: {
    label: 'Откликнулся',
    className: 'bg-success/15 text-success',
  },
  [ApplyStatus.FILTERED]: {
    label: 'Отфильтрован',
    className: 'bg-white/10 text-text-secondary',
  },
  [ApplyStatus.ACTION_REQUIRED]: {
    label: 'Ошибка',
    className: 'bg-error/15 text-error',
  },
}

export function StatusBadge({ status }: Readonly<{ status: ApplyStatus }>) {
  const { label, className } = config[status]
  return (
    <span className={`px-2 py-0.5 rounded text-xs font-medium ${className}`}>
      {label}
    </span>
  )
}
