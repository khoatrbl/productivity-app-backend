package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.ProfileDto;
import com.khoatrbl.productivity.domains.entities.Users;

public class ProfileMapper {
    public static ProfileDto toDto(Users user) {
        return ProfileDto.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .timezone(user.getTimezone())
                .currentLevel(LevelMapper.toDto(user.getCurrentLevel()))
                .currentExp(user.getCurrentExp())
                .coins(user.getCoins())
                .build();
    }

}
