package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import com.khoatrbl.productivity.domains.dtos.*;
import com.khoatrbl.productivity.domains.entities.SubTasks;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.repositories.TaskRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.RewardCalculationService;
import com.khoatrbl.productivity.services.TaskEstimationService;
import com.khoatrbl.productivity.services.TaskService;
import com.khoatrbl.productivity.utilities.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final RewardCalculationService rewardCalculationService;
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
                .priority(createTaskRequest.getPriority())
                .status(Status.INCOMPLETE)
                .sprintInMinutes(createTaskRequest.getSprintInMinutes())
                .build();

        int estimateTime = rewardCalculationService.estimateTime(newTask);
        newTask.setEstimateMin(estimateTime);

        int totalExp = rewardCalculationService.calculateTasksTotalExp(createTaskRequest.getPriority(), estimateTime);
        newTask.setTotalExp(totalExp);

        int coins = rewardCalculationService.calculateCoinForTask(createTaskRequest.getPriority());
        newTask.setCoins(coins);

        List<SubTasks> subtasks = prepareSubTasksForCreate(newTask, createTaskRequest.getSubTasks(), totalExp);
        newTask.setSubTasks(subtasks);

        return taskRepository.save(newTask);
    }

    @Override
    public Tasks updateTaskData(UUID userId, UUID taskId, UpdateTaskRequest updateTaskRequest) {

        Tasks taskToUpdate = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Task not found for task id: " + taskId)
                );

        taskToUpdate.setTitle(updateTaskRequest.getTitle());
        taskToUpdate.setDescription(updateTaskRequest.getDescription());
        taskToUpdate.setDueDate(updateTaskRequest.getDueDate());
        taskToUpdate.setDueTime(updateTaskRequest.getDueTime());
        taskToUpdate.setPriority(updateTaskRequest.getPriority());
        taskToUpdate.setSprintInMinutes(updateTaskRequest.getSprintInMinutes());

        int estimateTime = rewardCalculationService.estimateTime(taskToUpdate);
        taskToUpdate.setEstimateMin(estimateTime);

        int totalExp = rewardCalculationService.calculateTasksTotalExp(updateTaskRequest.getPriority(), estimateTime);
        taskToUpdate.setTotalExp(totalExp);

        updateSubTasks(taskToUpdate, updateTaskRequest.getSubTasks(), totalExp);

        return taskRepository.save(taskToUpdate);
    }

    @Override
    public Tasks updateTaskStatus(UUID userId, UUID taskId, UpdateTaskStatusRequest updateTaskStatusRequest) {
        Tasks taskToUpdate = taskRepository.findByIdAndUserId(taskId, userId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Task not found for id: " + taskId)
                        );

        Status oldStatus = taskToUpdate.getStatus();
        Status newStatus = updateTaskStatusRequest.getTaskStatus();
        LocalDateTime now = LocalDateTime.now();

        boolean startingSession = newStatus == Status.IN_PROGRESS && oldStatus != Status.IN_PROGRESS;
        boolean closingSession = oldStatus == Status.IN_PROGRESS && newStatus != Status.IN_PROGRESS;

        if (startingSession) {
            if (taskToUpdate.getStartedAt() == null) {
                taskToUpdate.setStartedAt(now); // first-ever start, kept for record/UI only — no longer feeds duration math
            }
            taskToUpdate.setCurrentSessionStartedAt(now);

        } else if (closingSession && taskToUpdate.getCurrentSessionStartedAt() != null) {

            long sessionSeconds = Duration.between(taskToUpdate.getCurrentSessionStartedAt(), now).getSeconds();
            int previousTotal = taskToUpdate.getTotalFocusedSeconds() != null ? taskToUpdate.getTotalFocusedSeconds() : 0;

            taskToUpdate.setTotalFocusedSeconds(previousTotal + (int) Math.max(sessionSeconds, 0));
            taskToUpdate.setCurrentSessionStartedAt(null);
        }

        if (newStatus == Status.COMPLETE) {
            taskToUpdate.setCompletedAt(now);
        }

        taskToUpdate.setStatus(newStatus);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    public void deleteTask(UUID userId, UUID taskId) {
        Tasks taskToDelete = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Task not found for task id: " + taskId)
                );

        taskRepository.delete(taskToDelete);
    }

    @Override
    public RewardEstimateResponse estimateReward(UUID userId, RewardEstimateRequest rewardEstimateRequest) {
        // A reference proxy, not a real fetch — estimateFromHistory only ever
        // calls .getUser().getId() off this, so no need to load the full entity.
        Users userRef = userRepository.getReferenceById(userId);

        Tasks transientTask = Tasks.builder()
                .user(userRef)
                .title(rewardEstimateRequest.getTitle() != null ? rewardEstimateRequest.getTitle() : "")
                .priority(rewardEstimateRequest.getPriority())
                .build(); // never persisted — exists only to satisfy the estimation methods' signatures

        int estimatedMinutes = rewardCalculationService.estimateTime(transientTask);
        int totalExp = rewardCalculationService.calculateTasksTotalExp(rewardEstimateRequest.getPriority(), estimatedMinutes);
        int totalCoins = rewardCalculationService.calculateCoinForTask(rewardEstimateRequest.getPriority());
        List<Integer> subTaskExp = rewardCalculationService.calculateExpForSubTasks(totalExp, rewardEstimateRequest.getSubTaskCount());

        return RewardEstimateResponse.builder()
                .estimatedMinutes(estimatedMinutes)
                .totalExp(totalExp)
                .totalCoins(totalCoins)
                .subTaskExp(subTaskExp)
                .build();
    }

    private List<SubTasks> prepareSubTasksForCreate(Tasks task, List<CreateSubTaskRequest> createSubTaskRequests, int totalExp) {
        List<SubTasks> subtasks = new ArrayList<>();

        if (!createSubTaskRequests.isEmpty()) {
            subtasks = createSubTaskRequests.stream().map(
                    subtask -> SubTasks.builder()
                            .content(subtask.getContent())
                            .position(subtask.getPosition())
                            .isComplete(false)
                            .task(task)
                            .build()
            ).toList();
        }

        List<Integer> expForSubTasks = rewardCalculationService.calculateExpForSubTasks(totalExp, subtasks.size());

        for (int i = 0; i < subtasks.size(); i++) {
            int currentExp = expForSubTasks.get(i);

            subtasks.get(i).setExp(currentExp);
        }

        return subtasks;
    }

    private void updateSubTasks(Tasks task, List<UpdateSubTaskRequest> updateSubTaskRequests, int totalExp) {

        Map<UUID, SubTasks> existingSubTasks = task.getSubTasks()
                .stream()
                .collect(Collectors.toMap(
                        SubTasks::getId,
                        subTask -> subTask
                ));

        Set<UUID> incomingIds = updateSubTaskRequests.stream()
                .map(UpdateSubTaskRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SubTasks> existingSubtasks = task.getSubTasks();

        // Remove subtasks that are no longer present
        existingSubtasks.removeIf(
                subTask -> !incomingIds.contains(subTask.getId())
        );

        for (UpdateSubTaskRequest request : updateSubTaskRequests) {
            if (request.getId() == null) {
                SubTasks newSubTask = SubTasks.builder()
                        .content(request.getContent())
                        .position(request.getPosition())
                        .task(task)
                        .isComplete(false)
                        .build();

                existingSubtasks.add(newSubTask);
            } else {
                SubTasks existingSubtask = existingSubTasks.get(request.getId());

                if (existingSubtask == null) {
                    throw new EntityNotFoundException("Subtask not found for id: " + request.getId());
                }

                existingSubtask.setContent(request.getContent());
                existingSubtask.setComplete(request.isComplete());
                existingSubtask.setPosition(request.getPosition());

                existingSubtasks.add(existingSubtask);
            }
        }

        List<Integer> expForSubTasks = rewardCalculationService.calculateExpForSubTasks(totalExp, existingSubtasks.size());

        for (int i = 0; i < existingSubtasks.size(); i++) {
            int currentExp = expForSubTasks.get(i);

            existingSubtasks.get(i).setExp(currentExp);
        }

        task.setSubTasks(existingSubtasks);
    }

}
