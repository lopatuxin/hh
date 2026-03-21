import { useState } from 'react'
import { useHistory } from '@/api/hooks'
import { StatusBadge } from '@/components/StatusBadge'
import { ChevronLeft, ChevronRight } from 'lucide-react'

export function History() {
  const [status, setStatus] = useState('')
  const [company, setCompany] = useState('')
  const [page, setPage] = useState(0)
  const size = 20

  const { data } = useHistory({ status: status || undefined, company: company || undefined, page, size })

  return (
    <div>
      <h2 className="text-2xl font-bold text-text-primary mb-6">История откликов</h2>

      {/* Фильтры */}
      <div className="flex gap-4 mb-6">
        <select
          value={status}
          onChange={(e) => { setStatus(e.target.value); setPage(0) }}
          className="bg-bg-card border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
        >
          <option value="">Все статусы</option>
          <option value="APPLIED">Откликнулся</option>
          <option value="FILTERED">Отфильтрован</option>
          <option value="ACTION_REQUIRED">Ошибка</option>
        </select>
        <input
          type="text"
          value={company}
          onChange={(e) => { setCompany(e.target.value); setPage(0) }}
          placeholder="Поиск по компании..."
          className="bg-bg-card border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent flex-1 max-w-xs"
        />
      </div>

      {/* Таблица */}
      <div className="bg-bg-card border border-border-card rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-text-secondary border-b border-border-card">
              <th className="text-left px-4 py-3 font-medium">ID вакансии</th>
              <th className="text-left px-4 py-3 font-medium">Название</th>
              <th className="text-left px-4 py-3 font-medium">Компания</th>
              <th className="text-left px-4 py-3 font-medium">Статус</th>
              <th className="text-left px-4 py-3 font-medium">Дата</th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((entry) => (
              <tr key={entry.vacancyId} className="border-b border-border-card/50 hover:bg-white/5">
                <td className="px-4 py-3">
                  <a
                    href={entry.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-accent hover:text-accent-hover underline"
                  >
                    {entry.vacancyId}
                  </a>
                </td>
                <td className="px-4 py-3 text-text-primary max-w-xs truncate">
                  {entry.title ?? '—'}
                </td>
                <td className="px-4 py-3 text-text-primary">{entry.company}</td>
                <td className="px-4 py-3">
                  <StatusBadge status={entry.status} />
                </td>
                <td className="px-4 py-3 text-text-secondary">
                  {new Date(entry.appliedAt).toLocaleString('ru-RU')}
                </td>
              </tr>
            ))}
            {(!data || data.content.length === 0) && (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-text-secondary">
                  Нет записей
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* Пагинация */}
        {data && data.totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-border-card">
            <span className="text-xs text-text-secondary">
              Стр. {data.page + 1} из {data.totalPages} (всего {data.totalElements})
            </span>
            <div className="flex gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="p-1.5 rounded hover:bg-white/10 disabled:opacity-30 text-text-secondary"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={page >= data.totalPages - 1}
                className="p-1.5 rounded hover:bg-white/10 disabled:opacity-30 text-text-secondary"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
