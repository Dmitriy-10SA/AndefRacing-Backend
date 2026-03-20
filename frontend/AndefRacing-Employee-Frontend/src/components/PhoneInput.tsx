import { forwardRef, ChangeEvent } from 'react'

interface PhoneInputProps {
  value?: string
  onChange: (value: string) => void
  onBlur?: () => void
  placeholder?: string
  className?: string
  name?: string
}

const PhoneInput = forwardRef<HTMLInputElement, PhoneInputProps>(
  ({ value = '', onChange, onBlur, placeholder = '+7-XXX-XXX-XX-XX', className = 'input', name }, ref) => {
    const formatPhoneNumber = (input: string): string => {
      // Удаляем все нецифровые символы кроме +
      let digits = input.replace(/[^\d+]/g, '')

      // Если пустая строка или только +, возвращаем пустую строку
      if (digits === '' || digits === '+') {
        return ''
      }

      // Если начинается с 8, заменяем на +7
      if (digits.startsWith('8')) {
        digits = '+7' + digits.slice(1)
      }

      // Если не начинается с +7, добавляем
      if (!digits.startsWith('+7')) {
        if (digits.startsWith('7')) {
          digits = '+' + digits
        } else if (digits.startsWith('+')) {
          digits = '+7' + digits.slice(1)
        } else {
          digits = '+7' + digits
        }
      }

      // Оставляем только +7 и следующие 10 цифр
      const prefix = '+7'
      const numbers = digits.slice(2).replace(/\D/g, '').slice(0, 10)

      // Если нет цифр после +7, возвращаем пустую строку
      if (numbers.length === 0) {
        return ''
      }

      // Форматируем: +7-XXX-XXX-XX-XX
      let formatted = prefix
      if (numbers.length > 0) {
        formatted += '-' + numbers.slice(0, 3)
      }
      if (numbers.length > 3) {
        formatted += '-' + numbers.slice(3, 6)
      }
      if (numbers.length > 6) {
        formatted += '-' + numbers.slice(6, 8)
      }
      if (numbers.length > 8) {
        formatted += '-' + numbers.slice(8, 10)
      }

      return formatted
    }

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
      const formatted = formatPhoneNumber(e.target.value)
      onChange(formatted)
    }

    return (
      <input
        ref={ref}
        type="text"
        name={name}
        value={value}
        onChange={handleChange}
        onBlur={onBlur}
        placeholder={placeholder}
        className={className}
      />
    )
  }
)

PhoneInput.displayName = 'PhoneInput'

export default PhoneInput
