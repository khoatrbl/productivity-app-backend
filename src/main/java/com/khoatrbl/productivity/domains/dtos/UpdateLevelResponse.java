package com.khoatrbl.productivity.domains.dtos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLevelResponse {
    private LevelDto currentLevel;
    private int currentExp;
}
