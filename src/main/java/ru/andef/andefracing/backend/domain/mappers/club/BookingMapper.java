package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.*;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ClubMapper.class, CityMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BookingMapper {
    @Mapping(target = "id", expression = "java(booking.getId())")
    @Mapping(target = "date", expression = "java(?)")
    @Mapping(target = "startTime", expression = "java(?)")
    @Mapping(target = "endTime", expression = "java(?)")
    @Mapping(target = "status", expression = "java(booking.getStatus())")
    EmployeeBookingShortDto toEmployeeBookingShortDto(Booking booking);

    List<EmployeeBookingShortDto> toEmployeeBookingShortDto(List<Booking> bookings);

    @Mapping(target = "id", expression = "java(booking.getId())")
    @Mapping(target = "date", expression = "java(?)")
    @Mapping(target = "startTime", expression = "java(?)")
    @Mapping(target = "endTime", expression = "java(?)")
    @Mapping(target = "status", expression = "java(booking.getStatus())")
    @Mapping(target = "club", expression = "java(clubMapper.toDto(booking.getClub()))")
    @Mapping(target = "city", expression = "java(cityMapper.toShortDto(booking.getClub().getCity()))")
    ClientBookingShortDto toClientBookingShortDto(
            Booking booking,
            @Context ClubMapper clubMapper,
            @Context CityMapper cityMapper
    );

    List<ClientBookingShortDto> toClientBookingShortDto(
            List<Booking> bookings,
            @Context ClubMapper clubMapper,
            @Context CityMapper cityMapper
    );

    EmployeeBookingFullInfoDto toEmployeeBookingFullInfoDto(Booking booking);

    ClientBookingFullInfoDto toClientBookingFullInfoDto(Booking booking);
}
