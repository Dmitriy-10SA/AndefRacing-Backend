import { useState } from 'react'
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../stores/authStore'
import { useQuery } from '@tanstack/react-query'
import { profileApi } from '@/api/profileApi'
import ConfirmModal from './ConfirmModal'
import { EmployeeRole } from '@/types'

const Layout = () => {
  const { isAuthenticated, logout, currentClub } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [showLogoutModal, setShowLogoutModal] = useState(false)

  const { data: personalInfo } = useQuery({
    queryKey: ['profile'],
    queryFn: profileApi.getPersonalInfo,
    enabled: isAuthenticated,
  })

  const isActive = (path: string) => {
    return location.pathname.startsWith(path)
  }

  const getLinkClassName = (path: string) => {
    const baseClass = "transition-colors border-b-2"
    if (isActive(path)) {
      return `${baseClass} border-white font-semibold`
    }
    return `${baseClass} border-transparent hover:border-primary-200 hover:text-primary-200`
  }

  const handleLogoutClick = () => {
    setShowLogoutModal(true)
  }

  const handleLogoutConfirm = () => {
    logout()
    setShowLogoutModal(false)
    navigate('/auth/login')
  }

  const handleLogoutCancel = () => {
    setShowLogoutModal(false)
  }

  const isManager = personalInfo?.roles.includes(EmployeeRole.MANAGER)
  const isAdmin = personalInfo?.roles.includes(EmployeeRole.ADMINISTRATOR)
  const hasAccessToBookings = isManager || isAdmin

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-primary-600 text-white shadow-lg">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <Link to={hasAccessToBookings ? "/bookings" : "/profile"} className="text-2xl font-bold flex items-center gap-3">
              <img src="/race-flag.svg" alt="AndefRacing" className="w-8 h-8" />
              AndefRacing
              {currentClub && (
                <span className="text-sm font-normal">• {currentClub.name}</span>
              )}
            </Link>
            <nav className="flex items-center gap-6">
              {isAuthenticated && (
                <>
                  {hasAccessToBookings && (
                    <Link to="/bookings" className={getLinkClassName('/bookings')}>
                      Бронирования
                    </Link>
                  )}
                  {isManager && (
                    <>
                      <Link to="/management/hr" className={getLinkClassName('/management/hr')}>
                        Персонал
                      </Link>
                      <Link to="/management/club" className={getLinkClassName('/management/club')}>
                        Клуб
                      </Link>
                      <Link to="/management/reports" className={getLinkClassName('/management/reports')}>
                        Отчеты
                      </Link>
                    </>
                  )}
                  <Link to="/profile" className={getLinkClassName('/profile')}>
                    Профиль
                  </Link>
                  <button
                    onClick={handleLogoutClick}
                    className="hover:text-primary-200 transition-colors"
                  >
                    Выход
                  </button>
                </>
              )}
              {!isAuthenticated && (
                <Link to="/auth/login" className={getLinkClassName('/auth/login')}>
                  Вход
                </Link>
              )}
            </nav>
          </div>
        </div>
      </header>

      <main className="flex-1 container mx-auto px-4 py-8">
        <Outlet />
      </main>

      <ConfirmModal
        isOpen={showLogoutModal}
        title="Подтверждение выхода"
        message="Вы уверены, что хотите выйти из аккаунта?"
        confirmText="Выйти"
        cancelText="Отмена"
        onConfirm={handleLogoutConfirm}
        onCancel={handleLogoutCancel}
      />
    </div>
  )
}

export default Layout
