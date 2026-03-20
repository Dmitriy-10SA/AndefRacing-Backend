import { useState, useCallback } from 'react'
import Toast, { ToastType } from '@/components/Toast'

interface ToastState {
  message: string
  type: ToastType
  id: number
}

export const useToast = () => {
  const [toasts, setToasts] = useState<ToastState[]>([])

  const showToast = useCallback((message: string, type: ToastType = 'info') => {
    const id = Date.now()
    // Заменяем все предыдущие уведомления на новое
    setToasts([{ message, type, id }])
  }, [])

  const hideToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id))
  }, [])

  const ToastContainer = useCallback(
    () => (
      <>
        {toasts.map((toast) => (
          <Toast
            key={toast.id}
            message={toast.message}
            type={toast.type}
            onClose={() => hideToast(toast.id)}
          />
        ))}
      </>
    ),
    [toasts, hideToast]
  )

  return {
    showToast,
    ToastContainer,
    success: (message: string) => showToast(message, 'success'),
    error: (message: string) => showToast(message, 'error'),
    info: (message: string) => showToast(message, 'info'),
  }
}
