package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.LevelDto;
import com.khoatrbl.productivity.domains.entities.Level;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LevelMapper {
    public static LevelDto toDto(Level level) {
        return LevelDto.builder()
                .level(level.getLevel())
                .threshold(level.getThreshold())
                .build();
    }
}
