package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.WorkScheduleExceptionDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface WorkScheduleExceptionMapper {
    @Mapping(target = "id", expression = "java(workScheduleException.getId())")
    @Mapping(target = "date", expression = "java(workScheduleException.getDate())")
    @Mapping(target = "openTime", expression = "java(workScheduleException.getOpenTime())")
    @Mapping(target = "closeTime", expression = "java(workScheduleException.getCloseTime())")
    @Mapping(target = "isWorkDay", expression = "java(workScheduleException.isWorkDay())")
    @Mapping(target = "description", expression = "java(workScheduleException.getDescription())")
    WorkScheduleExceptionDto toDto(WorkScheduleException workScheduleException);

    List<WorkScheduleExceptionDto> toDto(List<WorkScheduleException> workScheduleExceptions);
}
