import axiosInstance from '../lib/axios'
import {
  RegionShortDto,
  CityShortDto,
  PagedClubShortListDto,
  ClubFullInfoDto,
} from '../types'

export const searchApi = {
  getRegions: async (): Promise<RegionShortDto[]> => {
    const response = await axiosInstance.get<RegionShortDto[]>('/search/regions')
    return response.data
  },

  getCities: async (regionId: number): Promise<CityShortDto[]> => {
    const response = await axiosInstance.get<CityShortDto[]>(
      `/search/cities/${regionId}`
    )
    return response.data
  },

  getClubs: async (
    cityId: number,
    pageNumber: number,
    pageSize: number
  ): Promise<PagedClubShortListDto> => {
    const response = await axiosInstance.get<PagedClubShortListDto>(
      `/search/clubs/${cityId}`,
      {
        params: { pageNumber, pageSize },
      }
    )
    return response.data
  },

  getClubFullInfo: async (clubId: number): Promise<ClubFullInfoDto> => {
    const response = await axiosInstance.get<ClubFullInfoDto>(
      `/search/clubs/${clubId}/full-info`
    )
    return response.data
  },
}
