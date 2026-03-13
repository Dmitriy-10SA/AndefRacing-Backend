package ru.andef.andefracing.backend.network.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.andef.andefracing.backend.data.entities.club.hr.EmployeeRole;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.security.jwt.JwtFilter;
import ru.andef.andefracing.backend.network.security.jwt.JwtProperties;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final JwtProperties jwtProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        final String[] allEmployeeRoles = Arrays.stream(EmployeeRole.values())
                .map(EmployeeRole::getRole)
                .toArray(String[]::new);
        final String[] allEmployeeRolesForBookings = Arrays.stream(EmployeeRole.values())
                .map(EmployeeRole::getRole)
                .filter(role -> {
                    String adminRole = EmployeeRole.ADMIN.getRole();
                    String managerRole = EmployeeRole.MANAGER.getRole();
                    return role.equals(adminRole) || role.equals(managerRole);
                })
                .toArray(String[]::new);
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // auth
                        .requestMatchers(ApiPaths.AUTH_CLIENT + "/**").permitAll()
                        .requestMatchers(ApiPaths.AUTH_EMPLOYEE + "/**").permitAll()

                        // bookings
                        .requestMatchers(ApiPaths.BOOKINGS_CLIENT + "/**").hasRole(jwtProperties.getClientRole())
                        .requestMatchers(ApiPaths.BOOKINGS_EMPLOYEE + "/**").hasAnyRole(allEmployeeRolesForBookings)

                        // management
                        .requestMatchers(ApiPaths.CLUB_MANAGEMENT + "/**").hasRole(EmployeeRole.MANAGER.getRole())

                        // profile
                        .requestMatchers(ApiPaths.PROFILE_CLIENT + "/**").hasRole(jwtProperties.getClientRole())
                        .requestMatchers(ApiPaths.PROFILE_EMPLOYEE + "/**").hasAnyRole(allEmployeeRoles)

                        // reports
                        .requestMatchers(ApiPaths.REPORTS + "/**").hasRole(EmployeeRole.MANAGER.getRole())

                        // search
                        .requestMatchers(ApiPaths.REGIONS_SEARCH + "/**").permitAll()
                        .requestMatchers(ApiPaths.CITIES_SEARCH + "/**").permitAll()
                        .requestMatchers(ApiPaths.CLUBS_SEARCH + "/**").permitAll()

                        // swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        //other
                        .anyRequest().authenticated()
                )
                .sessionManagement(it ->
                        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}