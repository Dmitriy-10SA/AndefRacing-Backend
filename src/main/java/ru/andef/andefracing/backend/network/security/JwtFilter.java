package ru.andef.andefracing.backend.network.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс для проверки JWT токена для каждого HTTP-запроса
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /**
     * Проверка JWT токена для каждого HTTP-запроса
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (Objects.nonNull(authHeader) && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.extractClaims(token);
                String subject = claims.getSubject();
                UsernamePasswordAuthenticationToken authToken;
                if (subject.equals(jwtProperties.getClientSubject())) {
                    long clientId = claims.get(jwtProperties.getIdClaim(), long.class);
                    authToken = new UsernamePasswordAuthenticationToken(
                            clientId,
                            null,
                            List.of(new SimpleGrantedAuthority(jwtProperties.getClientRole()))
                    );
                } else if (subject.equals(jwtProperties.getEmployeeSubject())) {
                    long employeeId = claims.get(jwtProperties.getIdClaim(), long.class);
                    String[] roles = claims.get(jwtProperties.getRolesClaim(), String[].class);
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    authToken = new UsernamePasswordAuthenticationToken(employeeId, null, authorities);
                } else {
                    authToken = null;
                }
                if (Objects.nonNull(authToken)) {
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}