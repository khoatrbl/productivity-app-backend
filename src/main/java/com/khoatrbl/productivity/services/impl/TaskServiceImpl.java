package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.Status;
import com.khoatrbl.productivity.domains.dtos.CreateTaskRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateTaskRequest;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.TaskAccessDeniedException;
import com.khoatrbl.productivity.repositories.TaskRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.TaskService;
import com.khoatrbl.productivity.utilities.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public List<Tasks> getAllTasksByUserId(UUID userId) {
        return taskRepository.findAllByUserId(userId);
    }

    @Override
    public Tasks createTask(UUID userId, CreateTaskRequest createTaskRequest) {

        Users owner = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found for id: " + userId)
                );

        Tasks newTask = Tasks.builder()
                .user(owner)
                .title(StringUtils.normalizeTitle(createTaskRequest.getTitle()))
                .description(StringUtils.normalizeDescription(createTaskRequest.getDescription()))
                .dueDate(createTaskRequest.getDueDate())
                .dueTime(createTaskRequest.getDueTime())
                .estimateMin(15) // TODO: This is hardcoded for now, implement task estimation
                .priority(createTaskRequest.getPriority())
                .status(Status.INCOMPLETE)
                .build();

        return taskRepository.save(newTask);
    }

    @Override
    public Tasks updateTask(UUID userId, UUID taskId, UpdateTaskRequest updateTaskRequest) {

        // TODO: Add in estimated min calculation

        Tasks taskToUpdate = taskRepository.findByUserIdAndTaskId(userId, taskId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Task not found for task id: " + taskId)
                );

        taskToUpdate.setTitle(updateTaskRequest.getTitle());
        taskToUpdate.setDescription(updateTaskRequest.getDescription());
        taskToUpdate.setDueDate(updateTaskRequest.getDueDate());
        taskToUpdate.setDueTime(updateTaskRequest.getDueTime());
        taskToUpdate.setPriority(updateTaskRequest.getPriority());
        taskToUpdate.setStatus(updateTaskRequest.getStatus());
        taskToUpdate.setEstimateMin(15);

        return taskRepository.save(taskToUpdate);
    }
}
