package ru.andef.andefracing.backend.domain.services.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ClientSearchService extends SearchService {
    private final ClientRepository clientRepository;

    /**
     * Проверка, заблокирован ли клиент
     */
    private void checkClientIsBlocked(Client client) {
        if (client.isBlocked()) {
            throw new BlockedException("Клиент заблокирован");
        }
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
}