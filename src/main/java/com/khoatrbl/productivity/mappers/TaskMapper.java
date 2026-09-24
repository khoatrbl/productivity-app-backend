package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.TaskDto;
import com.khoatrbl.productivity.domains.entities.Tasks;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class TaskMapper {
    public static TaskDto toTaskDto(Tasks task) {
        return TaskDto.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .dueTime(task.getDueTime())
                .estimateMin(task.getEstimateMin())
                .totalExp(task.getTotalExp())
                .totalCoins(task.getCoins())
                .startedAt(task.getStartedAt())
                .completeAt(task.getCompletedAt())
                .priority(task.getPriority())
                .status(task.getStatus())
                .sprintInMinutes(task.getSprintInMinutes())
                .startExpClaimed(task.isStartExpClaimed())
                .createdAt(task.getCreatedAt())
                .subTasks(task.getSubTasks() != null ?
                        task.getSubTasks().stream().map(SubTaskMapper::toSubTaskDto).toList() : new ArrayList<>())
                .build();
    }
}
