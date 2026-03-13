package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.*;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.mappers.club.*;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubInfoDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.RegionShortDto;
import ru.andef.andefracing.backend.network.dtos.search.ClubFullInfoDto;
import ru.andef.andefracing.backend.network.dtos.search.PagedClubShortListDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final String NAME = "name";

    private final ClubRepository clubRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final GameRepository gameRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final PhotoRepository photoRepository;
    private final PriceRepository priceRepository;

    private final ClubMapper clubMapper;
    private final RegionMapper regionMapper;
    private final CityMapper cityMapper;
    private final PhotoMapper photoMapper;
    private final GameMapper gameMapper;
    private final PriceMapper priceMapper;
    private final WorkScheduleMapper workScheduleMapper;

    /**
     * Проверка, заблокирован ли клиент
     */
    private void checkClientIsBlocked(Client client) {
        if (client.isBlocked()) {
            throw new BlockedException("Клиент заблокирован");
        }
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
     * Получение цены по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Price findPriceById(long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Цена с id " + id + " не найдена"));
    }

    /**
     * Получение фото по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Photo findPhotoById(long photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Фото с id " + photoId + " не найдено"));
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
     * Получение клиента по номеру телефона или выброс кастомного исключения
     */
    @Transactional(readOnly = true)
    public Client findClientByPhone(String phone, RuntimeException exception) {
        Client client = clientRepository.findByPhone(phone).orElseThrow(() -> exception);
        checkClientIsBlocked(client);
        return client;
    }

    /**
     * Получение клиента по номеру телефона или выброс исключения
     */
    @Transactional(readOnly = true)
    public Client findClientByPhone(String phone) {
        Client client = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с телефоном " + phone + " не найден"));
        checkClientIsBlocked(client);
        return client;
    }

    /**
     * Получение клиента по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Client findClientById(long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с id " + id + " не найден"));
        checkClientIsBlocked(client);
        return client;
    }

    /**
     * Получение сотрудника по номеру телефона или выброс исключения
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeByPhone(String phone) {
        Employee employee = employeeRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с телефоном " + phone + " не найден"));
        checkEmployeeIsBlocked(employee);
        return employee;
    }

    /**
     * Получение сотрудника по номеру телефона или выброс кастомного исключения
     */
    @Transactional(readOnly = true)
    public Employee findEmployeeByPhone(String phone, RuntimeException exception) {
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
     * Получение региона по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public Region findRegionById(short id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Регион с id " + id + " не найден"));
    }

    /**
     * Получение города по id или выброс исключения
     */
    @Transactional(readOnly = true)
    public City findCityById(short id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Город с id " + id + " не найден"));
    }

    /**
     * Получение всех регионов в которых есть открытые клубы
     */
    @Transactional(readOnly = true)
    public List<RegionShortDto> getAllRegionsWithOpenClubs() {
        List<Region> regions = regionRepository.findAllRegionsWithOpenClubs();
        return regionMapper.toShortDto(regions);
    }

    /**
     * Получение всех городов в указанном регионе, в которых есть открытые клубы
     */
    @Transactional(readOnly = true)
    public List<CityShortDto> getAllCitiesInRegionWithOpenClubs(short regionId) {
        Region region = findRegionById(regionId);
        List<City> cities = cityRepository.findAllCitiesInRegionWithOpenClubs(region.getId());
        return cityMapper.toShortDto(cities);
    }

    /**
     * Получение всех клубов (работающих) в указанном городе с пагинацией
     */
    @Transactional(readOnly = true)
    public PagedClubShortListDto getAllOpenClubsInCity(short cityId, int pageNumber, int pageSize) {
        City city = findCityById(cityId);
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