package com.khoatrbl.productivity.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDto {
    private UUID id;

    private UUID ownerId;

    private PetLevelDto petLevel;

    private String name;

    private int petCurrentExp;

    private int petCurrentAffectionPoint;

    private int pettingsLeft;

    private Instant petCooldownUntil;

    private List<PetItemDto> items;
}
