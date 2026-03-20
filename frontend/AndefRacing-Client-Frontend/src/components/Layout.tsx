import { useState } from 'react'
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../stores/authStore'
import ConfirmModal from './ConfirmModal'

const Layout = () => {
  const { isAuthenticated, logout } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [showLogoutModal, setShowLogoutModal] = useState(false)

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

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-primary-600 text-white shadow-lg">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <Link to="/search" className="text-2xl font-bold flex items-center gap-3">
              <img src="/race-flag.svg" alt="AndefRacing" className="w-8 h-8" />
              AndefRacing
            </Link>
            <nav className="flex items-center gap-6">
              <Link to="/search" className={getLinkClassName('/search')}>
                Поиск клубов
              </Link>
              {isAuthenticated && (
                <>
                  <Link to="/favorites" className={getLinkClassName('/favorites')}>
                    Избранное
                  </Link>
                  <Link to="/bookings" className={getLinkClassName('/bookings')}>
                    Мои бронирования
                  </Link>
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
                <>
                  <Link to="/auth/login" className={getLinkClassName('/auth/login')}>
                    Вход
                  </Link>
                  <Link to="/auth/register" className="btn bg-white text-primary-600 hover:bg-gray-100">
                    Регистрация
                  </Link>
                </>
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
