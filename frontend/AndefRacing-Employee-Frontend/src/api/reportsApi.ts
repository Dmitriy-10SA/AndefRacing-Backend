import axiosInstance from '../lib/axios'
import {
  BookingStatisticsDto,
  FinancialStatisticsDto,
} from '@/types'

export const reportsApi = {
  getBookingStatistics: async (
    startDate: string,
    endDate: string
  ): Promise<BookingStatisticsDto> => {
    const response = await axiosInstance.get<BookingStatisticsDto>(
      '/reports/booking-statistics',
      {
        params: { startDate, endDate },
      }
    )
    return response.data
  },

  getFinancialStatistics: async (
    startDate: string,
    endDate: string
  ): Promise<FinancialStatisticsDto> => {
    const response = await axiosInstance.get<FinancialStatisticsDto>(
      '/reports/financial-statistics',
      {
        params: { startDate, endDate },
      }
    )
    return response.data
  },
}
