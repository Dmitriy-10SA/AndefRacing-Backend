package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.*;
import ru.andef.andefracing.backend.data.entities.club.booking.Booking;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.client.ClientBookingShortDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.booking.employee.EmployeeBookingShortDto;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ClubMapper.class, CityMapper.class, ClientMapper.class, RegionMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BookingMapper {
    @Mapping(target = "id", expression = "java(booking.getId())")
    @Mapping(target = "startDateTime", expression = "java(booking.getStartDateTime())")
    @Mapping(target = "endDateTime", expression = "java(booking.getEndDateTime())")
    @Mapping(target = "status", expression = "java(booking.getStatus())")
    EmployeeBookingShortDto toEmployeeBookingShortDto(Booking booking);

    List<EmployeeBookingShortDto> toEmployeeBookingShortDto(List<Booking> bookings);

    @Mapping(target = "id", expression = "java(booking.getId())")
    @Mapping(target = "startDateTime", expression = "java(booking.getStartDateTime())")
    @Mapping(target = "endDateTime", expression = "java(booking.getEndDateTime())")
    @Mapping(target = "status", expression = "java(booking.getStatus())")
    @Mapping(target = "club", expression = "java(clubMapper.toShortDto(booking.getClub()))")
    @Mapping(target = "city", expression = "java(cityMapper.toDto(booking.getClub().getCity(), regionMapper))")
    ClientBookingShortDto toClientBookingShortDto(
            Booking booking,
            @Context ClubMapper clubMapper,
            @Context CityMapper cityMapper,
            @Context RegionMapper regionMapper
    );

    List<ClientBookingShortDto> toClientBookingShortDto(
            List<Booking> bookings,
            @Context ClubMapper clubMapper,
            @Context CityMapper cityMapper,
            @Context RegionMapper regionMapper
    );

    @Mapping(target = "id", expression = "java(booking.getId())")
    @Mapping(target = "startDateTime", expression = "java(booking.getStartDateTime())")
    @Mapping(target = "endDateTime", expression = "java(booking.getEndDateTime())")
    @Mapping(target = "status", expression = "java(booking.getStatus())")
    @Mapping(target = "cntEquipment", expression = "java(booking.getCntEquipment())")
    @Mapping(target = "price", expression = "java(booking.getPriceValue())")
    @Mapping(target = "note", expression = "java(booking.getNote())")
    @Mapping(target = "client", expression = "java(clientMapper.toDto(booking.getClient()))")
    EmployeeBookingFullInfoDto toEmployeeBookingFullInfoDto(Booking booking, @Context ClientMapper clientMapper);

    @Mapping(target = "id", expression = "java(booking.getId())")
    @Mapping(target = "startDateTime", expression = "java(booking.getStartDateTime())")
    @Mapping(target = "endDateTime", expression = "java(booking.getEndDateTime())")
    @Mapping(target = "status", expression = "java(booking.getStatus())")
    @Mapping(target = "club", expression = "java(clubMapper.toShortDto(booking.getClub()))")
    @Mapping(target = "city", expression = "java(cityMapper.toDto(booking.getClub().getCity(), regionMapper))")
    @Mapping(target = "cntEquipment", expression = "java(booking.getCntEquipment())")
    @Mapping(target = "price", expression = "java(booking.getPriceValue())")
    @Mapping(target = "note", expression = "java(booking.getNote())")
    ClientBookingFullInfoDto toClientBookingFullInfoDto(
            Booking booking,
            @Context ClientMapper clientMapper,
            @Context ClubMapper clubMapper,
            @Context CityMapper cityMapper,
            @Context RegionMapper regionMapper
    );
}
