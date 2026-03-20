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
