import {format} from 'date-fns'
import {ru} from 'date-fns/locale'

// Форматирование даты из строки формата yyyy-MM-dd или Date объекта
// Работает с датами "как есть", без преобразований временных зон
export const formatDate = (
    date: string | Date,
    formatStr: string = 'dd.MM.yyyy'
): string => {
    if (!date) return ''

    // Строка формата yyyy-MM-dd (DATE из БД) - парсим как локальную дату
    if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
        const [year, month, day] = date.split('-')
        return format(new Date(+year, +month - 1, +day), formatStr, {locale: ru})
    }
    return format(new Date(date), formatStr, {locale: ru})
}

// Форматирование даты и времени из строки (например, "2026-03-19T10:00:00")
// Показываем время "как есть", без преобразований временных зон
export const formatDateTime = (date: string | Date): string => {
    if (!date) return ''
    // Парсим строку как локальное время (без Z в конце)
    return format(new Date(date), 'dd.MM.yyyy HH:mm', {locale: ru})
}

// Форматирование времени из строки формата HH:mm:ss
export const formatTimeByHoursAndMinutes = (time: string | null): string => {
    if (!time) return ''
    const [hours, minutes] = time.split(':')
    return `${hours}:${minutes}`
}

// Форматирование времени из строки (например, "2026-03-19T10:00:00")
// Показываем время "как есть", без преобразований временных зон
export const formatTime = (dateTimeString: string): string => {
    if (!dateTimeString) return ''
    // Парсим строку как локальное время
    const date = new Date(dateTimeString)
    return format(date, 'HH:mm', {locale: ru})
}

export const formatPhone = (phone: string): string => {
    // Already in format +7-XXX-XXX-XX-XX
    return phone
}

export const formatPrice = (price: number): string => {
    return `${price.toLocaleString('ru-RU')} ₽`
}

// Перевод дня недели на русский
export const formatDayOfWeek = (dayOfWeek: string): string => {
    const daysMap: Record<string, string> = {
        'MONDAY': 'Понедельник',
        'TUESDAY': 'Вторник',
        'WEDNESDAY': 'Среда',
        'THURSDAY': 'Четверг',
        'FRIDAY': 'Пятница',
        'SATURDAY': 'Суббота',
        'SUNDAY': 'Воскресенье'
    }
    return daysMap[dayOfWeek] || dayOfWeek
}

// Форматирование даты для input[type="date"] (yyyy-MM-dd)
// Принимает Date объект или строку формата yyyy-MM-dd
export const formatInputDate = (date: Date | string = new Date()): string => {
    if (!date) return ''

    // Если уже строка в формате yyyy-MM-dd, возвращаем как есть
    if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
        return date
    }

    // Если Date объект или другая строка, форматируем
    return format(new Date(date), 'yyyy-MM-dd')
}