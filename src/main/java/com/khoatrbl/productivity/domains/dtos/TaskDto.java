package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private Priority priority;
    private Status status;
}
