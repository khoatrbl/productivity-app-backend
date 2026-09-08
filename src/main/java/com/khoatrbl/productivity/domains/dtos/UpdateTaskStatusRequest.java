package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTaskStatusRequest {
    @NotNull(message = "Task status is required.")
    private Status taskStatus;
}
