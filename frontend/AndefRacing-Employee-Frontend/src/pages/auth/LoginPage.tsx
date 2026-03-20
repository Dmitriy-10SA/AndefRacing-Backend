import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { useMutation } from '@tanstack/react-query'
import { authApi } from '@/api/authApi.ts'
import { useAuthStore } from '@/stores/authStore.ts'
import { EmployeeLoginDto, EmployeeClubDto } from '@/types'
import { validatePassword } from '@/utils/validators'
import ErrorMessage from '../../components/ErrorMessage'
import PhoneInput from '../../components/PhoneInput'
import PasswordInput from '../../components/PasswordInput'

const LoginPage = () => {
  const navigate = useNavigate()
  const { setToken, setCurrentClub } = useAuthStore()
  const [errorMessage, setErrorMessage] = useState('')
  const [step, setStep] = useState<'phone' | 'password' | 'club'>('phone')
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const [clubs, setClubs] = useState<EmployeeClubDto[]>([])
  const [isFirstEnter, setIsFirstEnter] = useState(false)

  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<EmployeeLoginDto & { confirmPassword?: string }>({
    mode: 'onSubmit',
    defaultValues: {
      phone: '',
      password: '',
      confirmPassword: '',
    },
  })

  const phoneValue = watch('phone')
  const passwordValue = watch('password')

  const checkPhoneMutation = useMutation({
    mutationFn: authApi.isFirstEnter,
    onSuccess: (isFirst) => {
      setIsFirstEnter(isFirst)
      setPhone(phoneValue)
      setStep('password')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || `Сотрудник с номером телефона ${phoneValue} не найден`
      setErrorMessage(message)
    },
  })

  const preLoginMutation = useMutation({
    mutationFn: authApi.preLogin,
    onSuccess: (clubsList) => {
      setClubs(clubsList)
      setPassword(passwordValue)
      setStep('club')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Неверный логин или пароль'
      setErrorMessage(message)
    },
  })

  const loginMutation = useMutation({
    mutationFn: ({ clubId, data }: { clubId: number; data: EmployeeLoginDto }) =>
      authApi.login(clubId, data),
    onSuccess: (data, variables) => {
      setToken(data.jwt)
      const selectedClub = clubs.find((c) => c.id === variables.clubId)
      if (selectedClub) {
        setCurrentClub(selectedClub)
      }
      navigate('/bookings')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Ошибка входа'
      setErrorMessage(message)
    },
  })

  const handlePhoneSubmit = () => {
    setErrorMessage('')
    checkPhoneMutation.mutate(phoneValue)
  }

  const handlePasswordSubmit = () => {
    setErrorMessage('')
    const loginData: EmployeeLoginDto = { phone, password: passwordValue }
    preLoginMutation.mutate(loginData)
  }

  const handleClubSelect = (clubId: number) => {
    setErrorMessage('')
    const loginData: EmployeeLoginDto = { phone, password }
    loginMutation.mutate({ clubId, data: loginData })
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">AndefRacing</h1>
          <p className="mt-2 text-gray-600">
            {step === 'phone' && 'Вход для сотрудников'}
            {step === 'password' && (isFirstEnter ? 'Задайте пароль' : 'Введите пароль')}
            {step === 'club' && 'Выберите клуб'}
          </p>
        </div>

        <div className="card">
          {errorMessage && (
            <div className="mb-4">
              <ErrorMessage message={errorMessage} />
            </div>
          )}

          {step === 'phone' && (
            <form
              onSubmit={handleSubmit(handlePhoneSubmit)}
              noValidate
              className="space-y-4"
            >
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

              <button
                type="submit"
                disabled={checkPhoneMutation.isPending}
                className="btn-primary w-full"
              >
                {checkPhoneMutation.isPending ? 'Проверка...' : 'Далее'}
              </button>
            </form>
          )}

          {step === 'password' && (
            <form
              onSubmit={handleSubmit(handlePasswordSubmit)}
              noValidate
              className="space-y-4"
            >
              <div>
                <label className="label">
                  {isFirstEnter ? 'Задайте пароль' : 'Пароль'}
                </label>
                <Controller
                  name="password"
                  control={control}
                  rules={{
                    required: 'Пароль обязателен',
                    minLength: {
                      value: 8,
                      message: 'Пароль должен содержать минимум 8 символов',
                    },
                    validate: (value) => {
                      if (isFirstEnter && !validatePassword(value)) {
                        return 'Пароль должен содержать заглавные и строчные буквы, цифры и специальные символы'
                      }
                      return true
                    },
                  }}
                  render={({ field }) => (
                    <PasswordInput
                      value={field.value}
                      onChange={field.onChange}
                      onBlur={field.onBlur}
                    />
                  )}
                />
                {errors.password && (
                  <p className="error-text">{errors.password.message}</p>
                )}
                {isFirstEnter && (
                  <p className="text-xs text-gray-500 mt-1">
                    Минимум 8 символов, включая заглавные и строчные буквы, цифры и специальные символы
                  </p>
                )}
              </div>

              {isFirstEnter && (
                <div>
                  <label className="label">Подтвердите пароль</label>
                  <Controller
                    name="confirmPassword"
                    control={control}
                    rules={{
                      required: 'Подтверждение пароля обязательно',
                      validate: (value) =>
                        value === passwordValue || 'Пароли не совпадают',
                    }}
                    render={({ field }) => (
                      <PasswordInput
                        value={field.value || ''}
                        onChange={field.onChange}
                        onBlur={field.onBlur}
                      />
                    )}
                  />
                  {errors.confirmPassword && (
                    <p className="error-text">{errors.confirmPassword.message}</p>
                  )}
                </div>
              )}

              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => {
                    setStep('phone')
                    setErrorMessage('')
                  }}
                  className="btn-secondary flex-1"
                >
                  Назад
                </button>
                <button
                  type="submit"
                  disabled={preLoginMutation.isPending}
                  className="btn-primary flex-1"
                >
                  {preLoginMutation.isPending ? 'Вход...' : 'Далее'}
                </button>
              </div>
            </form>
          )}

          {step === 'club' && (
            <div className="space-y-4">
              <p className="text-sm text-gray-600">
                Выберите клуб, в котором будете работать:
              </p>
              <div className="space-y-2">
                {clubs.map((club) => (
                  <button
                    key={club.id}
                    onClick={() => handleClubSelect(club.id)}
                    disabled={loginMutation.isPending}
                    className="w-full text-left p-4 border border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors disabled:opacity-50"
                  >
                    <div className="font-medium">{club.name}</div>
                    <div className="text-sm text-gray-600">
                      {club.city.name}, {club.city.region.name}
                    </div>
                    <div className="text-sm text-gray-500">{club.address}</div>
                  </button>
                ))}
              </div>
              <button
                type="button"
                onClick={() => {
                  setStep('password')
                  setErrorMessage('')
                }}
                className="btn-secondary w-full"
              >
                Назад
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default LoginPage
