package ru.andef.andefracing.backend.network.controllers.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientChangePasswordDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientLoginDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;

/**
 * Controller для всех Auth действий клиента
 */
@RestController
@RequestMapping("/auth/client")
public class ClientAuthController {
    @PostMapping("/register")
    public ResponseEntity<ClientAuthResponseDto> register(@RequestBody @Valid ClientRegisterDto registerDto) {
        // TODO
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClientAuthResponseDto(""));
    }

    @PostMapping("/login")
    public ResponseEntity<ClientAuthResponseDto> login(@RequestBody @Valid ClientLoginDto loginDto) {
        // TODO
        return ResponseEntity.ok(new ClientAuthResponseDto(""));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ClientAuthResponseDto> changePassword(
            @RequestBody @Valid ClientChangePasswordDto changePasswordDto
    ) {
        // TODO ("без СМС, упрощаем, хоть и плохо")
        return ResponseEntity.ok(new ClientAuthResponseDto(""));
    }
}