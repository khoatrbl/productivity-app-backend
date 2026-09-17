package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateExpRequest {
    @NotNull(message = "Exp gained is required.")
    private int expGained;
}
