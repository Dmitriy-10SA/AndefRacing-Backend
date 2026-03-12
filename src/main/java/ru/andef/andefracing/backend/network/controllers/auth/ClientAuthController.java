package ru.andef.andefracing.backend.network.controllers.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.AuthService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.ApiVersions;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientLoginDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;

@RestController
@RequestMapping(ApiPaths.AUTH_CLIENT)
@RequiredArgsConstructor
public class ClientAuthController {
    private final AuthService authService;

    /**
     * Регистрация в системе для клиента
     */
    @PostMapping(path = "/register", version = ApiVersions.V1)
    public ResponseEntity<ClientAuthResponseDto> register(@RequestBody @Valid ClientRegisterDto registerDto) {
        ClientAuthResponseDto clientAuthResponseDto = authService.registerClient(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientAuthResponseDto);
    }

    /**
     * Вход в систему для клиента
     */
    @PostMapping(path = "/login", version = ApiVersions.V1)
    public ResponseEntity<ClientAuthResponseDto> login(@RequestBody @Valid ClientLoginDto loginDto) {
        ClientAuthResponseDto clientAuthResponseDto = authService.loginClient(loginDto);
        return ResponseEntity.ok(clientAuthResponseDto);
    }

    /**
     * Изменение пароля у клиента по номеру телефона
     */
    @PatchMapping(path = "/change-password", version = ApiVersions.V1)
    public ResponseEntity<ClientAuthResponseDto> changePassword(
            @RequestBody @Valid ClientChangePasswordDto changePasswordDto
    ) {
        ClientAuthResponseDto clientAuthResponseDto = authService.changeClientPassword(changePasswordDto);
        return ResponseEntity.ok(clientAuthResponseDto);
    }
}