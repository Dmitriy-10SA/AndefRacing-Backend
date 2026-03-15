package ru.andef.andefracing.backend.domain.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;
import ru.andef.andefracing.backend.network.dtos.booking.ClientDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ClientMapper
 *
 * @see ClientMapper
 */
class ClientMapperTest {
    private ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        clientMapper = Mappers.getMapper(ClientMapper.class);
    }

    @Test
    @DisplayName("Преобразование ClientRegisterDto в Client entity")
    void testToEntity() {
        // Arrange
        ClientRegisterDto registerDto = new ClientRegisterDto(
                "Иван",
                "+7-999-123-45-67",
                "securePassword123"
        );

        // Act
        Client client = clientMapper.toEntity(registerDto);

        // Assert
        assertNotNull(client);
        assertEquals(0, client.getId());
        assertTrue(client.getFavoriteClubs().isEmpty());
        assertEquals(registerDto.name(), client.getName());
        assertEquals(registerDto.phone(), client.getPhone());
        assertEquals(registerDto.password(), client.getPassword());
        assertFalse(client.isBlocked());
    }

    @Test
    @DisplayName("Преобразование Client в ClientPersonalInfoDto")
    void testToPersonalInfoDto() {
        // Arrange
        Client client = new Client(
                1L,
                "Петр",
                "+7-999-987-65-43",
                "password",
                false,
                null,
                null
        );

        // Act
        ClientPersonalInfoDto dto = clientMapper.toPersonalInfoDto(client);

        // Assert
        assertNotNull(dto);
        assertEquals(client.getPhone(), dto.getPhone());
        assertEquals(client.getName(), dto.getName());
    }

    @Test
    @DisplayName("Преобразование Client в ClientDto")
    void testToDto() {
        // Arrange
        Client client = new Client(
                123L,
                "Алексей",
                "+7-999-111-22-33",
                "password",
                false,
                null,
                null
        );

        // Act
        ClientDto dto = clientMapper.toDto(client);

        // Assert
        assertNotNull(dto);
        assertEquals(client.getId(), dto.id());
        assertEquals(client.getName(), dto.name());
        assertEquals(client.getPhone(), dto.phone());
    }
}
