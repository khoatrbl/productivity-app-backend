package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.TreatDto;
import com.khoatrbl.productivity.domains.entities.Treat;

public class TreatMapper {
    public static TreatDto toDto(Treat treat) {
        return TreatDto.builder()
                .id(treat.getId())
                .treatName(treat.getTreatName())
                .exp(treat.getExp())
                .price(treat.getPrice())
                .treatTier(treat.getTreatTier())
                .build();
    }
}
