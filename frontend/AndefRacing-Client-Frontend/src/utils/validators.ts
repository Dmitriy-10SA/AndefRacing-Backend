export const PHONE_REGEX = /^\+7-\d{3}-\d{3}-\d{2}-\d{2}$/

export const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{}|;:',.<>/?]).+$/

export const validatePhone = (phone: string): boolean => {
  return PHONE_REGEX.test(phone)
}

export const validatePassword = (password: string): boolean => {
  return password.length >= 8 && PASSWORD_REGEX.test(password)
}

export const formatPhoneInput = (value: string): string => {
  // Remove all non-digits
  const digits = value.replace(/\D/g, '')

  // Format as +7-XXX-XXX-XX-XX
  if (digits.length === 0) return ''
  if (digits.length <= 1) return `+${digits}`
  if (digits.length <= 4) return `+${digits.slice(0, 1)}-${digits.slice(1)}`
  if (digits.length <= 7) return `+${digits.slice(0, 1)}-${digits.slice(1, 4)}-${digits.slice(4)}`
  if (digits.length <= 9) return `+${digits.slice(0, 1)}-${digits.slice(1, 4)}-${digits.slice(4, 7)}-${digits.slice(7)}`
  return `+${digits.slice(0, 1)}-${digits.slice(1, 4)}-${digits.slice(4, 7)}-${digits.slice(7, 9)}-${digits.slice(9, 11)}`
}
