package ru.andef.andefracing.backend.network.controllers.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;
import ru.andef.andefracing.backend.domain.services.AuthService;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientLoginDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientAuthController.class)
class ClientAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private ClientRegisterDto getValidRegisterDto() {
        return new ClientRegisterDto(
                "Client Name",
                "+7-999-999-99-99",
                "password"
        );
    }

    private ClientLoginDto getValidLoginDto() {
        return new ClientLoginDto(
                "+7-999-999-99-99",
                "password"
        );
    }

    private ClientChangePasswordDto getValidChangePasswordDto() {
        return new ClientChangePasswordDto(
                "+7-999-999-99-99",
                "oldPassword"
        );
    }

    private ClientAuthResponseDto getAuthResponseDto() {
        return new ClientAuthResponseDto("token");
    }

    @Test
    void loginReturnsOkWhenValidDto() throws Exception {
        ClientLoginDto dto = getValidLoginDto();
        when(authService.loginClient(any(ClientLoginDto.class))).thenReturn(getAuthResponseDto());

        mockMvc.perform(post("/api/v1/auth/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordReturnsOkWhenValidDto() throws Exception {
        ClientChangePasswordDto dto = getValidChangePasswordDto();
        when(authService.changeClientPassword(any(ClientChangePasswordDto.class))).thenReturn(getAuthResponseDto());

        mockMvc.perform(post("/api/v1/auth/client/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}

