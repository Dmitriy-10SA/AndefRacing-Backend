package ru.andef.andefracing.backend.network.controllers.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.andef.andefracing.backend.domain.services.ProfileService;
import ru.andef.andefracing.backend.network.dtos.profile.employee.EmployeePersonalInfoDto;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeProfileController.class)
class EmployeeProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private ProfileService profileService;

    private Authentication employeeAuth() {
        JwtFilter.EmployeePrincipal principal = new JwtFilter.EmployeePrincipal(1L, 2, "Club");
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Test
    void getPersonalInfoReturnsOkWhenAuthenticated() throws Exception {
        when(profileService.getEmployeePersonalInfo(1L, 2)).thenReturn(
                new EmployeePersonalInfoDto("Surname", "Name", "Patronymic", "+7-999-999-99-99", Collections.emptyList())
        );

        mockMvc.perform(get("/api/v1/profile/employee/personal-info")
                        .with(authentication(employeeAuth())))
                .andExpect(status().isOk());
    }
}

