import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { profileApi } from '../../api/profileApi'
import { ClientChangePersonalInfoDto } from '../../types'
import LoadingSpinner from '../../components/LoadingSpinner'
import ErrorMessage from '../../components/ErrorMessage'
import PhoneInput from '../../components/PhoneInput'

const EditProfilePage = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [errorMessage, setErrorMessage] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['profile'],
    queryFn: profileApi.getPersonalInfo,
  })

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<ClientChangePersonalInfoDto>({
    values: data,
  })

  const updateMutation = useMutation({
    mutationFn: profileApi.changePersonalInfo,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['profile'] })
      navigate('/profile')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Ошибка обновления профиля'
      setErrorMessage(message)
    },
  })

  const onSubmit = (data: ClientChangePersonalInfoDto) => {
    setErrorMessage('')
    updateMutation.mutate(data)
  }

  if (isLoading) return <LoadingSpinner />

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Редактирование профиля</h1>

      <div className="card">
        {errorMessage && (
          <div className="mb-4">
            <ErrorMessage message={errorMessage} />
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label">Имя</label>
            <input
              type="text"
              className="input"
              {...register('name', {
                required: 'Имя обязательно',
                maxLength: {
                  value: 100,
                  message: 'Имя не должно превышать 100 символов',
                },
              })}
            />
            {errors.name && (
              <p className="error-text">{errors.name.message}</p>
            )}
          </div>

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

          <div className="flex gap-4">
            <button
              type="submit"
              disabled={updateMutation.isPending}
              className="btn-primary"
            >
              {updateMutation.isPending ? 'Сохранение...' : 'Сохранить'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/profile')}
              className="btn-secondary"
            >
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default EditProfilePage
