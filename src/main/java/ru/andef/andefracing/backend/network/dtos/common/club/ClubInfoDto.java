package ru.andef.andefracing.backend.network.dtos.common.club;

import lombok.Getter;
import ru.andef.andefracing.backend.network.dtos.common.PhotoDto;

/**
 * DTO - информация о клубе
 */
@Getter
public class ClubInfoDto extends ClubShortDto {
    private final PhotoDto mainPhoto;

    public ClubInfoDto(
            Integer id,
            String name,
            String phone,
            String email,
            String address,
            Short cntEquipment,
            Boolean isOpen,
            PhotoDto mainPhoto
    ) {
        super(id, name, phone, email, address, cntEquipment, isOpen);
        this.mainPhoto = mainPhoto;
    }
}
