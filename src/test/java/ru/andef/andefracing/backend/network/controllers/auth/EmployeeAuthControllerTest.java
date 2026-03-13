package ru.andef.andefracing.backend.network.controllers.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.AuthService;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeClubDto;
import ru.andef.andefracing.backend.network.dtos.auth.employee.EmployeeLoginDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeAuthController.class)
class EmployeeAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private EmployeeLoginDto getValidLoginDto() {
        return new EmployeeLoginDto(
                "+7-999-999-99-99",
                "password"
        );
    }

    @Test
    void isFirstEnterReturnsOkWhenValidPhone() throws Exception {
        when(authService.isEmployeeFirstEnter("+7-999-999-99-99")).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/employee/is-first-enter")
                        .param("phone", "+7-999-999-99-99"))
                .andExpect(status().isOk());
    }

    @Test
    void preLoginReturnsOkWhenValidDto() throws Exception {
        EmployeeLoginDto dto = getValidLoginDto();
        when(authService.preLoginEmployee(any(EmployeeLoginDto.class))).thenReturn(List.of(
                new EmployeeClubDto(
                        1,
                        "Club 1",
                        "+7-999-999-99-99",
                        "club@mail.com",
                        "Address",
                        (short) 10,
                        true,
                        null
                )
        ));

        mockMvc.perform(post("/api/v1/auth/employee/pre-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void loginReturnsOkWhenValidDto() throws Exception {
        EmployeeLoginDto dto = getValidLoginDto();
        when(authService.loginEmployee(any(Integer.class), any(EmployeeLoginDto.class)))
                .thenReturn(new EmployeeAuthResponseDto("token"));

        mockMvc.perform(post("/api/v1/auth/employee/login/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}

