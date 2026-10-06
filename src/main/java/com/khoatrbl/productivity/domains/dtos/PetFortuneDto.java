package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.FortuneType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetFortuneDto {
    private FortuneType type;
    private int amount;     // coins (2–7) or number of treats (2)
    private TreatDto treat; // only set when type == TREATS
}
