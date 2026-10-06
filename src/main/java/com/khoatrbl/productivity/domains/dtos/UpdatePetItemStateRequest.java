package com.khoatrbl.productivity.domains.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePetItemStateRequest {
    @NotNull(message = "Equip status is required.")
    @JsonProperty("isEquipped")
    private boolean equipped;
}
