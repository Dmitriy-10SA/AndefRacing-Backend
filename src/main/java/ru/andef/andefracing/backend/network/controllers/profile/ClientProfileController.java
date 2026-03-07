package ru.andef.andefracing.backend.network.controllers.profile;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andef.andefracing.backend.network.dtos.PagedClubShortListDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientChangePersonalInfoDto;
import ru.andef.andefracing.backend.network.dtos.profile.client.ClientPersonalInfoDto;

@RestController
@RequestMapping("/client/profile")
public class ClientProfileController {
    /**
     * Получение информации о клиенте (имя, номер телефона)
     */
    @GetMapping("/personal-info")
    public ResponseEntity<ClientPersonalInfoDto> getPersonalInfo() {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Редактирование личной информации клиента (имя, номер телефона)
     */
    @PutMapping("/change-personal-info")
    public ResponseEntity<Void> changePersonalInfo(
            @RequestBody @Valid ClientChangePersonalInfoDto changePersonalInfoDto
    ) {
        // TODO
        return ResponseEntity.ok().build();
    }

    /**
     * Добавление клуба в список избранных клубов клиента
     */
    @PostMapping("/favorite-clubs/{clubId}")
    public ResponseEntity<Void> addFavoriteClub(@PathVariable int clubId) {
        // TODO
        return ResponseEntity.ok().build();
    }

    /**
     * Получение списка избранных клубов клиента с пагинацией
     */
    @GetMapping("/favorite-club")
    public ResponseEntity<PagedClubShortListDto> getFavoriteClubs() {
        // TODO
        return ResponseEntity.ok(null);
    }

    /**
     * Удаление клуба из списка избранных клубов клиента
     */
    @DeleteMapping("/favorite-club/{clubId}")
    public ResponseEntity<Void> deleteFavoriteClub(@PathVariable int clubId) {
        // TODO
        return ResponseEntity.noContent().build();
    }
}