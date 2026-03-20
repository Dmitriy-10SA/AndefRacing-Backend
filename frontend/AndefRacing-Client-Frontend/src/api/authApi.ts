import axiosInstance from '../lib/axios'
import {
  ClientRegisterDto,
  ClientLoginDto,
  ClientChangePasswordDto,
  ClientAuthResponseDto,
} from '../types'

export const authApi = {
  register: async (data: ClientRegisterDto): Promise<ClientAuthResponseDto> => {
    const response = await axiosInstance.post<ClientAuthResponseDto>(
      '/auth/client/register',
      data
    )
    return response.data
  },

  login: async (data: ClientLoginDto): Promise<ClientAuthResponseDto> => {
    const response = await axiosInstance.post<ClientAuthResponseDto>(
      '/auth/client/login',
      data
    )
    return response.data
  },

  changePassword: async (data: ClientChangePasswordDto): Promise<ClientAuthResponseDto> => {
    const response = await axiosInstance.patch<ClientAuthResponseDto>(
      '/auth/client/change-password',
      data
    )
    return response.data
  },
}
