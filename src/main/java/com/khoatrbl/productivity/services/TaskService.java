package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.*;
import com.khoatrbl.productivity.domains.entities.Tasks;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<Tasks> getAllTasksByUserId(UUID userId);

    Tasks createTask(UUID userId, CreateTaskRequest createTaskRequest);

    Tasks updateTaskData(UUID userId, UUID taskId, UpdateTaskRequest updateTaskRequest);

    Tasks updateTaskStatus(UUID userId, UUID taskId, UpdateTaskStatusRequest updateTaskStatusRequest);

    void deleteTask(UUID userId, UUID taskId);

    RewardEstimateResponse estimateReward(UUID userId, RewardEstimateRequest rewardEstimateRequest);

}
