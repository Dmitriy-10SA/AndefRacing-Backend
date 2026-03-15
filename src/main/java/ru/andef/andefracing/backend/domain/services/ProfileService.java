package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.hr.Employee;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeClub;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.domain.exceptions.DuplicateException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.domain.mappers.club.ClubMapper;
import ru.andef.andefracing.backend.domain.mappers.club.EmployeeMapper;
import ru.andef.andefracing.backend.domain.mappers.club.PhotoMapper;
import ru.andef.andefracing.backend.domain.mappers.location.CityMapper;
import ru.andef.andefracing.backend.domain.mappers.location.RegionMapper;
import ru.andef.andefracing.backend.domain.services.search.ClientSearchService;
import ru.andef.andefracing.backend.domain.services.search.ClubSearchService;
import ru.andef.andefracing.backend.network.dtos.common.PageInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientChangePersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.FavoriteClubShortDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.PagedFavoriteClubShortListDto;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeePersonalInfoDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final String NAME = "name";

    private final ClientSearchService clientSearchService;
    private final ClubSearchService clubSearchService;

    private final ClientRepository clientRepository;
    private final ClubRepository clubRepository;

    private final ClientMapper clientMapper;
    private final EmployeeMapper employeeMapper;
    private final ClubMapper clubMapper;
    private final CityMapper cityMapper;
    private final RegionMapper regionMapper;
    private final PhotoMapper photoMapper;

    /**
     * Получение информации о клиенте
     */
    @Transactional(readOnly = true)
    public ClientPersonalInfoDto getClientPersonalInfo(long clientId) {
        Client client = clientSearchService.findClientById(clientId);
        return clientMapper.toPersonalInfoDto(client);
    }

    /**
     * Редактирование личной информации клиента (имя, номер телефона)
     */
    @Transactional
    public void changeClientPersonalInfo(long clientId, ClientChangePersonalInfoDto changePersonalInfoDto) {
        Client client = clientSearchService.findClientById(clientId);
        client.setName(changePersonalInfoDto.name());
        client.setPhone(changePersonalInfoDto.phone());
        clientRepository.save(client);
    }

    /**
     * Добавление клуба в список избранных клубов клиента
     */
    @Transactional
    public void addClubToClientFavoriteClubs(long clientId, int clubId) {
        Client client = clientSearchService.findClientById(clientId);
        Club club = clubSearchService.findClubById(clubId);
        if (client.getFavoriteClubs().contains(club)) {
            throw new DuplicateException("Клуб с id " + clubId + " уже добавлен в избранное");
        }
        client.addFavoriteClub(club);
        clientRepository.save(client);
    }

    /**
     * Получение списка избранных клубов клиента с пагинацией
     */
    @Transactional(readOnly = true)
    public PagedFavoriteClubShortListDto getClientFavoriteClubs(long clientId, int pageNumber, int pageSize) {
        Client client = clientSearchService.findClientById(clientId);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(NAME));
        Page<Club> clubsPage = clubRepository.getClientFavoriteClubs(client.getId(), pageRequest);
        List<FavoriteClubShortDto> content = clubMapper
                .toFavoriteClubShortDto(clubsPage.getContent(), cityMapper, regionMapper, photoMapper);
        long totalElements = clubsPage.getTotalElements();
        int totalPages = clubsPage.getTotalPages();
        boolean isLast = clubsPage.isLast();
        PageInfoDto pageInfoDto = new PageInfoDto(pageNumber, pageSize, totalElements, totalPages, isLast);
        return new PagedFavoriteClubShortListDto(content, pageInfoDto);
    }

    /**
     * Удаление клуба из списка избранных клубов клиента
     */
    @Transactional
    public void deleteClubFromClientFavoriteClubs(long clientId, int clubId) {
        Client client = clientSearchService.findClientById(clientId);
        Club club = clubSearchService.findClubById(clubId);
        if (!client.getFavoriteClubs().contains(club)) {
            throw new EntityNotFoundException("У клиента нет избранного клуба с id " + clubId);
        }
        client.deleteFavoriteClub(club);
        clientRepository.save(client);
    }

    /**
     * Получение информации о сотруднике (фамилия, имя, отчество, номер телефона, роли в текущем клубе)
     */
    @Transactional(readOnly = true)
    public EmployeePersonalInfoDto getEmployeePersonalInfo(long employeeId, int clubId) {
        Employee employee = clubSearchService.findEmployeeById(employeeId);
        Club club = clubSearchService.findClubById(clubId);
        List<EmployeeRole> roles = club.getEmployeesAndRoles().stream()
                .filter(employeeClub -> employeeClub.getEmployee().equals(employee))
                .map(EmployeeClub::getEmployeeRole)
                .toList();
        return employeeMapper.toPersonalInfo(employee, roles);
    }
}
