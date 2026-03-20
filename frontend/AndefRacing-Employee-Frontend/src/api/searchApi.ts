import axiosInstance from '../lib/axios'
import {ClubFullInfoDto,} from '@/types'

export const searchApi = {
    getClubFullInfo: async (clubId: number): Promise<ClubFullInfoDto> => {
        const response = await axiosInstance.get<ClubFullInfoDto>(
            `/search/clubs/${clubId}/full-info`
        )
        return response.data
    },
}
