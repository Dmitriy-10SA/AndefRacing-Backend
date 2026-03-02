package ru.andef.andefracing.backend.network.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Класс для генерации JWT токенов клиента и сотрудника, а также получения из токена Claims
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;

    private Date getExpirationDate() {
        return Date.from(Instant.now().plusMillis(jwtProperties.getExpirationMillis()));
    }

    private Key getHmacShaKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Генерация JWT токена для клиента
     */
    public String generateClientToken(long clientId) {
        return Jwts.builder()
                .setSubject(jwtProperties.getClientSubject())
                .claim(jwtProperties.getIdClaim(), clientId)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(getHmacShaKey())
                .compact();
    }

    /**
     * Генерация JWT токена для сотрудника
     */
    public String generateEmployeeToken(long employeeId, List<String> roles) {
        return Jwts.builder()
                .setSubject(jwtProperties.getEmployeeSubject())
                .claim(jwtProperties.getIdClaim(), employeeId)
                .claim(jwtProperties.getRolesClaim(), roles)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(getHmacShaKey())
                .compact();
    }

    /**
     * Получение Claims из JWT токена
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getHmacShaKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}