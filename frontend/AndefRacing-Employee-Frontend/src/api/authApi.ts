import axiosInstance from '../lib/axios'
import {
  EmployeeLoginDto,
  EmployeeAuthResponseDto,
  EmployeeClubDto,
} from '@/types'

export const authApi = {
  isFirstEnter: async (phone: string): Promise<boolean> => {
    const response = await axiosInstance.get<boolean>(
      '/auth/employee/is-first-enter',
      {
        params: { phone },
      }
    )
    return response.data
  },

  preLogin: async (data: EmployeeLoginDto): Promise<EmployeeClubDto[]> => {
    const response = await axiosInstance.post<EmployeeClubDto[]>(
      '/auth/employee/pre-login',
      data
    )
    return response.data
  },

  login: async (clubId: number, data: EmployeeLoginDto): Promise<EmployeeAuthResponseDto> => {
    const response = await axiosInstance.post<EmployeeAuthResponseDto>(
      `/auth/employee/login/${clubId}`,
      data
    )
    return response.data
  },
}
