package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeePersonalInfoDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmployeeMapper {
    @Mapping(target = "phone", expression = "java(employee.getPhone())")
    @Mapping(target = "name", expression = "java(employee.getName())")
    @Mapping(target = "surname", expression = "java(employee.getSurname())")
    @Mapping(target = "patronymic", expression = "java(employee.getPatronymic())")
    @Mapping(target = "roles", expression = "java(roles)")
    EmployeePersonalInfoDto toPersonalInfo(Employee employee, List<EmployeeRole> roles);
}
