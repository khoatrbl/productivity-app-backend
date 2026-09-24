package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.*;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.mappers.TaskMapper;
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

        TaskDto res = TaskMapper.toTaskDto(task);


        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/reward-estimate")
    public ResponseEntity<RewardEstimateResponse> estimateReward(
            @Valid @RequestBody RewardEstimateRequest rewardEstimateRequest,
            Authentication authentication) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        return ResponseEntity.ok(taskService.estimateReward(currentUserId, rewardEstimateRequest));
    }

    @PostMapping("/{id}/claims")
    public ResponseEntity<StartTaskExpClaimResponse> claimStartExpReward(
            @PathVariable("id") UUID taskId,
            Authentication authentication
    ) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        StartTaskExpClaimResponse res = taskService.claimStartExpReward(currentUserId, taskId);

        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTaskOfUser(Authentication authentication) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        List<Tasks> taskList = taskService.getAllTasksByUserId(currentUserId);
        List<TaskDto> res = taskList.stream().map(TaskMapper::toTaskDto).toList();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable("id") UUID taskId,
            Authentication authentication
    ) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        Tasks task = taskService.getTaskByUserIdAndTaskId(currentUserId, taskId);

        TaskDto dto = TaskMapper.toTaskDto(task);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest,
            @PathVariable("id") UUID taskId,
            Authentication authentication
    ) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        Tasks updatedTask = taskService.updateTaskData(currentUserId, taskId, updateTaskRequest);

        TaskDto taskDto = TaskMapper.toTaskDto(updatedTask);

        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest,
            @PathVariable("id") UUID taskId,
            Authentication authentication) {

        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);
        Tasks updatedTask = taskService.updateTaskStatus(currentUserId, taskId, updateTaskStatusRequest);

        TaskDto dto = TaskMapper.toTaskDto(updatedTask);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") UUID taskId, Authentication authentication) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        taskService.deleteTask(currentUserId, taskId);

        return ResponseEntity.noContent().build();
    }

}

