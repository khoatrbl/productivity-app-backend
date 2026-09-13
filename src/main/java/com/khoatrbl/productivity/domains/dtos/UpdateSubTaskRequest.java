package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSubTaskRequest {
    @NotBlank(message = "Subtask ID is required.")
    private UUID id;

    @NotBlank(message = "Subtask content is required.")
    @Size(min = 1, max = 40, message = "Subtask content must be between {min} and {max} characters.")
    private String content;

    @NotNull(message = "Subtask position is required.")
    private int position;

    @NotNull(message = "Subtask status is required.")
    private boolean isComplete;
}
