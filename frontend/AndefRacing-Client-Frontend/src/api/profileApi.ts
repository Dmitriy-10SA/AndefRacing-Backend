import axiosInstance from '../lib/axios'
import {
  ClientPersonalInfoDto,
  ClientChangePersonalInfoDto,
  PagedFavoriteClubShortListDto,
} from '../types'

export const profileApi = {
  getPersonalInfo: async (): Promise<ClientPersonalInfoDto> => {
    const response = await axiosInstance.get<ClientPersonalInfoDto>(
      '/profile/client/personal-info'
    )
    return response.data
  },

  changePersonalInfo: async (data: ClientChangePersonalInfoDto): Promise<void> => {
    await axiosInstance.patch('/profile/client/change-personal-info', data)
  },

  getFavoriteClubs: async (pageNumber: number, pageSize: number): Promise<PagedFavoriteClubShortListDto> => {
    const response = await axiosInstance.get<PagedFavoriteClubShortListDto>(
      '/profile/client/favorite-clubs',
      {
        params: { pageNumber, pageSize },
      }
    )
    return response.data
  },

  addFavoriteClub: async (clubId: number): Promise<void> => {
    await axiosInstance.post(`/profile/client/favorite-clubs/${clubId}`)
  },

  deleteFavoriteClub: async (clubId: number): Promise<void> => {
    await axiosInstance.delete(`/profile/client/favorite-clubs/${clubId}`)
  },

  isClubFavorite: async (clubId: number): Promise<boolean> => {
    const response = await axiosInstance.get<boolean>(
      `/profile/client/favorite-clubs/${clubId}/check`
    )
    return response.data
  },
}
