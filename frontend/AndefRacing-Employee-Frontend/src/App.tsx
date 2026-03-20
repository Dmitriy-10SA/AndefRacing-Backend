import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider, useQuery } from '@tanstack/react-query'
import { useAuthStore } from './stores/authStore'
import { profileApi } from './api/profileApi'
import { EmployeeRole } from './types'
import Layout from './components/Layout'

// Auth pages
import LoginPage from './pages/auth/LoginPage'

// Profile pages
import ProfilePage from './pages/profile/ProfilePage'

// Booking pages
import BookingsPage from './pages/bookings/BookingsPage'
import BookingDetailsPage from './pages/bookings/BookingDetailsPage'
import MakeBookingPage from './pages/bookings/MakeBookingPage'

// Management pages
import HRManagementPage from './pages/management/HRManagementPage'
import ClubManagementPage from './pages/management/ClubManagementPage'
import ReportsPage from './pages/management/ReportsPage'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
})

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  return isAuthenticated ? <>{children}</> : <Navigate to="/auth/login" replace />
}

function RoleProtectedRoute({
  children,
  allowedRoles
}: {
  children: React.ReactNode
  allowedRoles: EmployeeRole[]
}) {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  const { data: personalInfo, isLoading } = useQuery({
    queryKey: ['profile'],
    queryFn: profileApi.getPersonalInfo,
    enabled: isAuthenticated,
  })

  if (!isAuthenticated) {
    return <Navigate to="/auth/login" replace />
  }

  if (isLoading) {
    return <div className="flex justify-center items-center min-h-screen">Загрузка...</div>
  }

  const hasAccess = personalInfo?.roles.some(role => allowedRoles.includes(role))

  if (!hasAccess) {
    return <Navigate to="/profile" replace />
  }

  return <>{children}</>
}

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/auth/login" element={<LoginPage />} />

          <Route element={<Layout />}>
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Navigate to="/profile" replace />
                </ProtectedRoute>
              }
            />

            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/bookings"
              element={
                <RoleProtectedRoute allowedRoles={[EmployeeRole.ADMINISTRATOR, EmployeeRole.MANAGER]}>
                  <BookingsPage />
                </RoleProtectedRoute>
              }
            />
            <Route
              path="/bookings/:id"
              element={
                <RoleProtectedRoute allowedRoles={[EmployeeRole.ADMINISTRATOR, EmployeeRole.MANAGER]}>
                  <BookingDetailsPage />
                </RoleProtectedRoute>
              }
            />
            <Route
              path="/bookings/make"
              element={
                <RoleProtectedRoute allowedRoles={[EmployeeRole.ADMINISTRATOR, EmployeeRole.MANAGER]}>
                  <MakeBookingPage />
                </RoleProtectedRoute>
              }
            />

            <Route
              path="/management/hr"
              element={
                <RoleProtectedRoute allowedRoles={[EmployeeRole.MANAGER]}>
                  <HRManagementPage />
                </RoleProtectedRoute>
              }
            />
            <Route
              path="/management/club"
              element={
                <RoleProtectedRoute allowedRoles={[EmployeeRole.MANAGER]}>
                  <ClubManagementPage />
                </RoleProtectedRoute>
              }
            />
            <Route
              path="/management/reports"
              element={
                <RoleProtectedRoute allowedRoles={[EmployeeRole.MANAGER]}>
                  <ReportsPage />
                </RoleProtectedRoute>
              }
            />
          </Route>
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  )
}

export default App
