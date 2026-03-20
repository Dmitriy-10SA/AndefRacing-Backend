import axiosInstance from '../lib/axios'
import {
  EmployeeAndRolesDto,
  AddNewEmployeeDto,
  AddExistingEmployeeDto,
  EmployeeRole,
  WorkScheduleExceptionDto,
  AddWorkScheduleExceptionDto,
  UpdateWorkScheduleDto,
  AddPriceDto,
  GameDto,
} from '@/types'

export const managementApi = {
  // HR Management
  isEmployeeInSystem: async (employeePhone: string): Promise<boolean> => {
    const response = await axiosInstance.get<boolean>(
      '/club-management/hr/is-employee-in-system',
      {
        params: { employeePhone },
      }
    )
    return response.data
  },

  addNewEmployeeToClub: async (data: AddNewEmployeeDto): Promise<void> => {
    await axiosInstance.post('/club-management/hr/add-new-employee-to-club', data)
  },

  addExistingEmployeeToClub: async (data: AddExistingEmployeeDto): Promise<void> => {
    await axiosInstance.post('/club-management/hr/add-existing-employee-to-club', data)
  },

  getEmployeesAndRoles: async (): Promise<EmployeeAndRolesDto[]> => {
    const response = await axiosInstance.get<EmployeeAndRolesDto[]>(
      '/club-management/hr'
    )
    return response.data
  },

  deleteEmployeeFromClub: async (employeeId: number): Promise<void> => {
    await axiosInstance.delete(`/club-management/hr/delete-employee-from-club/${employeeId}`)
  },

  addRoleToEmployee: async (employeeId: number, role: EmployeeRole): Promise<void> => {
    await axiosInstance.post(
      `/club-management/hr/add-role-to-employee-in-club/${employeeId}`,
      null,
      {
        params: { role },
      }
    )
  },

  updateEmployeeRole: async (
    employeeId: number,
    oldRole: EmployeeRole,
    newRole: EmployeeRole
  ): Promise<void> => {
    await axiosInstance.patch(
      `/club-management/hr/update-employee-role-in-club/${employeeId}`,
      null,
      {
        params: { oldRole, newRole },
      }
    )
  },

  deleteEmployeeRole: async (employeeId: number, role: EmployeeRole): Promise<void> => {
    await axiosInstance.delete(
      `/club-management/hr/delete-employee-role-in-club/${employeeId}`,
      {
        params: { role },
      }
    )
  },

  // Work Schedule Management
  addWorkScheduleException: async (data: AddWorkScheduleExceptionDto): Promise<void> => {
    await axiosInstance.post('/club-management/work-schedule/exceptions', data)
  },

  getWorkScheduleExceptions: async (
    startDate: string,
    endDate: string
  ): Promise<WorkScheduleExceptionDto[]> => {
    const response = await axiosInstance.get<WorkScheduleExceptionDto[]>(
      '/club-management/work-schedule/exceptions',
      {
        params: { startDate, endDate },
      }
    )
    return response.data
  },

  deleteWorkScheduleException: async (workScheduleExceptionId: number): Promise<void> => {
    await axiosInstance.delete(
      `/club-management/work-schedule/exceptions/${workScheduleExceptionId}`
    )
  },

  updateWorkSchedule: async (data: UpdateWorkScheduleDto): Promise<void> => {
    await axiosInstance.put('/club-management/work-schedule', data)
  },

  // Club Management
  updateCntEquipment: async (cntEquipment: number): Promise<void> => {
    await axiosInstance.patch('/club-management', null, {
      params: { cntEquipment },
    })
  },

  openClub: async (): Promise<void> => {
    await axiosInstance.patch('/club-management/open')
  },

  closeClub: async (): Promise<void> => {
    await axiosInstance.patch('/club-management/close')
  },

  // Price Management
  addPrice: async (data: AddPriceDto): Promise<void> => {
    await axiosInstance.post('/club-management/prices', data)
  },

  updatePrice: async (priceId: number, value: number): Promise<void> => {
    await axiosInstance.patch(`/club-management/prices/${priceId}`, null, {
      params: { value },
    })
  },

  deletePrice: async (priceId: number): Promise<void> => {
    await axiosInstance.delete(`/club-management/prices/${priceId}`)
  },

  // Games Management
  getAllGames: async (): Promise<GameDto[]> => {
    const response = await axiosInstance.get<GameDto[]>('/club-management/games')
    return response.data
  },

  addGameToClub: async (gameId: number): Promise<void> => {
    await axiosInstance.post(`/club-management/games/${gameId}`)
  },

  deleteGameFromClub: async (gameId: number): Promise<void> => {
    await axiosInstance.delete(`/club-management/games/${gameId}`)
  },

  // Photos Management
  managePhotos: async (photos: File[]): Promise<void> => {
    const formData = new FormData()
    if (photos.length === 0) {
      // Send empty file to clear photos
      formData.append('photos', new File([], ''))
    } else {
      photos.forEach((photo) => {
        formData.append('photos', photo)
      })
    }
    await axiosInstance.post('/club-management/photos/manage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}
