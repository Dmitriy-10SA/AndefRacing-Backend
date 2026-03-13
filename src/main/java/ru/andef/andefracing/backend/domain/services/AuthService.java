package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeClub;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.EmployeeRepository;
import ru.andef.andefracing.backend.domain.exceptions.auth.ClientWithThisPhoneAlreadyExistsException;
import ru.andef.andefracing.backend.domain.exceptions.auth.InvalidPhoneOrPasswordException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.domain.mappers.club.ClubMapper;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientLoginDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeLoginDto;
import ru.andef.andefracing.backend.network.security.JwtUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SearchService searchService;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    private final ClientMapper clientMapper;
    private final ClubMapper clubMapper;
    private final CityMapper cityMapper;
    private final RegionMapper regionMapper;

    /**
     * Получение ролей (в виде List из String) сотрудника в клубе
     */
    private List<String> getEmployeeRolesInClub(Club club, Employee employee) {
        List<String> roles = club.getEmployeesAndRoles().stream()
                .filter(employeeClub -> employeeClub.getEmployee().equals(employee))
                .map(EmployeeClub::getEmployeeRole)
                .map(EmployeeRole::getRole)
                .toList();
        if (roles.isEmpty()) {
            throw new EntityNotFoundException("Сотрудник не найден в клубе");
        }
        return roles;
    }

    /**
     * Проверка верности пароля или выброс исключения
     */
    private void checkPassword(String password, String passwordHash) {
        if (!passwordEncoder.matches(password, passwordHash)) {
            throw new InvalidPhoneOrPasswordException();
        }
    }

    /**
     * Регистрация в системе для клиента
     */
    @Transactional
    public ClientAuthResponseDto registerClient(ClientRegisterDto registerDto) {
        try {
            searchService.findClientByPhone(registerDto.phone(), new EntityNotFoundException(""));
            throw new ClientWithThisPhoneAlreadyExistsException(registerDto.phone());
        } catch (EntityNotFoundException e) {
            String passwordHash = passwordEncoder.encode(registerDto.password());
            registerDto = new ClientRegisterDto(registerDto.name(), registerDto.phone(), passwordHash);
            Client client = clientMapper.toEntity(registerDto);
            client = clientRepository.save(client);
            String jwt = jwtUtil.generateClientToken(client.getId());
            return new ClientAuthResponseDto(jwt);
        }
    }

    /**
     * Вход в систему для клиента
     */
    @Transactional(readOnly = true)
    public ClientAuthResponseDto loginClient(ClientLoginDto loginDto) {
        Client client = searchService.findClientByPhone(loginDto.getPhone(), new InvalidPhoneOrPasswordException());
        checkPassword(loginDto.getPassword(), client.getPassword());
        String jwt = jwtUtil.generateClientToken(client.getId());
        return new ClientAuthResponseDto(jwt);
    }

    /**
     * Изменение пароля по номеру телефона (без получения СМС, да - плохо)
     */
    @Transactional
    public ClientAuthResponseDto changeClientPassword(ClientChangePasswordDto changePasswordDto) {
        Client client = searchService.findClientByPhone(changePasswordDto.getPhone());
        String passwordHash = passwordEncoder.encode(changePasswordDto.getPassword());
        client.setPassword(passwordHash);
        client = clientRepository.save(client);
        String jwt = jwtUtil.generateClientToken(client.getId());
        return new ClientAuthResponseDto(jwt);
    }

    /**
     * Проверка на первый вход сотрудника
     */
    @Transactional(readOnly = true)
    public boolean isEmployeeFirstEnter(String phone) {
        Employee employee = searchService.findEmployeeByPhone(phone);
        return employee.isNeedPassword();
    }

    /**
     * Подготовительный шаг для входа в систему для сотрудника
     */
    @Transactional
    public List<EmployeeClubDto> preLoginEmployee(EmployeeLoginDto loginDto) {
        Employee employee = searchService
                .findEmployeeByPhone(loginDto.getPhone(), new InvalidPhoneOrPasswordException());
        if (employee.isNeedPassword()) {
            String passwordHash = passwordEncoder.encode(loginDto.getPassword());
            employee.setPassword(passwordHash);
            employeeRepository.save(employee);
        } else {
            checkPassword(loginDto.getPassword(), employee.getPassword());
        }
        List<Club> clubs = employee.getClubAndRoles().stream().map(EmployeeClub::getClub).toList();
        return clubMapper.toEmployeeClubDto(clubs, cityMapper, regionMapper);
    }

    /**
     * Вход в систему для сотрудника
     */
    @Transactional(readOnly = true)
    public EmployeeAuthResponseDto loginEmployee(int clubId, EmployeeLoginDto loginDto) {
        Employee employee = searchService
                .findEmployeeByPhone(loginDto.getPhone(), new InvalidPhoneOrPasswordException());
        checkPassword(loginDto.getPassword(), employee.getPassword());
        Club club = searchService.findClubById(clubId);
        List<String> roles = getEmployeeRolesInClub(club, employee);
        String jwt = jwtUtil.generateEmployeeToken(employee.getId(), club.getId(), club.getName(), roles);
        return new EmployeeAuthResponseDto(jwt);
    }
}