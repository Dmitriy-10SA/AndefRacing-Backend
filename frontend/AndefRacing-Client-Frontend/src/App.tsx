import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './stores/authStore'
import Layout from './components/Layout'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import ChangePasswordPage from './pages/auth/ChangePasswordPage'
import ProfilePage from './pages/profile/ProfilePage'
import EditProfilePage from './pages/profile/EditProfilePage'
import SearchPage from './pages/search/SearchPage'
import ClubDetailsPage from './pages/search/ClubDetailsPage'
import FavoriteClubsPage from './pages/favorites/FavoriteClubsPage'
import BookingsPage from './pages/bookings/BookingsPage'
import BookingDetailsPage from './pages/bookings/BookingDetailsPage'
import MakeBookingPage from './pages/bookings/MakeBookingPage'

function App() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)

  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/auth/login" element={!isAuthenticated ? <LoginPage /> : <Navigate to="/search" />} />
        <Route path="/auth/register" element={!isAuthenticated ? <RegisterPage /> : <Navigate to="/search" />} />
        <Route path="/auth/change-password" element={!isAuthenticated ? <ChangePasswordPage /> : <Navigate to="/search" />} />

        {/* Protected routes */}
        <Route element={<Layout />}>
          <Route path="/search" element={<SearchPage />} />
          <Route path="/clubs/:clubId" element={<ClubDetailsPage />} />

          <Route path="/profile" element={isAuthenticated ? <ProfilePage /> : <Navigate to="/auth/login" />} />
          <Route path="/profile/edit" element={isAuthenticated ? <EditProfilePage /> : <Navigate to="/auth/login" />} />

          <Route path="/favorites" element={isAuthenticated ? <FavoriteClubsPage /> : <Navigate to="/auth/login" />} />

          <Route path="/bookings" element={isAuthenticated ? <BookingsPage /> : <Navigate to="/auth/login" />} />
          <Route path="/bookings/:clubId/:bookingId" element={isAuthenticated ? <BookingDetailsPage /> : <Navigate to="/auth/login" />} />
          <Route path="/clubs/:clubId/book" element={isAuthenticated ? <MakeBookingPage /> : <Navigate to="/auth/login" />} />

          <Route path="/" element={<Navigate to="/search" />} />
          <Route path="*" element={<Navigate to="/search" />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
