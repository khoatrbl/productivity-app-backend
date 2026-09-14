package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCoinsRequest {
    @NotNull(message = "Coin amount is required.")
    private int amount;
}
