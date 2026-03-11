package ru.andef.andefracing.backend.network.controllers.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.domain.services.ProfileService;
import ru.andef.andefracing.backend.network.ApiPaths;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientChangePersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.PagedFavoriteClubShortListDto;
import ru.andef.andefracing.backend.network.security.JwtFilter;

@RestController
@RequestMapping(ApiPaths.PROFILE_CLIENT)
@Validated
@RequiredArgsConstructor
public class ClientProfileController {
    private final ProfileService profileService;

    /**
     * Получение информации о клиенте (имя, номер телефона)
     */
    @GetMapping("/personal-info")
    public ResponseEntity<ClientPersonalInfoDto> getPersonalInfo(Authentication authentication) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ClientPersonalInfoDto clientPersonalInfoDto = profileService.getClientPersonalInfo(principal.id());
        return ResponseEntity.ok(clientPersonalInfoDto);
    }

    /**
     * Редактирование личной информации клиента (имя, номер телефона)
     */
    @PatchMapping("/change-personal-info")
    public ResponseEntity<Void> changePersonalInfo(
            @RequestBody @Valid ClientChangePersonalInfoDto changePersonalInfoDto,
            Authentication authentication
    ) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        profileService.changeClientPersonalInfo(principal.id(), changePersonalInfoDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Добавление клуба в список избранных клубов клиента
     */
    @PostMapping("/favorite-clubs/{clubId}")
    public ResponseEntity<Void> addFavoriteClub(@PathVariable int clubId, Authentication authentication) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        profileService.addClubToClientFavoriteClubs(principal.id(), clubId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение списка избранных клубов клиента с пагинацией
     */
    @GetMapping("/favorite-clubs")
    public ResponseEntity<PagedFavoriteClubShortListDto> getFavoriteClubs(
            @RequestParam @Min(value = 0) int pageNumber,
            @RequestParam @Min(value = 1) @Max(value = 100) int pageSize,
            Authentication authentication
    ) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PagedFavoriteClubShortListDto clubs = profileService.getClientFavoriteClubs(
                principal.id(),
                pageNumber,
                pageSize
        );
        return ResponseEntity.ok(clubs);
    }

    /**
     * Удаление клуба из списка избранных клубов клиента
     */
    @DeleteMapping("/favorite-clubs/{clubId}")
    public ResponseEntity<Void> deleteFavoriteClub(@PathVariable int clubId, Authentication authentication) {
        JwtFilter.ClientPrincipal principal = (JwtFilter.ClientPrincipal) authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        profileService.deleteClubFromClientFavoriteClubs(principal.id(), clubId);
        return ResponseEntity.noContent().build();
    }
}