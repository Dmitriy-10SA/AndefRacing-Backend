import axios from 'axios'
import { useAuthStore } from '../stores/authStore'

const API_BASE_URL = '/api'

export const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
})

axiosInstance.interceptors.request.use(
    (config) => {
        const token = useAuthStore.getState().token
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        const isAuthEndpoint = error.config?.url?.includes('/auth/employee/')
        if (error.response?.status === 401 && !isAuthEndpoint) {
            useAuthStore.getState().logout()
            window.location.href = '/auth/login'
        }
        const message = error.response?.data?.message
        if (message === 'Вы заблокированы') {
            useAuthStore.getState().logout()
            // Show a simple notification before redirect
            const notification = document.createElement('div')
            notification.className = 'fixed top-4 right-4 z-50 bg-red-50 border border-red-200 text-red-800 px-6 py-4 rounded-lg shadow-lg'
            notification.textContent = 'Вы заблокированы'
            document.body.appendChild(notification)
            setTimeout(() => {
                window.location.href = '/auth/login'
            }, 2000)
        }
        return Promise.reject(error)
    }
)

export default axiosInstance
