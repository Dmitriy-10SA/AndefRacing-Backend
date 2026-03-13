package ru.andef.andefracing.backend.domain.services.search;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
@Transactional
class ClientSearchServiceTest {
    private final ClientSearchService clientSearchService;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientSearchServiceTest(
            ClientSearchService clientSearchService,
            ClientRepository clientRepository
    ) {
        this.clientSearchService = clientSearchService;
        this.clientRepository = clientRepository;
    }

    private Client createClient(String phone, boolean blocked) {
        Client client = new Client("TestClient", phone, "password");
        client.setBlocked(blocked);
        return clientRepository.save(client);
    }

    @Test
    void findClientByPhoneReturnsClientWhenExists() {
        // Arrange
        Client client = createClient("+7-123-456-78-90", false);

        // Act
        Client found = clientSearchService.findClientByPhone(client.getPhone());

        // Assert
        assertNotNull(found);
        assertEquals(client.getId(), found.getId());
        assertEquals(client.getName(), found.getName());
        assertEquals(client.getPhone(), found.getPhone());
    }

    @Test
    void findClientByPhoneThrowsEntityNotFoundExceptionWhenClientDoesNotExist() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clientSearchService.findClientByPhone("+7-000-000-00-00")
        );
    }

    @Test
    void findClientByPhoneThrowsBlockedExceptionWhenClientIsBlocked() {
        // Arrange
        Client client = createClient("+7-111-111-11-11", true);

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clientSearchService.findClientByPhone(client.getPhone())
        );
    }

    @Test
    void findClientByPhoneWithCustomExceptionReturnsClientWhenExists() {
        // Arrange
        Client client = createClient("+7-222-222-22-22", false);
        RuntimeException customException = new RuntimeException("Custom error");

        // Act
        Client found = clientSearchService.findClientByPhone(client.getPhone(), customException);

        // Assert
        assertNotNull(found);
        assertEquals(client.getId(), found.getId());
    }

    @Test
    void findClientByPhoneWithCustomExceptionThrowsCustomExceptionWhenClientDoesNotExist() {
        // Arrange
        RuntimeException customException = new RuntimeException("Custom error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                clientSearchService.findClientByPhone("+7-999-999-99-99", customException)
        );
        assertEquals("Custom error", thrown.getMessage());
    }

    @Test
    void findClientByPhoneWithCustomExceptionThrowsBlockedExceptionWhenClientIsBlocked() {
        // Arrange
        Client client = createClient("+7-333-333-33-33", true);
        RuntimeException customException = new RuntimeException("Custom error");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clientSearchService.findClientByPhone(client.getPhone(), customException)
        );
    }

    @Test
    void findClientByIdReturnsClientWhenExists() {
        // Arrange
        Client client = createClient("+7-444-444-44-44", false);

        // Act
        Client found = clientSearchService.findClientById(client.getId());

        // Assert
        assertNotNull(found);
        assertEquals(client.getId(), found.getId());
        assertEquals(client.getName(), found.getName());
        assertEquals(client.getPhone(), found.getPhone());
    }

    @Test
    void findClientByIdThrowsEntityNotFoundExceptionWhenClientDoesNotExist() {
        // Arrange
        long nonExistentId = 999L;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                clientSearchService.findClientById(nonExistentId)
        );
    }

    @Test
    void findClientByIdThrowsBlockedExceptionWhenClientIsBlocked() {
        // Arrange
        Client client = createClient("+7-555-555-55-55", true);

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clientSearchService.findClientById(client.getId())
        );
    }

    @Test
    void findClientByIdReturnsCorrectClientAmongMultiple() {
        // Arrange
        Client client1 = createClient("+7-666-666-66-66", false);
        Client client2 = createClient("+7-777-777-77-77", false);
        Client client3 = createClient("+7-888-888-88-88", false);

        // Act
        Client found = clientSearchService.findClientById(client2.getId());

        // Assert
        assertEquals(client2.getId(), found.getId());
        assertEquals(client2.getPhone(), found.getPhone());
    }
}