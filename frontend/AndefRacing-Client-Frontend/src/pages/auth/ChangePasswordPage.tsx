import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { useMutation } from '@tanstack/react-query'
import { authApi } from '../../api/authApi'
import { useAuthStore } from '../../stores/authStore'
import { ClientChangePasswordDto } from '../../types'
import ErrorMessage from '../../components/ErrorMessage'
import PhoneInput from '../../components/PhoneInput'
import PasswordInput from '../../components/PasswordInput'

interface ChangePasswordFormData extends ClientChangePasswordDto {
  confirmPassword: string
}

const ChangePasswordPage = () => {
  const navigate = useNavigate()
  const setToken = useAuthStore((state) => state.setToken)
  const [errorMessage, setErrorMessage] = useState('')
  const [successMessage, setSuccessMessage] = useState('')

  const {
    register,
    handleSubmit,
    control,
    watch,
    formState: { errors },
  } = useForm<ChangePasswordFormData>()

  const changePasswordMutation = useMutation({
    mutationFn: authApi.changePassword,
    onSuccess: (data) => {
      setToken(data.jwt)
      setSuccessMessage('Пароль успешно изменен')
      setTimeout(() => {
        navigate('/search')
      }, 2000)
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Ошибка изменения пароля'
      setErrorMessage(message)
    },
  })

  const password = watch('password')

  const onSubmit = (data: ChangePasswordFormData) => {
    setErrorMessage('')
    setSuccessMessage('')
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { confirmPassword, ...changePasswordData } = data
    changePasswordMutation.mutate(changePasswordData)
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">AndefRacing</h1>
          <p className="mt-2 text-gray-600">Изменение пароля</p>
        </div>

        <div className="card">
          {errorMessage && (
            <div className="mb-4">
              <ErrorMessage message={errorMessage} />
            </div>
          )}

          {successMessage && (
            <div className="mb-4 bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg">
              <p>{successMessage}</p>
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
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
              <label className="label">Новый пароль</label>
              <PasswordInput
                {...register('password', {
                  required: 'Пароль обязателен',
                  minLength: {
                    value: 8,
                    message: 'Пароль должен содержать не менее 8 символов',
                  },
                  pattern: {
                    value: /^(?=.*[a-zа-я])(?=.*[A-ZА-Я])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{}|;:',.<>/?]).+$/,
                    message: 'Пароль должен содержать заглавную букву, цифру и спецсимвол',
                  },
                })}
              />
              {errors.password && (
                <p className="error-text">{errors.password.message}</p>
              )}
              <p className="text-xs text-gray-500 mt-1">
                Минимум 8 символов, включая заглавную букву, цифру и спецсимвол
              </p>
            </div>

            <div>
              <label className="label">Подтвердите новый пароль</label>
              <PasswordInput
                {...register('confirmPassword', {
                  required: 'Подтверждение пароля обязательно',
                  validate: (value) =>
                    value === password || 'Пароли не совпадают',
                })}
              />
              {errors.confirmPassword && (
                <p className="error-text">{errors.confirmPassword.message}</p>
              )}
            </div>

            <button
              type="submit"
              disabled={changePasswordMutation.isPending}
              className="btn-primary w-full"
            >
              {changePasswordMutation.isPending ? 'Изменение...' : 'Изменить пароль'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <Link
              to="/auth/login"
              className="text-primary-600 hover:text-primary-700"
            >
              Вернуться к входу
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ChangePasswordPage
