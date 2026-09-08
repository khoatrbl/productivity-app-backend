package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.CreateTaskRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateTaskRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateTaskStatusRequest;
import com.khoatrbl.productivity.domains.entities.Tasks;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<Tasks> getAllTasksByUserId(UUID userId);

    Tasks createTask(UUID userId, CreateTaskRequest createTaskRequest);

    Tasks updateTaskData(UUID userId, UUID taskId, UpdateTaskRequest updateTaskRequest);

    Tasks updateTaskStatus(UUID userId, UUID taskId, UpdateTaskStatusRequest updateTaskStatusRequest);

    void deleteTask(UUID userId, UUID taskId);
}
