import { useState } from 'react'
import { useStats, useRuns } from '@/api/hooks'
import { MetricCard } from '@/components/MetricCard'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts'

type Period = 7 | 30 | 365

export function Dashboard() {
  const [period, setPeriod] = useState<Period>(30)
  const { data: stats } = useStats(period)
  const { data: runs } = useRuns(10)

  return (
    <div>
      <h2 className="text-2xl font-bold text-text-primary mb-6">Дашборд</h2>

      <div className="grid grid-cols-4 gap-4 mb-8">
        <MetricCard title="Откликов сегодня" value={stats?.today ?? 0} />
        <MetricCard title="За неделю" value={stats?.week ?? 0} />
        <MetricCard title="За месяц" value={stats?.month ?? 0} />
        <MetricCard title="Всего" value={stats?.total ?? 0} />
      </div>

      <div className="bg-bg-card border border-border-card rounded-xl p-6 mb-8">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold text-text-primary">Отклики по дням</h3>
          <div className="flex gap-2">
            {([7, 30, 365] as Period[]).map((p) => (
              <button
                key={p}
                onClick={() => setPeriod(p)}
                className={`px-3 py-1 rounded text-sm transition-colors ${
                  period === p
                    ? 'bg-accent text-white'
                    : 'bg-white/5 text-text-secondary hover:text-text-primary'
                }`}
              >
                {p === 365 ? 'Всё' : `${p}д`}
              </button>
            ))}
          </div>
        </div>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={stats?.dailyStats ?? []}>
            <CartesianGrid strokeDasharray="3 3" stroke="#2d3148" />
            <XAxis
              dataKey="date"
              stroke="#9ca3af"
              tick={{ fontSize: 12 }}
              tickFormatter={(v: string) => v.slice(5)}
            />
            <YAxis stroke="#9ca3af" tick={{ fontSize: 12 }} />
            <Tooltip
              contentStyle={{
                background: '#1a1d2e',
                border: '1px solid #2d3148',
                borderRadius: 8,
                color: '#e5e7eb',
              }}
            />
            <Legend />
            <Line
              type="monotone"
              dataKey="applied"
              name="Откликнулся"
              stroke="#6366f1"
              strokeWidth={2}
              dot={false}
            />
            <Line
              type="monotone"
              dataKey="filtered"
              name="Отфильтрован"
              stroke="#9ca3af"
              strokeWidth={2}
              dot={false}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>

      <div className="bg-bg-card border border-border-card rounded-xl p-6">
        <h3 className="text-lg font-semibold text-text-primary mb-4">Последние запуски</h3>
        <table className="w-full text-sm">
          <thead>
            <tr className="text-text-secondary border-b border-border-card">
              <th className="text-left py-2 font-medium">Дата</th>
              <th className="text-right py-2 font-medium">Найдено</th>
              <th className="text-right py-2 font-medium">Отфильтровано</th>
              <th className="text-right py-2 font-medium">Откликнулся</th>
              <th className="text-right py-2 font-medium">Ошибок</th>
              <th className="text-right py-2 font-medium">Длительность</th>
            </tr>
          </thead>
          <tbody>
            {runs?.map((run) => (
              <tr key={run.id} className="border-b border-border-card/50 hover:bg-white/5">
                <td className="py-2 text-text-primary">
                  {new Date(run.startedAt).toLocaleString('ru-RU')}
                </td>
                <td className="py-2 text-right text-text-primary">{run.found}</td>
                <td className="py-2 text-right text-text-secondary">{run.filtered}</td>
                <td className="py-2 text-right text-success">{run.applied}</td>
                <td className="py-2 text-right text-error">{run.failed}</td>
                <td className="py-2 text-right text-text-secondary">
                  {Math.round(run.durationMs / 1000)}с
                </td>
              </tr>
            ))}
            {(!runs || runs.length === 0) && (
              <tr>
                <td colSpan={6} className="py-8 text-center text-text-secondary">
                  Запусков пока нет
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
