package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubTaskStatusRequest {
    @NotNull(message = "Subtask status is required.")
    private Boolean isComplete;
}
