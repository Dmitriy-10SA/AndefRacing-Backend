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
    @Mapping(target = "id", expression = "java(workSchedule.getId())")
    @Mapping(target = "dayOfWeek", expression = "java(workSchedule.getDayOfWeek())")
    @Mapping(target = "openTime", expression = "java(workSchedule.getOpenTime())")
    @Mapping(target = "closeTime", expression = "java(workSchedule.getCloseTime())")
    @Mapping(target = "isWorkDay", expression = "java(workSchedule.isWorkDay())")
    WorkScheduleDto toDto(WorkSchedule workSchedule);

    List<WorkScheduleDto> toDto(List<WorkSchedule> workSchedules);
}
