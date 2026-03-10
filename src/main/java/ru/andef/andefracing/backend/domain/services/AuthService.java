package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.domain.exceptions.ClientWithThisPhoneAlreadyExistsException;
import ru.andef.andefracing.backend.domain.mappers.ClientMapper;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientAuthResponseDto;
import ru.andef.andefracing.backend.network.dtos.auth.client.ClientRegisterDto;
import ru.andef.andefracing.backend.network.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    /**
     * Регистрация в системе для клиента
     */
    @Transactional
    public ClientAuthResponseDto registerClient(ClientRegisterDto registerDto) {
        if (clientRepository.existsByPhone(registerDto.phone())) {
            throw new ClientWithThisPhoneAlreadyExistsException(registerDto.phone());
        }
        String passwordHash = passwordEncoder.encode(registerDto.password());
        registerDto = new ClientRegisterDto(registerDto.name(), registerDto.phone(), passwordHash);
        Client client = clientMapper.toEntity(registerDto);
        client = clientRepository.save(client);
        String jwt = jwtUtil.generateClientToken(client.getId());
        return new ClientAuthResponseDto(jwt);
    }
}