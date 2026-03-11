package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkSchedule;
import ru.andef.andefracing.backend.network.dtos.search.WorkScheduleDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface WorkScheduleMapper {
    @Mapping(target = "id", expression = "java(game.getId())")
    @Mapping(target = "dayOfWeek", expression = "java(game.getDayOfWeek())")
    @Mapping(target = "openTime", expression = "java(game.getOpenTime())")
    @Mapping(target = "closeTime", expression = "java(game.getCloseTime())")
    @Mapping(target = "isWorkDay", expression = "java(game.isWorkDay())")
    WorkScheduleDto toDto(WorkSchedule workSchedule);

    List<WorkScheduleDto> toDto(List<WorkSchedule> workSchedules);
}
