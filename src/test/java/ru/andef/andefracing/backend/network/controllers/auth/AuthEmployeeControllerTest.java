package ru.andef.andefracing.backend.network.controllers.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeLoginDto;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthEmployeeController.class)
class AuthEmployeeControllerTest {
    private static final String BASE_URL = "/auth/employee";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeLoginDto getValidLoginDto() {
        return new EmployeeLoginDto("+7-123-456-78-90", "Password1!");
    }

    private EmployeeChangePasswordDto getValidChangePasswordDto() {
        return new EmployeeChangePasswordDto("+7-123-456-78-90", "NewPass1!");
    }

    @Test
    void isFirstEnterReturnOkWhenValidPhone() throws Exception {
        mockMvc.perform(get(BASE_URL + "/is-first-enter")
                        .param("phone", "+7-123-456-78-90"))
                .andExpect(status().isOk());
    }

    @Test
    void isFirstEnterReturnBadRequestWhenPhoneInvalid() throws Exception {
        mockMvc.perform(get(BASE_URL + "/is-first-enter")
                        .param("phone", "123456"))
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