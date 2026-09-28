package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.PetLevelDto;
import com.khoatrbl.productivity.domains.entities.PetLevels;

public class PetLevelMapper {
    public static PetLevelDto toDto(PetLevels petLevel) {
        return PetLevelDto.builder()
                .level(petLevel.getLevel())
                .threshold(petLevel.getThreshold())
                .build();
    }

}
