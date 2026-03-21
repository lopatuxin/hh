interface MetricCardProps {
  title: string
  value: number | string
  subtitle?: string
}

export function MetricCard({ title, value, subtitle }: Readonly<MetricCardProps>) {
  return (
    <div className="bg-bg-card border border-border-card rounded-xl p-5">
      <div className="text-sm text-text-secondary mb-1">{title}</div>
      <div className="text-3xl font-bold text-text-primary">{value}</div>
      {subtitle && (
        <div className="text-xs text-text-secondary mt-1">{subtitle}</div>
      )}
    </div>
  )
}
