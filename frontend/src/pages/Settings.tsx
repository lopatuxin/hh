import { useState } from 'react'
import { useSettings, useUpdateSettings, useAuthStatus, useStartAuth } from '@/api/hooks'
import type { SettingsDto } from '@/types'
import { Monitor, Save } from 'lucide-react'
import AuthModal from '../components/AuthModal'

export function Settings() {
  const { data: settings } = useSettings()
  const { data: authStatus } = useAuthStatus()
  const updateMutation = useUpdateSettings()
  const startAuth = useStartAuth()
  const [showAuthModal, setShowAuthModal] = useState(false)

  const defaultForm: SettingsDto = {
    resumeId: '',
    delayMinMs: 10000,
    delayMaxMs: 45000,
    maxPerDay: 50,
  }
  const [form, setForm] = useState<SettingsDto>(settings ?? defaultForm)

  const [prevSettings, setPrevSettings] = useState(settings)
  if (settings !== prevSettings) {
    setPrevSettings(settings)
    if (settings) {
      setForm(settings)
    }
  }

  function handleSave() {
    updateMutation.mutate(form)
  }

  return (
    <div>
      <h2 className="text-2xl font-bold text-text-primary mb-6">Настройки</h2>

      {/* Авторизация */}
      <div className="bg-bg-card border border-border-card rounded-xl p-6 mb-6">
        <h3 className="text-lg font-semibold text-text-primary mb-4">Авторизация</h3>
        <div className="flex items-center gap-3 mb-4">
          <div
            className={`w-3 h-3 rounded-full ${authStatus?.exists ? 'bg-success' : 'bg-error'}`}
          />
          <span className="text-sm text-text-primary">
            {authStatus?.exists ? 'Сессия сохранена' : 'Сессия не найдена'}
          </span>
          {authStatus?.lastModified && (
            <span className="text-xs text-text-secondary">
              (обновлено {new Date(authStatus.lastModified).toLocaleString('ru-RU')})
            </span>
          )}
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => startAuth.mutate(undefined, { onSuccess: () => setShowAuthModal(true) })}
            disabled={startAuth.isPending}
            className="flex items-center gap-2 px-4 py-2 bg-accent hover:bg-accent-hover text-white rounded-lg text-sm transition-colors disabled:opacity-50"
          >
            <Monitor className="w-4 h-4" />
            Авторизоваться
          </button>
        </div>
      </div>

      {/* Параметры */}
      <div className="bg-bg-card border border-border-card rounded-xl p-6">
        <h3 className="text-lg font-semibold text-text-primary mb-4">Параметры</h3>
        <div className="space-y-4 max-w-lg">
          <div>
            <label htmlFor="resumeId" className="text-sm text-text-secondary mb-1 block">Resume ID</label>
            <input
              id="resumeId"
              type="text"
              value={form.resumeId}
              onChange={(e) => setForm((f) => ({ ...f, resumeId: e.target.value }))}
              className="w-full bg-bg-content border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="delayMinMs" className="text-sm text-text-secondary mb-1 block">Мин. задержка, мс</label>
              <input
                id="delayMinMs"
                type="number"
                value={form.delayMinMs}
                onChange={(e) => setForm((f) => ({ ...f, delayMinMs: Number(e.target.value) }))}
                className="w-full bg-bg-content border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
              />
            </div>
            <div>
              <label htmlFor="delayMaxMs" className="text-sm text-text-secondary mb-1 block">Макс. задержка, мс</label>
              <input
                id="delayMaxMs"
                type="number"
                value={form.delayMaxMs}
                onChange={(e) => setForm((f) => ({ ...f, delayMaxMs: Number(e.target.value) }))}
                className="w-full bg-bg-content border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
              />
            </div>
          </div>
          <div>
            <label htmlFor="maxPerDay" className="text-sm text-text-secondary mb-1 block">Лимит откликов в день</label>
            <input
              id="maxPerDay"
              type="number"
              value={form.maxPerDay}
              onChange={(e) => setForm((f) => ({ ...f, maxPerDay: Number(e.target.value) }))}
              className="w-full bg-bg-content border border-border-card rounded-lg px-3 py-2 text-sm text-text-primary outline-none focus:border-accent"
            />
          </div>
          <button
            onClick={handleSave}
            disabled={updateMutation.isPending}
            className="flex items-center gap-2 px-6 py-2.5 bg-accent hover:bg-accent-hover text-white rounded-lg text-sm font-medium transition-colors disabled:opacity-50"
          >
            <Save className="w-4 h-4" />
            Сохранить
          </button>
          {updateMutation.isSuccess && (
            <div className="text-sm text-success">Настройки сохранены</div>
          )}
        </div>
      </div>

      {showAuthModal && <AuthModal onClose={() => setShowAuthModal(false)} />}
    </div>
  )
}
