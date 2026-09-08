package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    private UUID taskId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private int estimateMin;
    private LocalDateTime startAt;
    private LocalDateTime completeAt;
    private Priority priority;
    private Status status;
}
