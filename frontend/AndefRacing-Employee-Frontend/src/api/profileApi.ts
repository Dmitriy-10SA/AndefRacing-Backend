import axiosInstance from '../lib/axios'
import {
  EmployeePersonalInfoDto,
} from '@/types'

export const profileApi = {
  getPersonalInfo: async (): Promise<EmployeePersonalInfoDto> => {
    const response = await axiosInstance.get<EmployeePersonalInfoDto>(
      '/profile/employee/personal-info'
    )
    return response.data
  },
}
