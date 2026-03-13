package ru.andef.andefracing.backend.domain.services.booking;

import lombok.RequiredArgsConstructor;
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
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingSearchService extends BookingService {
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
            FreeBookingSlotsRequestDto freeBookingSlotsRequestDto
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
        OffsetDateTime slotStart = dayStart;
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
        clientSearchService.findClientById(clientId);
        OffsetDateTime start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        List<Booking> bookings = bookingRepository.findAllByDateRangeAndClientId(clientId, start, end);
        return bookingMapper.toClientBookingShortDto(bookings, clubMapper, cityMapper, regionMapper);
    }

    /**
     * Просмотр полной информации о бронировании для клиента
     */
    @Transactional(readOnly = true)
    public ClientBookingFullInfoDto getBookingFullInfoForClient(long clientId, int clubId, long bookingId) {
        clientSearchService.findClientById(clientId);
        Club club = clubSearchService.findClubById(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        return bookingMapper.toClientBookingFullInfoDto(booking, clientMapper);
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
     * Просмотр полной информации о бронировании для сотрудника
     */
    @Transactional(readOnly = true)
    public EmployeeBookingFullInfoDto getBookingFullInfoForEmployee(long employeeId, int clubId, long bookingId) {
        clubSearchService.findEmployeeById(employeeId);
        Club club = clubSearchService.findClubById(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        return bookingMapper.toEmployeeBookingFullInfoDto(booking, clientMapper);
    }
}