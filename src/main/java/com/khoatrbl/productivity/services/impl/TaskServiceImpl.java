package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.Status;
import com.khoatrbl.productivity.domains.dtos.CreateTaskRequest;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.domains.entities.Users;
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
        return List.of();
    }

    @Override
    public Tasks createTask(UUID userId, CreateTaskRequest createTaskRequest) {

        // TODO: currently the method does NOT know who task this belongs to. Configure the owner of this task using the userID.

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
                .priority(createTaskRequest.getPriority())
                .status(Status.INCOMPLETE)
                .build();

        return taskRepository.save(newTask);
    }
}
