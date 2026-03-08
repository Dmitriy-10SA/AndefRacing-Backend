package ru.andef.andefracing.backend.network.dtos.common.club;

import lombok.Getter;
import ru.andef.andefracing.backend.network.dtos.common.PhotoDto;

@Getter
public class ClubInfoDto extends ClubShortDto {
    private final PhotoDto mainPhoto;

    public ClubInfoDto(
            int id,
            String name,
            String phone,
            String email,
            String address,
            short cntEquipment,
            boolean isOpen,
            PhotoDto mainPhoto
    ) {
        super(id, name, phone, email, address, cntEquipment, isOpen);
        this.mainPhoto = mainPhoto;
    }
}
