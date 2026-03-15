package ru.andef.andefracing.backend.domain.services.club.management;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeClub;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
import ru.andef.andefracing.backend.domain.exceptions.DuplicateException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.management.EmployeeWithThisPhoneAlreadyExistsException;
import ru.andef.andefracing.backend.domain.mappers.club.EmployeeMapper;
import ru.andef.andefracing.backend.domain.services.search.ClubSearchService;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddExistingEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.AddNewEmployeeDto;
import ru.andef.andefracing.backend.network.dtos.management.hr.EmployeeAndRolesDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClubHrManagementService {
    private final ClubSearchService clubSearchService;

    private final ClubRepository clubRepository;
    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    /**
     * Получение ролей сотрудника в клубе (вспомогательный метод)
     */
    private List<EmployeeRole> findEmployeeRolesInClub(Club club, Employee employee) {
        List<EmployeeRole> rolesInClub = new ArrayList<>();
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (employeeClub.getEmployee().equals(employee)) {
                rolesInClub.add(employeeClub.getEmployeeRole());
            }
        }
        return rolesInClub;
    }

    /**
     * Удаление роли у сотрудника в клубе (вспомогательный метод)
     */
    private void deleteEmployeeRoleInClub(Club club, Employee employee, EmployeeRole role) {
        List<EmployeeRole> rolesInClub = findEmployeeRolesInClub(club, employee);
        if (rolesInClub.isEmpty()) {
            throw new EntityNotFoundException("Сотрудник c id " + employee.getId() + " не найден в клубе");
        } else if (!rolesInClub.contains(role)) {
            throw new EntityNotFoundException("У сотрудника с id " + employee.getId() + " нет роли " + role.getRu());
        }
        club.deleteRoleForEmployee(employee, role);
    }

    /**
     * Проверка, что сотрудник есть в системе
     */
    @Transactional(readOnly = true)
    public boolean isEmployeeInSystem(String phone) {
        return employeeRepository.findByPhone(phone).isPresent();
    }

    /**
     * Добавление сотрудника, которого нет в системе, в выбранный текущим клуб
     * по номеру телефона сотрудника с заданием ролей
     */
    @Transactional
    public void addNewEmployeeToClub(int clubId, AddNewEmployeeDto addNewEmployeeDto) {
        if (employeeRepository.existsByPhone(addNewEmployeeDto.getPhone())) {
            throw new EmployeeWithThisPhoneAlreadyExistsException(addNewEmployeeDto.getPhone());
        }
        Club club = clubSearchService.findClubById(clubId);
        Employee employee = new Employee(
                addNewEmployeeDto.getSurname(),
                addNewEmployeeDto.getName(),
                addNewEmployeeDto.getPatronymic(),
                addNewEmployeeDto.getPhone()
        );
        employee = employeeRepository.save(employee);
        club.addEmployee(employee, addNewEmployeeDto.getRoles());
        clubRepository.save(club);
    }

    /**
     * Добавление сотрудника, который уже есть в системе, в выбранный текущим клуб
     * по номеру телефона сотрудника с заданием ролей
     */
    @Transactional
    public void addExistingEmployeeToClub(int clubId, AddExistingEmployeeDto addExistingEmployeeDto) {
        Club club = clubSearchService.findClubById(clubId);
        Employee employee = clubSearchService
                .findEmployeeByPhoneWithoutPasswordNotSetException(addExistingEmployeeDto.getPhone());
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (employeeClub.getEmployee().equals(employee)) {
                throw new DuplicateException("Сотрудник с id " + employee.getId() + " уже есть в клубе");
            }
        }
        club.addEmployee(employee, addExistingEmployeeDto.getRoles());
        clubRepository.save(club);
    }

    /**
     * Получение списка сотрудников и их ролей в клубе
     */
    @Transactional(readOnly = true)
    public List<EmployeeAndRolesDto> getEmployeesAndRolesInClub(int clubId) {
        Club club = clubSearchService.findClubById(clubId);
        Map<Employee, List<EmployeeRole>> employeeAndRolesMap = new HashMap<>();
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (!employeeAndRolesMap.containsKey(employeeClub.getEmployee())) {
                employeeAndRolesMap.put(employeeClub.getEmployee(), new ArrayList<>());
            }
            employeeAndRolesMap.get(employeeClub.getEmployee()).add(employeeClub.getEmployeeRole());
        }
        List<EmployeeAndRolesDto> employeeAndRoles = new ArrayList<>();
        for (Map.Entry<Employee, List<EmployeeRole>> entry : employeeAndRolesMap.entrySet()) {
            employeeAndRoles.add(new EmployeeAndRolesDto(employeeMapper.toDto(entry.getKey()), entry.getValue()));
        }
        return employeeAndRoles;
    }

    /**
     * Удаление сотрудника из выбранного текущим клуба
     */
    @Transactional
    public void deleteEmployeeFromClub(int clubId, long employeeId) {
        Club club = clubSearchService.findClubById(clubId);
        Employee employee = clubSearchService.findEmployeeByIdWithoutPasswordNotSetException(employeeId);
        boolean isExistingInClub = false;
        for (EmployeeClub employeeClub : club.getEmployeesAndRoles()) {
            if (employeeClub.getEmployee().equals(employee)) {
                isExistingInClub = true;
                break;
            }
        }
        if (!isExistingInClub) {
            throw new EntityNotFoundException("Сотрудник c id " + employeeId + " не найден в клубе");
        }
        club.deleteEmployee(employee);
        clubRepository.save(club);
    }

    /**
     * Добавить роль сотруднику в выбранном текущим клубе
     */
    @Transactional
    public void addRoleToEmployeeInClub(int clubId, long employeeId, EmployeeRole role) {
        Club club = clubSearchService.findClubById(clubId);
        Employee employee = clubSearchService.findEmployeeByIdWithoutPasswordNotSetException(employeeId);
        List<EmployeeRole> rolesInClub = findEmployeeRolesInClub(club, employee);
        if (rolesInClub.isEmpty()) {
            throw new EntityNotFoundException("Сотрудник c id " + employeeId + " не найден в клубе");
        } else if (rolesInClub.contains(role)) {
            throw new DuplicateException("Сотрудник с id " + employeeId + " уже имеет роль " + role.getRu());
        }
        club.addRoleForEmployee(employee, role);
        clubRepository.save(club);
    }

    /**
     * Изменить роль сотрудника в выбранном текущем клубе
     */
    @Transactional
    public void updateEmployeeRoleInClub(int clubId, long employeeId, EmployeeRole oldRole, EmployeeRole newRole) {
        if (oldRole.equals(EmployeeRole.EMPLOYEE)) {
            throw new IllegalArgumentException("Нельзя изменить роль EMPLOYEE на другую роль");
        }
        Club club = clubSearchService.findClubById(clubId);
        Employee employee = clubSearchService.findEmployeeByIdWithoutPasswordNotSetException(employeeId);
        deleteEmployeeRoleInClub(club, employee, oldRole);
        club.addRoleForEmployee(employee, newRole);
        clubRepository.save(club);
    }

    /**
     * Удалить роль сотрудника в выбранном текущим клубе
     */
    @Transactional
    public void deleteEmployeeRoleInClub(int clubId, long employeeId, EmployeeRole role) {
        Club club = clubSearchService.findClubById(clubId);
        Employee employee = clubSearchService.findEmployeeByIdWithoutPasswordNotSetException(employeeId);
        if (role.equals(EmployeeRole.EMPLOYEE) && findEmployeeRolesInClub(club, employee).size() > 1) {
            throw new IllegalArgumentException(
                    "Нельзя удалить роль EMPLOYEE у сотрудника, у которого есть другие роли"
            );
        }
        deleteEmployeeRoleInClub(club, employee, role);
        clubRepository.save(club);
    }
}