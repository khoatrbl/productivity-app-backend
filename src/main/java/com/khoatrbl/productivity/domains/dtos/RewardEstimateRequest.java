package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.Priority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardEstimateRequest {
    @Size(max = 50, message = "Title must be at most {max} characters.")
    private String title; // nullable/blank is fine — falls straight to priority-bucket estimation

    @NotNull(message = "Priority is required.")
    private Priority priority;

    @Min(value = 0, message = "Subtask count cannot be negative.")
    private int subTaskCount;
}
