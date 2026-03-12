package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.entities.club.booking.BookingStatus;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.BookingRepository;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
import ru.andef.andefracing.backend.data.repositories.club.WorkScheduleExceptionRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.booking.BookingIntersectionException;
import ru.andef.andefracing.backend.domain.exceptions.booking.InvalidBookingSlotException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.domain.mappers.club.BookingMapper;
import ru.andef.andefracing.backend.domain.mappers.club.ClubMapper;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientMakeBookingDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeMakeBookingDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final ClientRepository clientRepository;
    private final ClubRepository clubRepository;
    private final EmployeeRepository employeeRepository;
    private final BookingRepository bookingRepository;
    private final WorkScheduleExceptionRepository workScheduleExceptionRepository;

    private final BookingMapper bookingMapper;
    private final ClubMapper clubMapper;
    private final CityMapper cityMapper;
    private final ClientMapper clientMapper;
    private final RegionMapper regionMapper;

    /**
     * Получение клуба по id или выброс исключения
     */
    private Club findClubByIdOrThrow(int clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Клуб с id " + clubId + " не найден"));
    }

    /**
     * Получение клиента по id или выброс исключения
     */
    private Client findClientByIdOrThrow(long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с id " + clientId + " не найден"));
    }

    /**
     * Получение сотрудника по id или выброс исключения
     */
    private Employee findEmployeeByIdOrThrow(long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с id " + employeeId + " не найден"));
    }

    /**
     * Проверка, что клуб открыт
     */
    private void checkClubOpen(Club club) {
        if (!club.isOpen()) {
            throw new EntityNotFoundException("Клуб закрыт");
        }
    }

    /**
     * Проверка, что бронирование не в прошлом
     */
    private void checkBookingNotInPast(OffsetDateTime start) {
        if (start.isBefore(OffsetDateTime.now())) {
            throw new InvalidBookingSlotException("Нельзя создать бронирование в прошлом");
        }
    }

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
        Club club = findClubByIdOrThrow(clubId);
        checkClubOpen(club);
        LocalDate date = freeBookingSlotsRequestDto.date();
        short durationMinutes = freeBookingSlotsRequestDto.durationMinutes();
        short cntEquipment = freeBookingSlotsRequestDto.cntEquipment();
        OffsetDateTime dayStart = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime dayEnd = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        List<Booking> bookings = bookingRepository.findAllByDateRangeAndClubId(clubId, dayStart, dayEnd);
        // учесть график работы
        for (WorkSchedule workSchedule : club.getWorkSchedules()) {
            if (workSchedule.getDayOfWeek() == date.getDayOfWeek().getValue()) {
                if (!workSchedule.isWorkDay()) {
                    return List.of();
                }
                dayStart = OffsetDateTime.of(date, workSchedule.getOpenTime(), ZoneOffset.UTC);
                dayEnd = OffsetDateTime.of(date, workSchedule.getCloseTime(), ZoneOffset.UTC);
            }
        }
        // учесть дни-исключения
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
     * Сделать бронирование от лица клиента
     */
    @Transactional
    public void makeClientBooking(long clientId, int clubId, ClientMakeBookingDto clientMakeBookingDto) {
        OffsetDateTime start = clientMakeBookingDto.getSlot().startDateTime();
        OffsetDateTime end = clientMakeBookingDto.getSlot().endDateTime();
        if (!start.isBefore(end)) {
            throw new InvalidBookingSlotException("Время начала должно быть раньше времени окончания");
        }
        Client client = findClientByIdOrThrow(clientId);
        Club club = findClubByIdOrThrow(clubId);
        short cntEquipment = clientMakeBookingDto.getCntEquipment();
        BigDecimal price = clientMakeBookingDto.getPrice().setScale(2, RoundingMode.HALF_EVEN);
        short durationMinutes = (short) Duration.between(start, end).toMinutes();
        BigDecimal expectedPrice = null;
        for (Price priceInClub : club.getPrices()) {
            if (priceInClub.getDurationMinutes() == durationMinutes) {
                expectedPrice = priceInClub.getValue().multiply(BigDecimal.valueOf(cntEquipment));
                expectedPrice = expectedPrice.setScale(2, RoundingMode.HALF_EVEN);
            }
        }
        if (expectedPrice == null) {
            throw new EntityNotFoundException("Цены для кол-ва минут: " + durationMinutes + " нет в клубе");
        } else if (expectedPrice.compareTo(price) != 0) {
            throw new InvalidBookingSlotException("Указана некорректная цена");
        }
        checkClubOpen(club);
        checkBookingNotInPast(start);
        Booking booking = client.makeBooking(club, start, end, cntEquipment, price);
        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new BookingIntersectionException();
        }
    }

    /**
     * Получение списка всех бронирований клиента за диапазон дат
     */
    @Transactional(readOnly = true)
    public List<ClientBookingShortDto> getAllClientBookings(long clientId, LocalDate startDate, LocalDate endDate) {
        findClientByIdOrThrow(clientId);
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
        findClientByIdOrThrow(clientId);
        Club club = findClubByIdOrThrow(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        return bookingMapper.toClientBookingFullInfoDto(booking, clientMapper);
    }

    /**
     * Подтверждение оплаты бронирования сотрудником
     */
    @Transactional
    public void confirmBookingPaymentByEmployee(long employeeId, int clubId, long bookingId) {
        Employee employee = findEmployeeByIdOrThrow(employeeId);
        Club club = findClubByIdOrThrow(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        employee.confirmBookingPayment(booking);
        bookingRepository.save(booking);
    }

    /**
     * Сделать бронирование от лица сотрудника
     */
    @Transactional
    public void makeEmployeeBooking(long employeeId, int clubId, EmployeeMakeBookingDto employeeMakeBookingDto) {
        OffsetDateTime start = employeeMakeBookingDto.getSlot().startDateTime();
        OffsetDateTime end = employeeMakeBookingDto.getSlot().endDateTime();
        if (!start.isBefore(end)) {
            throw new InvalidBookingSlotException("Время начала должно быть раньше времени окончания");
        }
        Employee employee = findEmployeeByIdOrThrow(employeeId);
        Club club = findClubByIdOrThrow(clubId);
        short cntEquipment = employeeMakeBookingDto.getCntEquipment();
        BigDecimal price = employeeMakeBookingDto.getPrice().setScale(2, RoundingMode.HALF_EVEN);
        short durationMinutes = (short) Duration.between(start, end).toMinutes();
        BigDecimal expectedPrice = null;
        for (Price priceInClub : club.getPrices()) {
            if (priceInClub.getDurationMinutes() == durationMinutes) {
                expectedPrice = priceInClub.getValue().multiply(BigDecimal.valueOf(cntEquipment));
                expectedPrice = expectedPrice.setScale(2, RoundingMode.HALF_EVEN);
            }
        }
        if (expectedPrice == null) {
            throw new EntityNotFoundException("Цены для кол-ва минут: " + durationMinutes + " нет в клубе");
        } else if (expectedPrice.compareTo(price) != 0) {
            throw new InvalidBookingSlotException("Указана некорректная цена");
        }
        checkClubOpen(club);
        checkBookingNotInPast(start);
        Booking booking = employee.makeBooking(club, start, end, cntEquipment, price);
        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new BookingIntersectionException();
        }
    }

    /**
     * Отмена бронирования сотрудником
     */
    @Transactional
    public void cancelBookingByEmployee(long employeeId, int clubId, long bookingId) {
        Employee employee = findEmployeeByIdOrThrow(employeeId);
        Club club = findClubByIdOrThrow(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        employee.cancelBooking(booking);
        bookingRepository.save(booking);
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
        findEmployeeByIdOrThrow(employeeId);
        Club club = findClubByIdOrThrow(clubId);
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
        findEmployeeByIdOrThrow(employeeId);
        Club club = findClubByIdOrThrow(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        return bookingMapper.toEmployeeBookingFullInfoDto(booking, clientMapper);
    }
}
