package ru.andef.andefracing.backend.network.dtos.search;

import lombok.Getter;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;
import ru.andef.andefracing.backend.network.dtos.common.PhotoDto;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubShortDto;

import java.util.List;

/**
 * DTO - полная информация о клубе
 */
@Getter
public class ClubFullInfoDto extends ClubShortDto {
    private final List<PhotoDto> photos;
    private final List<GameDto> games;
    private final List<PriceDto> prices;
    private final List<WorkScheduleDto> workSchedules;

    public ClubFullInfoDto(
            int id,
            String name,
            String phone,
            String email,
            String address,
            short cntEquipment,
            boolean isOpen,
            List<PhotoDto> photos,
            List<GameDto> games,
            List<PriceDto> prices,
            List<WorkScheduleDto> workSchedules
    ) {
        super(id, name, phone, email, address, cntEquipment, isOpen);
        this.photos = photos;
        this.games = games;
        this.prices = prices;
        this.workSchedules = workSchedules;
    }
}
