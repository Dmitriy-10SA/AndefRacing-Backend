package ru.andef.andefracing.backend.domain.services.search;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.repositories.club.*;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.PasswordIsNotSetException;
import ru.andef.andefracing.backend.domain.mappers.club.*;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubSearchService {
    private static final String NAME = "name";

    private final LocationSearchService locationSearchService;

    private final PhotoRepository photoRepository;
    private final PriceRepository priceRepository;
    private final GameRepository gameRepository;
    private final EmployeeRepository employeeRepository;
    private final ClubRepository clubRepository;

    private final ClubMapper clubMapper;
    private final PhotoMapper photoMapper;
    private final GameMapper gameMapper;
    private final PriceMapper priceMapper;
    private final WorkScheduleMapper workScheduleMapper;

    /**
     * Получение фото по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Photo findPhotoById(long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фото с id " + id + " не найдено"));
    }

    /**
     * Получение цены по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Price findPriceById(long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Цена с id " + id + " не найдена"));
    }

    /**
     * Получение игры по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Game findGameById(short id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Игра с id " + id + " не найдена"));
    }

    /**
     * Проверка, заблокирован ли сотрудник
     */
    private void checkEmployeeIsBlocked(Employee employee) {
        if (employee.isBlocked()) {
            throw new BlockedException("Сотрудник заблокирован");
        }
    }

    /**
     * Проверка, задан ли пароль у сотрудника
     */
    private void checkEmployeeHasPassword(Employee employee) {
        if (employee.isNeedPassword()) {
            throw new PasswordIsNotSetException("Пароль не задан");
        }
    }

    /**
     * Получение сотрудника по номеру телефона или выброс исключения
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeByPhone(String phone) {
        Employee employee = employeeRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с телефоном " + phone + " не найден"));
        checkEmployeeIsBlocked(employee);
        checkEmployeeHasPassword(employee);
        return employee;
    }

    /**
     * Получение сотрудника по номеру телефона или выброс кастомного исключения
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeByPhone(String phone, RuntimeException exception) {
        Employee employee = employeeRepository.findByPhone(phone).orElseThrow(() -> exception);
        checkEmployeeIsBlocked(employee);
        checkEmployeeHasPassword(employee);
        return employee;
    }

    /**
     * Получение сотрудника по номеру телефона или выброс кастомного исключения, но без проверки задания пароля
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeByPhoneWithoutPasswordNotSetException(String phone) {
        Employee employee = employeeRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с телефоном " + phone + " не найден"));
        checkEmployeeIsBlocked(employee);
        return employee;
    }

    /**
     * Получение сотрудника по номеру телефона или выброс кастомного исключения, но без проверки задания пароля
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeByPhoneWithoutPasswordNotSetException(String phone, RuntimeException exception) {
        Employee employee = employeeRepository.findByPhone(phone).orElseThrow(() -> exception);
        checkEmployeeIsBlocked(employee);
        return employee;
    }

    /**
     * Получение сотрудника по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeById(long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с id " + id + " не найден"));
        checkEmployeeIsBlocked(employee);
        checkEmployeeHasPassword(employee);
        return employee;
    }

    /**
     * Получение сотрудника по id без выброса исключения
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeByIdWithoutPasswordNotSetException(long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с id " + id + " не найден"));
        checkEmployeeIsBlocked(employee);
        return employee;
    }

    /**
     * Получение клуба по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Club findClubById(int id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Клуб с id " + id + " не найден"));
    }

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @Transactional(readOnly = true)
    public PagedClubShortListDto getAllOpenClubsInCity(short cityId, int pageNumber, int pageSize) {
        City city = locationSearchService.findCityById(cityId);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(NAME));
        Page<Club> clubsPage = clubRepository.findAllByCity_IdAndIsOpenTrue(city.getId(), pageRequest);
        List<ClubInfoDto> content = clubMapper.toInfoDto(clubsPage.getContent(), photoMapper);
        PageInfoDto pageInfoDto = new PageInfoDto(
                pageNumber,
                pageSize,
                clubsPage.getTotalElements(),
                clubsPage.getTotalPages(),
                clubsPage.isLast()
        );
        return new PagedClubShortListDto(content, pageInfoDto);
    }

    /**
     * Получение подробной информации о клубе
     */
    @Transactional(readOnly = true)
    public ClubFullInfoDto getClubFullInfo(int clubId) {
        Club club = findClubById(clubId);
        List<Game> games = gameRepository.findAllActiveGamesInClub(clubId);
        return clubMapper.toFullInfoDto(club, games, photoMapper, gameMapper, priceMapper, workScheduleMapper);
    }
}