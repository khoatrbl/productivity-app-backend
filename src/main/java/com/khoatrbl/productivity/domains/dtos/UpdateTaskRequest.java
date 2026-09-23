package com.khoatrbl.productivity.domains.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskRequest {
    @NotBlank(message = "Task title is required.")
    @Size(min = 1, max = 255, message = "Task title must be between {min} and {max} characters.")
    @Pattern(
            regexp = "^[^\\r\\n]*$",
            message = "Title cannot contain line breaks."
    )
    private String title;

    @Size(max = 1000, message = "Task description can only contains {max} characters.")
    private String description;

    @FutureOrPresent(message = "Due date cannot be in the past.")
    private LocalDate dueDate;

    private LocalTime dueTime;

    @NotNull(message = "Task priority is required.")
    private Priority priority;

    @NotNull(message = "Sprint interval is required.")
    private int sprintInMinutes;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Builder.Default
    private List<UpdateSubTaskRequest> subTasks = new ArrayList<>();
}
