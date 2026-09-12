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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubTaskRequest {
    @NotBlank(message = "Subtask content is required.")
    @Size(min = 1, max = 40, message = "Subtask content must be between {min} and {max} characters.")
    private String content;

    @NotNull(message = "Subtask order is required.")
    private int position;


}
