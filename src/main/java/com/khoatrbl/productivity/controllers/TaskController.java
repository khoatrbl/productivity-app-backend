package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.CreateTaskRequest;
import com.khoatrbl.productivity.domains.dtos.CreateTaskResponse;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.utilities.SecurityUtils;
import com.khoatrbl.productivity.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<CreateTaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest createTaskRequest,
            Authentication authentication) {

        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        Tasks task = taskService.createTask(currentUserId, createTaskRequest);

        CreateTaskResponse res = CreateTaskResponse.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .dueTime(task.getDueTime())
                .priority(task.getPriority())
                .status(task.getStatus())
                .build();


        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
