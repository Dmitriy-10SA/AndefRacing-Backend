package ru.andef.andefracing.backend.domain.services.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.repositories.club.BookingRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.booking.InvalidBookingSlotException;
import ru.andef.andefracing.backend.domain.exceptions.booking.NotEnoughSimulatorsException;
import ru.andef.andefracing.backend.domain.services.search.ClientSearchService;
import ru.andef.andefracing.backend.domain.services.search.ClubSearchService;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientMakeBookingDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeMakeBookingDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BookingManagementService {
    private final ClientSearchService clientSearchService;
    private final ClubSearchService clubSearchService;

    private final BookingRepository bookingRepository;

    /**
     * Проверка, что дата окончания позже даты начала
     */
    private void checkStartAndEndDateTime(OffsetDateTime start, OffsetDateTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidBookingSlotException("Время начала должно быть раньше времени окончания");
        }
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
            short cntEquipment,
            BigDecimal price,
            Supplier<Booking> makeBookingCallback
    ) {
        checkPrice(club, start, end, cntEquipment, price);
        if (!club.isOpen()) {
            throw new EntityNotFoundException("Клуб закрыт");
        }
        if (start.isBefore(OffsetDateTime.now())) {
            throw new InvalidBookingSlotException("Нельзя создать бронирование в прошлом");
        }
        Booking booking = makeBookingCallback.get();
        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new NotEnoughSimulatorsException();
        }
    }

    /**
     * Сделать бронирование от лица клиента
     */
    @Transactional
    public void makeClientBooking(long clientId, int clubId, ClientMakeBookingDto clientMakeBookingDto) {
        OffsetDateTime start = clientMakeBookingDto.getSlot().startDateTime();
        OffsetDateTime end = clientMakeBookingDto.getSlot().endDateTime();
        checkStartAndEndDateTime(start, end);
        Client client = clientSearchService.findClientById(clientId);
        Club club = clubSearchService.findClubById(clubId);
        short cntEquipment = clientMakeBookingDto.getCntEquipment();
        BigDecimal price = clientMakeBookingDto.getPrice().setScale(2, RoundingMode.HALF_EVEN);
        makeBooking(
                club,
                start,
                end,
                cntEquipment,
                price,
                () -> client.makeBooking(club, start, end, cntEquipment, price)
        );
    }

    /**
     * Сделать бронирование от лица сотрудника
     */
    @Transactional
    public void makeEmployeeBooking(long employeeId, int clubId, EmployeeMakeBookingDto employeeMakeBookingDto) {
        OffsetDateTime start = employeeMakeBookingDto.getSlot().startDateTime();
        OffsetDateTime end = employeeMakeBookingDto.getSlot().endDateTime();
        checkStartAndEndDateTime(start, end);
        Employee employee = clubSearchService.findEmployeeById(employeeId);
        Club club = clubSearchService.findClubById(clubId);
        short cntEquipment = employeeMakeBookingDto.getCntEquipment();
        BigDecimal price = employeeMakeBookingDto.getPrice().setScale(2, RoundingMode.HALF_EVEN);
        makeBooking(
                club,
                start,
                end,
                cntEquipment,
                price,
                () -> employee.makeBooking(club, start, end, cntEquipment, price)
        );
    }

    /**
     * Подтверждение оплаты бронирования сотрудником
     */
    @Transactional
    public void confirmBookingPaymentByEmployee(long employeeId, int clubId, long bookingId) {
        Employee employee = clubSearchService.findEmployeeById(employeeId);
        Club club = clubSearchService.findClubById(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        employee.confirmBookingPayment(booking);
        bookingRepository.save(booking);
    }

    /**
     * Отмена бронирования сотрудником
     */
    @Transactional
    public void cancelBookingByEmployee(long employeeId, int clubId, long bookingId) {
        Employee employee = clubSearchService.findEmployeeById(employeeId);
        Club club = clubSearchService.findClubById(clubId);
        Booking booking = bookingRepository.findByIdAndClub(bookingId, club)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено в клубе")
                );
        employee.cancelBooking(booking);
        bookingRepository.save(booking);
    }
}