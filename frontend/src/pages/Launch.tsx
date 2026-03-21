import { useRef, useState } from 'react'
import { useApplyStatus, useRunApply, useStopApply } from '@/api/hooks'
import { TagInput } from '@/components/TagInput'
import { StatusBadge } from '@/components/StatusBadge'
import type { ApplyCriteria } from '@/types'
import { WorkFormat, Currency } from '@/types'
import { Play, Square, Plus, X } from 'lucide-react'

export function Launch() {
  const [keywords, setKeywords] = useState<string[]>([])
  const [areaId, setAreaId] = useState(0)
  const [salaryFrom, setSalaryFrom] = useState(0)
  const [currency, setCurrency] = useState<Currency>(Currency.RUR)
  const [experience, setExperience] = useState('')
  const [workFormats, setWorkFormats] = useState<WorkFormat[]>([])
  const [excludedCompanies, setExcludedCompanies] = useState<string[]>([])
  const nextGroupId = useRef(0)
  const [requiredTitleGroups, setRequiredTitleGroups] = useState<{ id: number; tags: string[] }[]>([])
  const [excludedTitleWords, setExcludedTitleWords] = useState<string[]>([])
  const [limit, setLimit] = useState(30)

  const { data: status } = useApplyStatus(true)
  const runMutation = useRunApply()
  const stopMutation = useStopApply()
  const isRunning = status?.running ?? false

  function handleRun() {
    const criteria: ApplyCriteria = {
      areaId,
      salaryFrom,
      currency,
      experience,
      keywords,
      excludedCompanies,
      workFormats,
      requiredTitleGroups: requiredTitleGroups.map((g) => g.tags),
      excludedTitleWords,
      limit,
    }
    runMutation.mutate(criteria)
  }

  function handleStop() {
    stopMutation.mutate()
  }

  function toggleWorkFormat(wf: WorkFormat) {
    setWorkFormats((prev) =>
      prev.includes(wf) ? prev.filter((f) => f !== wf) : [...prev, wf]
    )
  }

  function addTitleGroup() {
    setRequiredTitleGroups((prev) => [...prev, { id: nextGroupId.current++, tags: [] }])
  }

  function updateTitleGroup(id: number, tags: string[]) {
    setRequiredTitleGroups((prev) => prev.map((g) => (g.id === id ? { ...g, tags } : g)))
  }

  function removeTitleGroup(id: number) {
    setRequiredTitleGroups((prev) => prev.filter((g) => g.id !== id))
  }

  const workFormatLabels: Record<WorkFormat, string> = {
    [WorkFormat.OFFICE]: 'Офис',
    [WorkFormat.REMOTE]: 'Удалённо',
    [WorkFormat.HYBRID]: 'Гибрид',
  }

  return (
    <div>
      <h2 className="text-2xl font-bold text-text-primary mb-6">Запуск откликов</h2>

      <div className="flex gap-6">
        {/* Форма критериев */}
        <div className="flex-3 space-y-4">
          <div className="bg-bg-card border border-border-card rounded-xl p-6 space-y-4">
            <div>
              <span className="text-sm text-text-secondary mb-1 block">Ключевые слова</span>
              <TagInput tags={keywords} onChange={setKeywords} placeholder="Введите слово и нажмите Enter" />
            </div>

            <div className="grid grid-cols-3 gap-4">
              <div>
                <label htmlFor="areaId" className="text-sm text-text-secondary mb-1 block">Регион (areaId)</label>
                <input
                  id="areaId"
                  type="number"
                  value={areaId}
                  onChange={(e) => setAreaId(Number(e.target.value))}
                  className="w-full bg-bg-card border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
                />
              </div>
              <div>
                <label htmlFor="salaryFrom" className="text-sm text-text-secondary mb-1 block">Зарплата от</label>
                <input
                  id="salaryFrom"
                  type="number"
                  value={salaryFrom || ''}
                  onChange={(e) => setSalaryFrom(Number(e.target.value))}
                  className="w-full bg-bg-card border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
                />
              </div>
              <div>
                <label htmlFor="currency" className="text-sm text-text-secondary mb-1 block">Валюта</label>
                <select
                  id="currency"
                  value={currency}
                  onChange={(e) => setCurrency(e.target.value as Currency)}
                  className="w-full bg-bg-card border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
                >
                  <option value="RUR">RUR</option>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="experience" className="text-sm text-text-secondary mb-1 block">Опыт</label>
                <select
                  id="experience"
                  value={experience}
                  onChange={(e) => setExperience(e.target.value)}
                  className="w-full bg-bg-card border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
                >
                  <option value="">Не указан</option>
                  <option value="noExperience">Нет опыта</option>
                  <option value="between1And3">1-3 года</option>
                  <option value="between3And6">3-6 лет</option>
                  <option value="moreThan6">Более 6 лет</option>
                </select>
              </div>
              <div>
                <label htmlFor="limit" className="text-sm text-text-secondary mb-1 block">Лимит откликов</label>
                <input
                  id="limit"
                  type="number"
                  value={limit}
                  onChange={(e) => setLimit(Number(e.target.value))}
                  className="w-full bg-bg-card border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
                />
              </div>
            </div>

            <div>
              <span className="text-sm text-text-secondary mb-1 block">Формат работы</span>
              <div className="flex gap-3">
                {Object.values(WorkFormat).map((wf) => (
                  <label key={wf} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={workFormats.includes(wf)}
                      onChange={() => toggleWorkFormat(wf)}
                      className="accent-accent"
                    />
                    <span className="text-sm text-text-primary">{workFormatLabels[wf]}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <span className="text-sm text-text-secondary mb-1 block">Исключённые компании</span>
              <TagInput tags={excludedCompanies} onChange={setExcludedCompanies} placeholder="Компания + Enter" />
            </div>

            <div>
              <div className="flex items-center justify-between mb-1">
                <span className="text-sm text-text-secondary">Обязательные слова в названии (группы)</span>
                <button
                  type="button"
                  onClick={addTitleGroup}
                  className="flex items-center gap-1 text-xs text-accent hover:text-accent-hover"
                >
                  <Plus className="w-3 h-3" /> Добавить группу
                </button>
              </div>
              {requiredTitleGroups.map((group) => (
                <div key={group.id} className="flex items-start gap-2 mb-2">
                  <div className="flex-1">
                    <TagInput
                      tags={group.tags}
                      onChange={(tags) => updateTitleGroup(group.id, tags)}
                      placeholder="Слова группы"
                    />
                  </div>
                  <button
                    type="button"
                    onClick={() => removeTitleGroup(group.id)}
                    className="mt-2 text-error hover:text-red-300"
                  >
                    <X className="w-4 h-4" />
                  </button>
                </div>
              ))}
            </div>

            <div>
              <span className="text-sm text-text-secondary mb-1 block">Исключённые слова в названии</span>
              <TagInput tags={excludedTitleWords} onChange={setExcludedTitleWords} placeholder="Слово + Enter" />
            </div>

            <div className="pt-2">
              {isRunning ? (
                <button
                  onClick={handleStop}
                  className="flex items-center gap-2 px-6 py-3 bg-error hover:bg-red-600 text-white rounded-lg font-medium transition-colors"
                >
                  <Square className="w-4 h-4" />
                  Остановить
                </button>
              ) : (
                <button
                  onClick={handleRun}
                  disabled={runMutation.isPending}
                  className="flex items-center gap-2 px-6 py-3 bg-accent hover:bg-accent-hover text-white rounded-lg font-medium transition-colors disabled:opacity-50"
                >
                  <Play className="w-4 h-4" />
                  Запустить
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Мониторинг */}
        <div className="flex-2">
          <div className="bg-bg-card border border-border-card rounded-xl p-6">
            <h3 className="text-lg font-semibold text-text-primary mb-4">Мониторинг</h3>

            {status && (
              <>
                <div className="grid grid-cols-2 gap-3 mb-4">
                  <div className="text-center p-3 rounded-lg bg-white/5">
                    <div className="text-2xl font-bold text-text-primary">{status.found}</div>
                    <div className="text-xs text-text-secondary">Найдено</div>
                  </div>
                  <div className="text-center p-3 rounded-lg bg-white/5">
                    <div className="text-2xl font-bold text-text-secondary">{status.filtered}</div>
                    <div className="text-xs text-text-secondary">Отфильтровано</div>
                  </div>
                  <div className="text-center p-3 rounded-lg bg-white/5">
                    <div className="text-2xl font-bold text-success">{status.applied}</div>
                    <div className="text-xs text-text-secondary">Откликнулся</div>
                  </div>
                  <div className="text-center p-3 rounded-lg bg-white/5">
                    <div className="text-2xl font-bold text-error">{status.failed}</div>
                    <div className="text-xs text-text-secondary">Ошибок</div>
                  </div>
                </div>

                {isRunning && (
                  <div className="w-full bg-white/10 rounded-full h-2 mb-4">
                    <div
                      className="bg-accent h-2 rounded-full transition-all"
                      style={{ width: '100%' }}
                    />
                  </div>
                )}

                <div className="space-y-2 max-h-125 overflow-y-auto">
                  {status.processedVacancies.map((v) => (
                    <div
                      key={v.vacancyId}
                      className="p-3 rounded-lg bg-white/5 border border-border-card/50"
                    >
                      <div className="flex items-start justify-between gap-2">
                        <div className="flex-1 min-w-0">
                          <div className="text-sm text-text-primary truncate">{v.title}</div>
                          <div className="text-xs text-text-secondary">{v.company}</div>
                          {v.salary && (
                            <div className="text-xs text-text-secondary">
                              {v.salary.from && `от ${v.salary.from}`}
                              {v.salary.to && ` до ${v.salary.to}`}
                              {v.salary.currency && ` ${v.salary.currency}`}
                            </div>
                          )}
                        </div>
                        <StatusBadge status={v.status} />
                      </div>
                      {v.filterReason && (
                        <div className="text-xs text-text-secondary mt-1 italic">{v.filterReason}</div>
                      )}
                    </div>
                  ))}
                  {status.processedVacancies.length === 0 && !isRunning && (
                    <div className="text-sm text-text-secondary text-center py-8">
                      Запустите процесс для отображения результатов
                    </div>
                  )}
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
