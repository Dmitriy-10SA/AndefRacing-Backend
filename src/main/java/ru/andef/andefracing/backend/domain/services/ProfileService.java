package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.profile.client.DuplicateFavoriteClubException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientChangePersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ClientRepository clientRepository;
    private final ClubRepository clubRepository;

    private final ClientMapper clientMapper;

    /**
     * Получение клуба по id или выброс исключения
     */
    private Club findClubByIdOrThrow(int clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Клуб с id " + clubId + " не найден"));
    }

    /**
     * Получение клиента по id или выброс исключения
     */
    private Client findClientByIdOrThrow(long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с id " + clientId + " не найден"));
    }

    /**
     * Получение информации о клиенте
     */
    @Transactional(readOnly = true)
    public ClientPersonalInfoDto getClientPersonalInfo(long clientId) {
        Client client = findClientByIdOrThrow(clientId);
        return clientMapper.toPersonalInfoDto(client);
    }

    /**
     * Редактирование личной информации клиента (имя, номер телефона)
     */
    @Transactional
    public void changeClientPersonalInfo(long clientId, ClientChangePersonalInfoDto changePersonalInfoDto) {
        Client client = findClientByIdOrThrow(clientId);
        client.setName(changePersonalInfoDto.name());
        client.setPhone(changePersonalInfoDto.phone());
        clientRepository.save(client);
    }

    /**
     * Добавление клуба в список избранных клубов клиента
     */
    @Transactional
    public void addClubToClientFavoriteClubs(long clientId, int clubId) {
        Client client = findClientByIdOrThrow(clientId);
        Club club = findClubByIdOrThrow(clubId);
        if (client.getFavoriteClubs().contains(club)) {
            throw new DuplicateFavoriteClubException(clubId);
        }
        client.addFavoriteClub(club);
        clientRepository.save(client);
    }
}
