import axiosInstance from '../lib/axios'
import {
  FreeBookingSlotDto,
  EmployeeMakeBookingDto,
  EmployeeBookingFullInfoDto,
  PagedEmployeeBookingShortListDto,
} from '@/types'

export const bookingApi = {
  getFreeSlots: async (
    durationMinutes: number,
    cntEquipment: number,
    date: string,
    userCurrentDate: string,
    userCurrentTime: string
  ): Promise<FreeBookingSlotDto[]> => {
    const response = await axiosInstance.get<FreeBookingSlotDto[]>(
      '/bookings/employee/free-slots',
      {
        params: { durationMinutes, cntEquipment, date, userCurrentDate, userCurrentTime },
      }
    )
    return response.data
  },

  makeBooking: async (data: EmployeeMakeBookingDto): Promise<void> => {
    await axiosInstance.post('/bookings/employee/make-booking', data)
  },

  getBookings: async (
    startDate: string,
    endDate: string,
    clientPhone: string | null,
    pageNumber: number,
    pageSize: number
  ): Promise<PagedEmployeeBookingShortListDto> => {
    const params: any = { startDate, endDate, pageNumber, pageSize }
    if (clientPhone) {
      params.clientPhone = clientPhone
    }
    const response = await axiosInstance.get<PagedEmployeeBookingShortListDto>(
      '/bookings/employee',
      { params }
    )
    return response.data
  },

  getBookingFullInfo: async (bookingId: number): Promise<EmployeeBookingFullInfoDto> => {
    const response = await axiosInstance.get<EmployeeBookingFullInfoDto>(
      `/bookings/employee/full-info/${bookingId}`
    )
    return response.data
  },

  confirmBookingPayment: async (bookingId: number): Promise<void> => {
    await axiosInstance.patch(`/bookings/employee/confirm-booking-payment/${bookingId}`)
  },

  cancelBooking: async (bookingId: number): Promise<void> => {
    await axiosInstance.patch(`/bookings/employee/cancel/${bookingId}`)
  },
}
