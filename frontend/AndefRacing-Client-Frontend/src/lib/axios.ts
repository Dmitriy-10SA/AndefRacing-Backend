import axios from 'axios'
import {useAuthStore} from '../stores/authStore'

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
        const isAuthEndpoint = error.config?.url?.includes('/auth/client/')
        if (error.response?.status === 401 && !isAuthEndpoint) {
            useAuthStore.getState().logout()
            window.location.href = '/auth/login'
        }
        const message = error.response?.data?.message
        if (message === 'Вы заблокированы' || message === 'Клиент заблокирован') {
            useAuthStore.getState().logout()
            alert('Вы заблокированы')
            window.location.href = '/auth/login'
        }
        return Promise.reject(error)
    }
)

export default axiosInstance
