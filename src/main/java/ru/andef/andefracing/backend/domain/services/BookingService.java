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
import ru.andef.andefracing.backend.data.repositories.club.BookingRepository;
import ru.andef.andefracing.backend.data.repositories.club.WorkScheduleExceptionRepository;
import ru.andef.andefracing.backend.domain.exceptions.booking.BookingIntersectionException;
import ru.andef.andefracing.backend.domain.exceptions.booking.InvalidBookingSlotException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.domain.mappers.club.BookingMapper;
import ru.andef.andefracing.backend.domain.mappers.club.ClubMapper;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotDto;
import ru.andef.andefracing.backend.network.dtos.booking.FreeBookingSlotsRequestDto;
import ru.andef.andefracing.backend.network.dtos.booking.MakeBookingDto;
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
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final SearchService searchService;

    private final BookingRepository bookingRepository;
    private final WorkScheduleExceptionRepository workScheduleExceptionRepository;

    private final BookingMapper bookingMapper;
    private final ClubMapper clubMapper;
    private final CityMapper cityMapper;
    private final ClientMapper clientMapper;
    private final RegionMapper regionMapper;

    /**
     * Проверка, что клуб открыт
     */
    private void checkClubOpen(Club club) {
        if (!club.isOpen()) {
            throw new EntityNotFoundException("Клуб закрыт");
        }
    }

    /**
     * Проверка, что дата окончания позже даты начала
     */
    private void checkStartAndEndDateTime(OffsetDateTime start, OffsetDateTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidBookingSlotException("Время начала должно быть раньше времени окончания");
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
     * Проверка верности указанной стоимости
     */
    private void checkPrice(
            Club club,
            OffsetDateTime start,
            OffsetDateTime end,
            short cntEquipment,
            BigDecimal price
    ) {
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
    }

    /**
     * Сделать бронирование (общий метод для сотрудников и клиентов)
     */
    private void makeBooking(
            Club club,
            OffsetDateTime start,
            OffsetDateTime end,
            MakeBookingDto makeBookingDto,
            Supplier<Booking> makeBookingCallback
    ) {
        short cntEquipment = makeBookingDto.getCntEquipment();
        BigDecimal price = makeBookingDto.getPrice().setScale(2, RoundingMode.HALF_EVEN);
        checkPrice(club, start, end, cntEquipment, price);
        checkClubOpen(club);
        if (start.isBefore(OffsetDateTime.now())) {
            throw new InvalidBookingSlotException("Нельзя создать бронирование в прошлом");
        }
        Booking booking = makeBookingCallback.get();
        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new BookingIntersectionException();
        }
    }

    /**
     * Получение доступных слотов для бронирования
     */
    @Transactional(readOnly = true)
    public List<FreeBookingSlotDto> getFreeBookingSlotsInClub(
            int clubId,
            FreeBookingSlotsRequestDto freeBookingSlotsRequestDto
    ) {
        Club club = searchService.findClubById(clubId);
        checkClubOpen(club);
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
     * Сделать бронирование от лица клиента
     */
    @Transactional
    public void makeClientBooking(long clientId, int clubId, ClientMakeBookingDto clientMakeBookingDto) {
        OffsetDateTime start = clientMakeBookingDto.getSlot().startDateTime();
        OffsetDateTime end = clientMakeBookingDto.getSlot().endDateTime();
        checkStartAndEndDateTime(start, end);
        Client client = searchService.findClientById(clientId);
        Club club = searchService.findClubById(clubId);
        short cntEquipment = clientMakeBookingDto.getCntEquipment();
        BigDecimal price = clientMakeBookingDto.getPrice().setScale(2, RoundingMode.HALF_EVEN);
        makeBooking(
                club,
                start,
                end,
                clientMakeBookingDto,
                () -> client.makeBooking(club, start, end, cntEquipment, price)
        );
    }

    /**
     * Получение списка всех бронирований клиента за диапазон дат
     */
    @Transactional(readOnly = true)
    public List<ClientBookingShortDto> getAllClientBookings(long clientId, LocalDate startDate, LocalDate endDate) {
        searchService.findClientById(clientId);
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
        searchService.findClientById(clientId);
        Club club = searchService.findClubById(clubId);
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
        Employee employee = searchService.findEmployeeById(employeeId);
        Club club = searchService.findClubById(clubId);
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
        checkStartAndEndDateTime(start, end);
        Employee employee = searchService.findEmployeeById(employeeId);
        Club club = searchService.findClubById(clubId);
        short cntEquipment = employeeMakeBookingDto.getCntEquipment();
        BigDecimal price = employeeMakeBookingDto.getPrice().setScale(2, RoundingMode.HALF_EVEN);
        makeBooking(
                club,
                start,
                end,
                employeeMakeBookingDto,
                () -> employee.makeBooking(club, start, end, cntEquipment, price)
        );
    }

    /**
     * Отмена бронирования сотрудником
     */
    @Transactional
    public void cancelBookingByEmployee(long employeeId, int clubId, long bookingId) {
        Employee employee = searchService.findEmployeeById(employeeId);
        Club club = searchService.findClubById(clubId);
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
        searchService.findEmployeeById(employeeId);
        Club club = searchService.findClubById(clubId);
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
        searchService.findEmployeeById(employeeId);
        Club club = searchService.findClubById(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        return bookingMapper.toEmployeeBookingFullInfoDto(booking, clientMapper);
    }
}
