// Employee Role Enum
export enum EmployeeRole {
    EMPLOYEE = 'EMPLOYEE',
    ADMINISTRATOR = 'ADMIN',
    MANAGER = 'MANAGER'
}

// Auth types
export interface EmployeeLoginDto {
    phone: string
    password: string
}

export interface EmployeeAuthResponseDto {
    jwt: string
}

export interface EmployeeClubDto {
    id: number
    name: string
    phone: string
    email: string
    address: string
    cntEquipment: number
    isOpen: boolean
    city: CityDto
}

// Profile types
export interface EmployeePersonalInfoDto {
    phone: string
    name: string
    surname: string
    patronymic: string | null
    roles: EmployeeRole[]
}

// Location types
export interface RegionShortDto {
    id: number
    name: string
}

export interface CityShortDto {
    id: number
    name: string
}

export interface CityDto {
    id: number
    name: string
    region: RegionShortDto
}

// Photo types
export interface PhotoDto {
    id: number
    url: string
    sequenceNumber: number
}

// Club types
export interface ClubShortDto {
    id: number
    name: string
    phone: string
    email: string
    address: string
    cntEquipment: number
    isOpen: boolean
}

export interface ClubInfoDto extends ClubShortDto {
    mainPhoto: PhotoDto | null
}

export interface PageInfoDto {
    pageNumber: number
    pageSize: number
    totalElements: number
    totalPages: number
    isLast: boolean
}

export interface PagedClubShortListDto {
    content: ClubInfoDto[]
    pageInfoDto: PageInfoDto
}

export interface GameDto {
    id: number
    name: string
    photoUrl: string | null
    isActive: boolean
}

export interface PriceDto {
    id: number
    durationMinutes: number
    value: number
}

export interface WorkScheduleDto {
    id: number
    dayOfWeek: string
    openTime: string
    closeTime: string
    isWorkDay: boolean
}

export interface ClubFullInfoDto extends ClubShortDto {
    photos: PhotoDto[]
    games: GameDto[]
    prices: PriceDto[]
    workSchedules: WorkScheduleDto[]
}

// Booking types
export enum BookingStatus {
    PENDING_PAYMENT = 'PENDING_PAYMENT',
    PAID = 'PAID',
    CANCELLED = 'CANCELLED'
}

export interface FreeBookingSlotDto {
    startDateTime: string
    endDateTime: string
}

export interface EmployeeMakeBookingDto {
    cntEquipment: number
    slot: FreeBookingSlotDto
    note?: string
}

export interface BookingShortDto {
    id: number
    startDateTime: string
    endDateTime: string
    status: BookingStatus
}

export interface EmployeeBookingShortDto extends BookingShortDto {}

export interface ClientDto {
    id: number
    name: string
    phone: string
}

export interface EmployeeBookingFullInfoDto extends EmployeeBookingShortDto {
    cntEquipment: number
    price: number
    note: string | null
    client: ClientDto | null
}

export interface PagedEmployeeBookingShortListDto {
    content: EmployeeBookingShortDto[]
    pageInfo: PageInfoDto
}

// HR Management types
export interface EmployeeDto {
    id: number
    surname: string
    name: string
    patronymic: string | null
    phone: string
}

export interface EmployeeAndRolesDto {
    employeeDto: EmployeeDto
    roles: EmployeeRole[]
}

export interface AddNewEmployeeDto {
    phone: string
    roles: EmployeeRole[]
    surname: string
    name: string
    patronymic: string | null
}

export interface AddExistingEmployeeDto {
    phone: string
    roles: EmployeeRole[]
}

// Work Schedule Management types
export interface WorkScheduleExceptionDto {
    id: number
    date: string
    openTime: string | null
    closeTime: string | null
    isWorkDay: boolean
    description: string | null
}

export interface AddWorkScheduleExceptionDto {
    date: string
    openTime: string | null
    closeTime: string | null
    isWorkDay: boolean
    description: string | null
}

export interface UpdateWorkScheduleDto {
    dayOfWeek: string
    openTime: string | null
    closeTime: string | null
    isWorkDay: boolean
}

// Price Management types
export interface AddPriceDto {
    durationMinutes: number
    value: number
}

// Report types
export interface BookingStatisticsDto {
    clubId: number
    startDate: string
    endDate: string
    bookingsCount: number
    cancellationsPercent: number
    dateAndBookingsCountDtoList: DateAndBookingsCountDto[]
}

export interface DateAndBookingsCountDto {
    date: string
    bookingsCount: number
}

export interface FinancialStatisticsDto {
    clubId: number
    startDate: string
    endDate: string
    totalRevenue: number
    dateAndTotalRevenues: DateAndTotalRevenueDto[]
    averageReceipt: number
}

export interface DateAndTotalRevenueDto {
    date: string
    revenue: number
}

// API Error types
export interface ApiError {
    message: string
    status?: number
}
