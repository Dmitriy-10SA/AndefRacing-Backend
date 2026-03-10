package ru.andef.andefracing.backend.network.dtos.profile.employee;

import lombok.Getter;
import ru.andef.andefracing.backend.network.dtos.common.club.ClubShortDto;
import ru.andef.andefracing.backend.network.dtos.common.location.CityDto;

@Getter
public class EmployeeClubDto extends ClubShortDto {
    private final CityDto city;

    public EmployeeClubDto(
            int id,
            String name,
            String phone,
            String email,
            String address,
            short cntEquipment,
            boolean isOpen,
            CityDto city
    ) {
        super(id, name, phone, email, address, cntEquipment, isOpen);
        this.city = city;
    }
}
