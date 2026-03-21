import { NavLink } from 'react-router-dom'
import { BarChart3, Rocket, List, Settings } from 'lucide-react'
import { useAuthStatus } from '@/api/hooks'

const navItems = [
  { to: '/', label: 'Дашборд', icon: BarChart3 },
  { to: '/launch', label: 'Запуск', icon: Rocket },
  { to: '/history', label: 'История', icon: List },
  { to: '/settings', label: 'Настройки', icon: Settings },
]

export function Sidebar() {
  const { data: authStatus } = useAuthStatus()

  return (
    <aside className="w-60 bg-bg-sidebar h-screen fixed left-0 top-0 flex flex-col border-r border-border-card">
      <div className="p-6">
        <h1 className="text-xl font-bold text-text-primary flex items-center gap-2">
          <Rocket className="w-6 h-6 text-accent" />
          HH Auto
        </h1>
      </div>

      <nav className="flex-1 px-3">
        {navItems.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg mb-1 text-sm transition-colors ${
                isActive
                  ? 'bg-accent/15 text-accent'
                  : 'text-text-secondary hover:text-text-primary hover:bg-white/5'
              }`
            }
          >
            <Icon className="w-5 h-5" />
            {label}
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-border-card">
        <div className="flex items-center gap-2 text-xs text-text-secondary">
          <div
            className={`w-2 h-2 rounded-full ${
              authStatus?.exists ? 'bg-success' : 'bg-error'
            }`}
          />
          {authStatus?.exists ? 'Авторизован' : 'Не авторизован'}
        </div>
        <div className="text-xs text-text-secondary mt-1">v0.1.0</div>
      </div>
    </aside>
  )
}
