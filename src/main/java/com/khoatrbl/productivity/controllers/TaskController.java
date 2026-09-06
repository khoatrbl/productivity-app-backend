package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.CreateTaskRequest;
import com.khoatrbl.productivity.domains.dtos.TaskDto;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.utilities.SecurityUtils;
import com.khoatrbl.productivity.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @Valid @RequestBody CreateTaskRequest createTaskRequest,
            Authentication authentication) {

        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        Tasks task = taskService.createTask(currentUserId, createTaskRequest);

        TaskDto res = TaskDto.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .dueTime(task.getDueTime())
                .priority(task.getPriority())
                .status(task.getStatus())
                .build();


        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTaskOfUser(Authentication authentication) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        List<Tasks> taskList = taskService.getAllTasksByUserId(currentUserId);
        List<TaskDto> res = taskList.stream().map(task -> TaskDto.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .dueTime(task.getDueTime())
                .priority(task.getPriority())
                .status(task.getStatus())
                .build()
        ).toList();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
