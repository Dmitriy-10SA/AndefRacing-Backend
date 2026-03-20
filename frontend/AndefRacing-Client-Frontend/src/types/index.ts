// Auth types
export interface ClientRegisterDto {
    name: string
    phone: string
    password: string
}

export interface ClientLoginDto {
    phone: string
    password: string
}

export interface ClientChangePasswordDto {
    phone: string
    password: string
}

export interface ClientAuthResponseDto {
    jwt: string
}

// Profile types
export interface ClientPersonalInfoDto {
    phone: string
    name: string
}

export interface ClientChangePersonalInfoDto {
    name: string
    phone: string
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
    photo: PhotoDto | null
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

// Favorite clubs types
export interface FavoriteClubShortDto extends ClubShortDto {
    city: CityDto
    mainPhoto: PhotoDto | null
}

export interface PagedFavoriteClubShortListDto {
    content: FavoriteClubShortDto[]
    pageInfo: PageInfoDto
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

export interface ClientMakeBookingDto {
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

export interface ClientBookingShortDto extends BookingShortDto {
    club: ClubShortDto
    city: CityDto
}

export interface ClientBookingFullInfoDto extends ClientBookingShortDto {
    cntEquipment: number
    price: number
    note: string | null
}

export interface PagedClientBookingShortListDto {
    content: ClientBookingShortDto[]
    pageInfo: PageInfoDto
}

// API Error types
export interface ApiError {
    message: string
    status?: number
}
