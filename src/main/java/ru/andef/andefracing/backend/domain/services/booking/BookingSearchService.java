package ru.andef.andefracing.backend.domain.services.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.repositories.club.BookingRepository;
import ru.andef.andefracing.backend.data.repositories.club.WorkScheduleExceptionRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.InvalidDateRangeException;
import ru.andef.andefracing.backend.domain.exceptions.auth.UserNotFoundFromTokenException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.domain.mappers.club.BookingMapper;
import ru.andef.andefracing.backend.domain.mappers.club.ClubMapper;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.domain.services.search.ClientSearchService;
import ru.andef.andefracing.backend.domain.services.search.ClubSearchService;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.PagedClientBookingShortListDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.PagedEmployeeBookingShortListDto;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingSearchService {
    private static final String START_DATE_TIME = "startDateTime";

    private final ClientSearchService clientSearchService;
    private final ClubSearchService clubSearchService;

    private final BookingRepository bookingRepository;
    private final WorkScheduleExceptionRepository workScheduleExceptionRepository;

    private final BookingMapper bookingMapper;
    private final ClubMapper clubMapper;
    private final CityMapper cityMapper;
    private final ClientMapper clientMapper;
    private final RegionMapper regionMapper;

    /**
     * Подсчет числа занятого оборудования на слот
     */
    private int calculateUsedCntEquipments(
            List<Booking> bookings,
            OffsetDateTime slotStart,
            OffsetDateTime slotEnd
    ) {
        int usedCntEquipment = 0;
        for (Booking booking : bookings) {
            boolean isBookingInSlot = booking.getStartDateTime().isBefore(slotEnd) &&
                    booking.getEndDateTime().isAfter(slotStart);
            boolean isBookingStatusNotCancelled = booking.getStatus() != BookingStatus.CANCELLED;
            if (isBookingInSlot && isBookingStatusNotCancelled) {
                usedCntEquipment += booking.getCntEquipment();
            }
        }
        return usedCntEquipment;
    }

    /**
     * Получение доступных слотов для бронирования
     */
    @Transactional(readOnly = true)
    public List<FreeBookingSlotDto> getFreeBookingSlotsInClub(
            int clubId,
            FreeBookingSlotsRequestDto freeBookingSlotsRequestDto,
            LocalDate userCurrentDate,
            LocalTime userCurrentTime
    ) {
        Club club = clubSearchService.findClubById(clubId);
        if (!club.isOpen()) {
            throw new EntityNotFoundException("Клуб закрыт");
        }
        LocalDate date = freeBookingSlotsRequestDto.date();
        short durationMinutes = freeBookingSlotsRequestDto.durationMinutes();
        short cntEquipment = freeBookingSlotsRequestDto.cntEquipment();
        OffsetDateTime dayStart = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime dayEnd = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        List<Booking> bookings = bookingRepository.findAllByDateRangeAndClubId(clubId, dayStart, dayEnd);
        // учет графика работы
        for (WorkSchedule workSchedule : club.getWorkSchedules()) {
            if (workSchedule.getDayOfWeek() == date.getDayOfWeek().getValue()) {
                if (!workSchedule.isWorkDay()) {
                    return List.of();
                }
                dayStart = OffsetDateTime.of(date, workSchedule.getOpenTime(), ZoneOffset.UTC);
                dayEnd = OffsetDateTime.of(date, workSchedule.getCloseTime(), ZoneOffset.UTC);
            }
        }
        // учет дней-исключений
        Optional<WorkScheduleException> workScheduleException = workScheduleExceptionRepository
                .findByClubIdAndDate(clubId, date);
        if (workScheduleException.isPresent()) {
            boolean isWorkDay = workScheduleException.get().isWorkDay();
            if (!isWorkDay) {
                return List.of();
            }
            LocalTime exceptionOpenTime = workScheduleException.get().getOpenTime();
            LocalTime exceptionCloseTime = workScheduleException.get().getCloseTime();
            dayStart = OffsetDateTime.of(date, exceptionOpenTime, ZoneOffset.UTC);
            dayEnd = OffsetDateTime.of(date, exceptionCloseTime, ZoneOffset.UTC);
        }
        // учет текущего времени пользователя
        if (userCurrentDate.equals(date)) {
            OffsetDateTime userCurrentDateTime = OffsetDateTime.of(date, userCurrentTime, ZoneOffset.UTC);
            dayStart = userCurrentDateTime.isBefore(dayStart) ? dayStart : userCurrentDateTime;
        }
        // округление до кратного 15 минут вверх
        int minutes = dayStart.getMinute();
        int mod = minutes % 15;
        if (mod != 0) {
            dayStart = dayStart.plusMinutes(15 - mod);
        }
        dayStart = dayStart.withSecond(0).withNano(0);
        OffsetDateTime slotStart = dayStart;
        if (dayStart.isAfter(dayEnd)) {
            return List.of();
        }
        List<FreeBookingSlotDto> freeBookingSlots = new ArrayList<>();
        while (!slotStart.plusMinutes(durationMinutes).isAfter(dayEnd)) {
            OffsetDateTime slotEnd = slotStart.plusMinutes(durationMinutes);
            int usedCntEquipments = calculateUsedCntEquipments(bookings, slotStart, slotEnd);
            if (club.getCntEquipment() - usedCntEquipments >= cntEquipment) {
                freeBookingSlots.add(new FreeBookingSlotDto(slotStart, slotEnd));
            }
            slotStart = slotStart.plusMinutes(15);
        }
        return freeBookingSlots;
    }

    /**
     * Получение списка всех бронирований клиента за диапазон дат
     */
    @Transactional(readOnly = true)
    public List<ClientBookingShortDto> getAllClientBookings(long clientId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException();
        }
        clientSearchService.findClientById(clientId);
        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        List<Booking> bookings = bookingRepository.findAllByDateRangeAndClientId(clientId, start, end);
        return bookingMapper.toClientBookingShortDto(bookings, clubMapper, cityMapper, regionMapper);
    }

    /**
     * Получение списка всех бронирований клиента за диапазон дат с пагинацией
     */
    @Transactional(readOnly = true)
    public PagedClientBookingShortListDto getAllClientBookingsPaged(
            long clientId,
            LocalDate startDate,
            LocalDate endDate,
            int pageNumber,
            int pageSize
    ) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException();
        }
        clientSearchService.findClientByIdOrThrowCustomException(clientId, new UserNotFoundFromTokenException());
        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(START_DATE_TIME));
        Page<Booking> bookingsPage = bookingRepository.findAllByDateRangeAndClientIdPaged(
                clientId,
                start,
                end,
                pageRequest
        );
        List<ClientBookingShortDto> content = bookingMapper.toClientBookingShortDto(
                bookingsPage.getContent(),
                clubMapper,
                cityMapper,
                regionMapper
        );
        long totalElements = bookingsPage.getTotalElements();
        int totalPages = bookingsPage.getTotalPages();
        boolean isLast = bookingsPage.isLast();
        PageInfoDto pageInfoDto = new PageInfoDto(pageNumber, pageSize, totalElements, totalPages, isLast);
        return new PagedClientBookingShortListDto(content, pageInfoDto);
    }

    /**
     * Просмотр полной информации о бронировании для клиента
     */
    @Transactional(readOnly = true)
    public ClientBookingFullInfoDto getBookingFullInfoForClient(long clientId, int clubId, long bookingId) {
        clientSearchService.findClientByIdOrThrowCustomException(clientId, new UserNotFoundFromTokenException());
        Club club = clubSearchService.findClubById(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование не найдено в клубе")
                );
        return bookingMapper.toClientBookingFullInfoDto(booking, clientMapper, clubMapper, cityMapper, regionMapper);
    }

    /**
     * Получение списка всех бронирований за диапазон дат и по номеру телефона клиента (номер телефона опционален)
     * для сотрудника
     */
    @Transactional(readOnly = true)
    public List<EmployeeBookingShortDto> getBookingsForEmployee(
            long employeeId,
            int clubId,
            LocalDate startDate,
            LocalDate endDate,
            Optional<String> clientPhone
    ) {
        clubSearchService.findEmployeeById(employeeId);
        Club club = clubSearchService.findClubById(clubId);
        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        List<Booking> bookings;
        if (clientPhone.isPresent()) {
            bookings = bookingRepository
                    .findAllByDateRangeAndClubIdAndClientPhone(club.getId(), start, end, clientPhone.get());
        } else {
            bookings = bookingRepository.findAllByDateRangeAndClubId(club.getId(), start, end);
        }
        return bookingMapper.toEmployeeBookingShortDto(bookings);
    }

    /**
     * Получение списка всех бронирований за диапазон дат и по номеру телефона клиента (номер телефона опционален)
     * для сотрудника с пагинацией
     */
    @Transactional(readOnly = true)
    public PagedEmployeeBookingShortListDto getBookingsForEmployeePaged(
            long employeeId,
            int clubId,
            LocalDate startDate,
            LocalDate endDate,
            Optional<String> clientPhone,
            int pageNumber,
            int pageSize
    ) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException();
        }
        clubSearchService.findEmployeeByIdOrThrowCustomException(employeeId, new UserNotFoundFromTokenException());
        Club club = clubSearchService.findClubById(clubId);
        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(START_DATE_TIME));
        Page<Booking> bookingsPage;
        if (clientPhone.isPresent()) {
            bookingsPage = bookingRepository.findAllByDateRangeAndClubIdAndClientPhonePaged(
                    club.getId(),
                    start,
                    end,
                    clientPhone.get(),
                    pageRequest
            );
        } else {
            bookingsPage = bookingRepository.findAllByDateRangeAndClubIdPaged(
                    club.getId(),
                    start,
                    end,
                    pageRequest
            );
        }
        List<EmployeeBookingShortDto> content = bookingMapper.toEmployeeBookingShortDto(bookingsPage.getContent());
        long totalElements = bookingsPage.getTotalElements();
        int totalPages = bookingsPage.getTotalPages();
        boolean isLast = bookingsPage.isLast();
        PageInfoDto pageInfoDto = new PageInfoDto(pageNumber, pageSize, totalElements, totalPages, isLast);
        return new PagedEmployeeBookingShortListDto(content, pageInfoDto);
    }

    /**
     * Просмотр полной информации о бронировании для сотрудника
     */
    @Transactional(readOnly = true)
    public EmployeeBookingFullInfoDto getBookingFullInfoForEmployee(long employeeId, int clubId, long bookingId) {
        clubSearchService.findEmployeeByIdOrThrowCustomException(employeeId, new UserNotFoundFromTokenException());
        Club club = clubSearchService.findClubById(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование не найдено в клубе")
                );
        return bookingMapper.toEmployeeBookingFullInfoDto(booking, clientMapper);
    }
}