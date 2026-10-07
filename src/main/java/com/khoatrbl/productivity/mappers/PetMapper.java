package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.PetDto;
import com.khoatrbl.productivity.domains.entities.Pets;

public class PetMapper {
    public static PetDto toDto(Pets pet) {
        return PetDto.builder()
                .id(pet.getId())
                .ownerId(pet.getOwner().getId())
                .name(pet.getName())
                .petCurrentExp(pet.getPetCurrentExp())
                .petLevel(PetLevelMapper.toDto(pet.getPetLevel()))
                .petCurrentAffectionPoint(pet.getCurrentAffectionPoint())
                .upcomingAffectionGains(pet.getUpcomingAffectionGains())
                .pettingsLeft(pet.getPettingsLeft())
                .bonusPets(pet.getBonusPets())
                .petCooldownUntil(pet.getPetCooldownUntil())
                .items(pet.getItems().stream()
                        .map(PetItemMapper::toDto)
                        .toList())
                .build();
    }
}
