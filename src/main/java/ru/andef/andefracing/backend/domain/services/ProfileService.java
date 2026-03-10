package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

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
}
