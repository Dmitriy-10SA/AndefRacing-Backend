import axiosInstance from '../lib/axios'
import {
  FreeBookingSlotDto,
  ClientMakeBookingDto,
  ClientBookingFullInfoDto,
  PagedClientBookingShortListDto,
} from '../types'

export const bookingApi = {
  getFreeSlots: async (
    clubId: number,
    durationMinutes: number,
    cntEquipment: number,
    date: string,
    userCurrentDate: string,
    userCurrentTime: string
  ): Promise<FreeBookingSlotDto[]> => {
    const response = await axiosInstance.get<FreeBookingSlotDto[]>(
      `/bookings/client/free-slots/${clubId}`,
      {
        params: { durationMinutes, cntEquipment, date, userCurrentDate, userCurrentTime },
      }
    )
    return response.data
  },

  makeBooking: async (
    clubId: number,
    data: ClientMakeBookingDto
  ): Promise<void> => {
    await axiosInstance.post(`/bookings/client/make-booking/${clubId}`, data)
  },

  getBookings: async (
    startDate: string,
    endDate: string,
    pageNumber: number,
    pageSize: number
  ): Promise<PagedClientBookingShortListDto> => {
    const response = await axiosInstance.get<PagedClientBookingShortListDto>(
      '/bookings/client',
      {
        params: { startDate, endDate, pageNumber, pageSize },
      }
    )
    return response.data
  },

  getBookingFullInfo: async (
    clubId: number,
    bookingId: number
  ): Promise<ClientBookingFullInfoDto> => {
    const response = await axiosInstance.get<ClientBookingFullInfoDto>(
      `/bookings/client/${clubId}/${bookingId}`
    )
    return response.data
  },
}
