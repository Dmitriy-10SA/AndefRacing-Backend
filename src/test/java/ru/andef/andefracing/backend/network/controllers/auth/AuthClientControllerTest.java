package ru.andef.andefracing.backend.network.controllers.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientLoginDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthClientController.class)
class AuthClientControllerTest {
    private static final String BASE_URL = "/auth/client";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientRegisterDto getValidRegisterDto() {
        return new ClientRegisterDto(
                "Ivan",
                "+7-123-456-78-90",
                "Password1!"
        );
    }

    private ClientLoginDto getValidLoginDto() {
        return new ClientLoginDto("+7-123-456-78-90", "Password1!");
    }

    private ClientChangePasswordDto getValidChangePasswordDto() {
        return new ClientChangePasswordDto("+7-123-456-78-90", "NewPass1!");
    }

    @Test
    void registerReturnCreatedWhenValidDto() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getValidRegisterDto())))
                .andExpect(status().isCreated());
    }

    @Test
    void registerReturnBadRequestWhenNameIsBlank() throws Exception {
        ClientRegisterDto dto = new ClientRegisterDto(
                "",
                "+7-123-456-78-90",
                "Password1!"
        );
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginReturnOkWhenValidDto() throws Exception {
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getValidLoginDto())))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordReturnOkWhenValidDto() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getValidChangePasswordDto())))
                .andExpect(status().isOk());
    }
}