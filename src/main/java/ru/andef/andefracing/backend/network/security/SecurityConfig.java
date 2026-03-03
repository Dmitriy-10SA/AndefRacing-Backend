package ru.andef.andefracing.backend.network.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final JwtProperties jwtProperties;
    private final String[] allEmployeeRoles = Arrays.stream(EmployeeRole.values())
            .map(EmployeeRole::getRole)
            .toArray(String[]::new);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(it -> {
                    it.requestMatchers("/auth/**").permitAll();
                    it.requestMatchers("/client/**").hasRole(jwtProperties.getClientRole());
                    it.requestMatchers("/employee/profile/**").hasAnyRole(allEmployeeRoles);
                })
                .sessionManagement(it ->
                        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}