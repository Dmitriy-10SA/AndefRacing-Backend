import { Heart } from 'lucide-react'

interface FavoriteButtonProps {
  isFavorite: boolean
  onClick: (e: React.MouseEvent) => void
  disabled?: boolean
  className?: string
}

const FavoriteButton = ({ isFavorite, onClick, disabled = false, className = '' }: FavoriteButtonProps) => {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className={`p-2 rounded-lg transition-colors ${
        isFavorite
          ? 'bg-red-100 text-red-600 hover:bg-red-200'
          : 'bg-gray-100 text-gray-400 hover:bg-gray-200 hover:text-gray-600'
      } disabled:opacity-50 disabled:cursor-not-allowed ${className}`}
      title={isFavorite ? 'Удалить из избранного' : 'Добавить в избранное'}
    >
      <Heart className={`w-5 h-5 ${isFavorite ? 'fill-current' : ''}`} />
    </button>
  )
}

export default FavoriteButton
