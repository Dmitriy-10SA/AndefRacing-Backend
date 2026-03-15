package ru.andef.andefracing.backend.domain.services.search;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.domain.exceptions.BlockedException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:scripts/db/truncate-all-tables-for-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

    private Client createClient(String phone) {
        Client client = new Client("Test Client", phone, "password");
        return clientRepository.save(client);
    }

    private Client createBlockedClient(String phone) {
        Client client = new Client("Blocked Client", phone, "password");
        client.setBlocked(true);
        return clientRepository.save(client);
    }

    @Test
    void findClientByPhoneReturnsClientWhenExists() {
        // Arrange
        Client client = createClient("+7-111-111-11-11");

        // Act
        Client result = clientSearchService.findClientByPhone("+7-111-111-11-11");

        // Assert
        assertNotNull(result);
        assertEquals(client.getId(), result.getId());
        assertEquals(client.getName(), result.getName());
        assertEquals(client.getPhone(), result.getPhone());
    }

    @Test
    void findClientByPhoneThrowsExceptionWhenClientNotFound() {
        // Arrange
        String nonExistentPhone = "+7-999-999-99-99";

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clientSearchService.findClientByPhone(nonExistentPhone)
        );
        assertTrue(exception.getMessage().contains(nonExistentPhone));
    }

    @Test
    void findClientByPhoneThrowsExceptionWhenClientIsBlocked() {
        // Arrange
        Client blockedClient = createBlockedClient("+7-222-222-22-22");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clientSearchService.findClientByPhone(blockedClient.getPhone())
        );
    }

    @Test
    void findClientByPhoneWithCustomExceptionThrowsCustomException() {
        // Arrange
        String nonExistentPhone = "+7-999-999-99-99";
        RuntimeException customException = new IllegalArgumentException("Custom error message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                clientSearchService.findClientByPhone(nonExistentPhone, customException)
        );
        assertEquals("Custom error message", exception.getMessage());
    }

    @Test
    void findClientByPhoneWithCustomExceptionReturnsClientWhenExists() {
        // Arrange
        Client client = createClient("+7-333-333-33-33");
        RuntimeException customException = new IllegalArgumentException("Should not be thrown");

        // Act
        Client result = clientSearchService.findClientByPhone("+7-333-333-33-33", customException);

        // Assert
        assertNotNull(result);
        assertEquals(client.getId(), result.getId());
    }

    @Test
    void findClientByPhoneWithCustomExceptionThrowsBlockedExceptionWhenClientIsBlocked() {
        // Arrange
        Client blockedClient = createBlockedClient("+7-444-444-44-44");
        RuntimeException customException = new IllegalArgumentException("Custom error");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clientSearchService.findClientByPhone(blockedClient.getPhone(), customException)
        );
    }

    @Test
    void findClientByIdReturnsClientWhenExists() {
        // Arrange
        Client client = createClient("+7-555-555-55-55");

        // Act
        Client result = clientSearchService.findClientById(client.getId());

        // Assert
        assertNotNull(result);
        assertEquals(client.getId(), result.getId());
        assertEquals(client.getName(), result.getName());
        assertEquals(client.getPhone(), result.getPhone());
    }

    @Test
    void findClientByIdThrowsExceptionWhenClientNotFound() {
        // Arrange
        long nonExistentId = 999L;

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clientSearchService.findClientById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    void findClientByIdThrowsExceptionWhenClientIsBlocked() {
        // Arrange
        Client blockedClient = createBlockedClient("+7-666-666-66-66");

        // Act & Assert
        assertThrows(BlockedException.class, () ->
                clientSearchService.findClientById(blockedClient.getId())
        );
    }
}