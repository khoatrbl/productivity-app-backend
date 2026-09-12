package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.SubTaskDto;
import com.khoatrbl.productivity.domains.entities.SubTasks;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubTaskMapper {
    public static SubTaskDto toSubTaskDto(SubTasks subtask) {
        return SubTaskDto.builder()
                .id(subtask.getId())
                .content(subtask.getContent())
                .exp(subtask.getExp())
                .position(subtask.getPosition())
                .isComplete(subtask.isComplete())
                .parentTaskId(subtask.getTask().getId())
                .build();
    }
}
