// Форматирование даты - отображаем как есть, без преобразований
export const formatDate = (date: string, formatStr: string = 'dd.MM.yyyy'): string => {
  // Извлекаем дату из ISO строки
  const match = date.match(/^(\d{4})-(\d{2})-(\d{2})/)
  if (!match) return date

  const [, year, month, day] = match

  if (formatStr === 'dd.MM.yyyy') {
    return `${day}.${month}.${year}`
  }
  if (formatStr === 'yyyy-MM-dd') {
    return `${year}-${month}-${day}`
  }

  return date
}

export const formatDateTime = (dateTime: string): string => {
  // Извлекаем дату и время из ISO строки - отображаем как есть
  // Формат: 2024-01-15T14:30:00 или 2024-01-15T14:30:00.000
  const match = dateTime.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/)
  if (!match) return dateTime

  const [, year, month, day, hours, minutes] = match
  return `${day}.${month}.${year} ${hours}:${minutes}`
}

export const formatTime = (dateTime: string): string => {
  // Извлекаем только время из ISO строки
  const match = dateTime.match(/T(\d{2}:\d{2})/)
  if (!match) return dateTime

  return match[1]
}

export const formatPhone = (phone: string): string => {
  // Already in format +7-XXX-XXX-XX-XX
  return phone
}

export const formatPrice = (price: number): string => {
  return `${price.toLocaleString('ru-RU')} ₽`
}

/**
 * Получить полный URL для изображения
 * @param url - относительный URL изображения (например, /files/clubs/1/photo.jpg)
 * @returns полный URL для загрузки изображения
 */
export const getImageUrl = (url: string | null): string => {
  if (!url) {
    return '/placeholder-image.jpg' // можно добавить placeholder
  }

  // Если URL уже полный (начинается с http), возвращаем как есть
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  // Для статических файлов (/files/**) не добавляем /api
  if (url.startsWith('/files/')) {
    return url
  }

  // Добавляем базовый URL API для остальных запросов
  const API_BASE_URL = '/api'
  return `${API_BASE_URL}${url}`
}
