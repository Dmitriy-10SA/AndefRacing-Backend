import { useEffect } from 'react'
import { CheckCircle, XCircle, AlertCircle, X } from 'lucide-react'

export type ToastType = 'success' | 'error' | 'info'

interface ToastProps {
  message: string
  type: ToastType
  onClose: () => void
  duration?: number
}

const Toast = ({ message, type, onClose, duration = 5000 }: ToastProps) => {
  useEffect(() => {
    if (duration > 0) {
      const timer = setTimeout(() => {
        onClose()
      }, duration)
      return () => clearTimeout(timer)
    }
  }, [duration, onClose])

  const getIcon = () => {
    switch (type) {
      case 'success':
        return <CheckCircle className="w-5 h-5 text-green-500" />
      case 'error':
        return <XCircle className="w-5 h-5 text-red-500" />
      case 'info':
        return <AlertCircle className="w-5 h-5 text-blue-500" />
    }
  }

  const getBackgroundColor = () => {
    switch (type) {
      case 'success':
        return 'bg-green-50 border-green-200'
      case 'error':
        return 'bg-red-50 border-red-200'
      case 'info':
        return 'bg-blue-50 border-blue-200'
    }
  }

  const getTextColor = () => {
    switch (type) {
      case 'success':
        return 'text-green-800'
      case 'error':
        return 'text-red-800'
      case 'info':
        return 'text-blue-800'
    }
  }

  return (
    <div
      className={`fixed top-4 right-4 z-50 max-w-md w-full shadow-lg rounded-lg border ${getBackgroundColor()} p-4 flex items-start gap-3 animate-slide-in`}
    >
      <div className="flex-shrink-0">{getIcon()}</div>
      <p className={`flex-1 ${getTextColor()} font-medium`}>{message}</p>
      <button
        onClick={onClose}
        className={`flex-shrink-0 ${getTextColor()} hover:opacity-70 transition-opacity`}
      >
        <X className="w-5 h-5" />
      </button>
    </div>
  )
}

export default Toast
