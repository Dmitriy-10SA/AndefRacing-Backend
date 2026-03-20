import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { useMutation } from '@tanstack/react-query'
import { authApi } from '../../api/authApi'
import { useAuthStore } from '../../stores/authStore'
import { ClientLoginDto } from '../../types'
import ErrorMessage from '../../components/ErrorMessage'
import PhoneInput from '../../components/PhoneInput'
import PasswordInput from '../../components/PasswordInput'

const LoginPage = () => {
  const navigate = useNavigate()
  const setToken = useAuthStore((state) => state.setToken)
  const [errorMessage, setErrorMessage] = useState('')

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<ClientLoginDto>({
    mode: 'onSubmit',
    shouldUnregister: false,
    defaultValues: {
      phone: '',
      password: '',
    },
  })

  const loginMutation = useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      setToken(data.jwt)
      navigate('/search')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Ошибка входа'
      setErrorMessage(message)
    },
  })

  const onSubmit = (data: ClientLoginDto) => {
    setErrorMessage('') // Очищаем предыдущую ошибку
    loginMutation.mutate(data)
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">AndefRacing</h1>
          <p className="mt-2 text-gray-600">Вход в систему</p>
        </div>

        <div className="card">
          {errorMessage && (
            <div className="mb-4">
              <ErrorMessage message={errorMessage} />
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
            <div>
              <label className="label">Номер телефона</label>
              <Controller
                name="phone"
                control={control}
                rules={{
                  required: 'Номер телефона обязателен',
                  pattern: {
                    value: /^\+7-\d{3}-\d{3}-\d{2}-\d{2}$/,
                    message: 'Формат: +7-XXX-XXX-XX-XX',
                  },
                }}
                render={({ field }) => (
                  <PhoneInput
                    value={field.value}
                    onChange={field.onChange}
                    onBlur={field.onBlur}
                  />
                )}
              />
              {errors.phone && (
                <p className="error-text">{errors.phone.message}</p>
              )}
            </div>

            <div>
              <label className="label">Пароль</label>
              <PasswordInput
                {...register('password', {
                  required: 'Пароль обязателен',
                })}
              />
              {errors.password && (
                <p className="error-text">{errors.password.message}</p>
              )}
            </div>

            <button
              type="submit"
              disabled={loginMutation.isPending}
              className="btn-primary w-full"
            >
              {loginMutation.isPending ? 'Вход...' : 'Войти'}
            </button>
          </form>

          <div className="mt-6 text-center space-y-2">
            <Link
              to="/auth/change-password"
              className="block text-primary-600 hover:text-primary-700"
            >
              Забыли пароль?
            </Link>
            <Link
              to="/auth/register"
              className="block text-primary-600 hover:text-primary-700"
            >
              Нет аккаунта? Зарегистрироваться
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
