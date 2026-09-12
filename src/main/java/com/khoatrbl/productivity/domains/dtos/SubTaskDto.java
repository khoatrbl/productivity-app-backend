package com.khoatrbl.productivity.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubTaskDto {
    private UUID id;
    private UUID parentTaskId;
    private String content;
    private int exp;
    private int position;
    private boolean isComplete;
}
