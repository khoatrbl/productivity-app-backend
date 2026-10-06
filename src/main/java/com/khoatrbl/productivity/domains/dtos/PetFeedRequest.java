package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetFeedRequest {
    @NotNull(message = "Treat ID is required.")
    private UUID treatId;
}
