package ru.andef.andefracing.backend.network.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Класс для хранения Properties, относящихся к JWT
 */
@Component
@Getter
public class JwtProperties {
    @Value(value = "${jwt.secret-key}")
    private String secret;

    @Value(value = "${jwt.expiration-millis}")
    private long expirationMillis;

    private final String idClaim = "id";
    private final String rolesClaim = "roles";

    private final String issuer = "AndefRacingBackendApplication";

    private final String clientSubject = "CLIENT";
    private final String employeeSubject = "EMPLOYEE";

    private final String clientRole = "ROLE_CLIENT";
}