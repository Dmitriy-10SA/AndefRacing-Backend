package ru.andef.andefracing.backend.network.dtos;

import lombok.Getter;

/**
 * Dto для избранного клуба клиента (краткая информация)
 */
@Getter
public class FavoriteClubShortDto extends ClubShortDto {
    private final CityDto city;
    private final PhotoDto mainPhoto;

    public FavoriteClubShortDto(
            int id,
            String name,
            String phone,
            String email,
            String address,
            short cntEquipment,
            boolean isOpen,
            CityDto city,
            PhotoDto mainPhoto
    ) {
        super(id, name, phone, email, address, cntEquipment, isOpen);
        this.city = city;
        this.mainPhoto = mainPhoto;
    }
}
