import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { EmployeeClubDto } from '@/types'

interface AuthState {
  token: string | null
  isAuthenticated: boolean
  currentClub: EmployeeClubDto | null
  setToken: (token: string) => void
  setCurrentClub: (club: EmployeeClubDto) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      isAuthenticated: false,
      currentClub: null,
      setToken: (token: string) => set({ token, isAuthenticated: true }),
      setCurrentClub: (club: EmployeeClubDto) => set({ currentClub: club }),
      logout: () => set({ token: null, isAuthenticated: false, currentClub: null }),
    }),
    {
      name: 'auth-storage',
    }
  )
)
