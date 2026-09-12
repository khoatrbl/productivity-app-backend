package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import com.khoatrbl.productivity.domains.dtos.CreateTaskRequest;
import com.khoatrbl.productivity.domains.dtos.SubTaskDto;
import com.khoatrbl.productivity.domains.dtos.UpdateTaskRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateTaskStatusRequest;
import com.khoatrbl.productivity.domains.entities.SubTasks;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.repositories.SubTaskRepository;
import com.khoatrbl.productivity.repositories.TaskRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.TaskEstimationService;
import com.khoatrbl.productivity.services.TaskService;
import com.khoatrbl.productivity.utilities.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskEstimationService taskEstimationService;
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
                .build();

        int estimateTime = this.estimateTime(newTask);
        newTask.setEstimateMin(estimateTime);

        int totalExp = this.calculateTaskTotalExp(createTaskRequest.getPriority(), estimateTime);
        newTask.setTotalExp(totalExp);

        List<SubTasks> subtasks = new ArrayList<>();

        if (!createTaskRequest.getSubTasks().isEmpty()) {
            subtasks = createTaskRequest.getSubTasks().stream().map(
                    subtask -> SubTasks.builder()
                            .content(subtask.getContent())
                            .position(subtask.getPosition())
                            .isComplete(false)
                            .task(newTask)
                            .build()
            ).toList();
        }

        List<Integer> expForSubTasks = this.calculateExpForSubTasks(totalExp, subtasks.size());

        for (int i = 0; i < subtasks.size(); i++) {
            int currentExp = expForSubTasks.get(i);

            subtasks.get(i).setExp(currentExp);
        }

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

        int estimateTime = this.estimateTime(taskToUpdate);
        taskToUpdate.setEstimateMin(estimateTime);
        taskToUpdate.setTotalExp(this.calculateTaskTotalExp(updateTaskRequest.getPriority(), estimateTime));

        return taskRepository.save(taskToUpdate);
    }

    @Override
    public Tasks updateTaskStatus(UUID userId, UUID taskId, UpdateTaskStatusRequest updateTaskStatusRequest) {
        Tasks taskToUpdate = taskRepository.findByIdAndUserId(taskId, userId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Task not found for id: " + taskId)
                        );

        Status newStatus = updateTaskStatusRequest.getTaskStatus();

        switch (newStatus) {
            case IN_PROGRESS:
                if (taskToUpdate.getStartedAt() == null) {
                    taskToUpdate.setStartedAt(LocalDateTime.now());
                }
                taskToUpdate.setStatus(Status.IN_PROGRESS);
                break;

            case COMPLETE:
                if (taskToUpdate.getCompletedAt() == null) {
                    taskToUpdate.setCompletedAt(LocalDateTime.now());
                }
                taskToUpdate.setStatus(Status.COMPLETE);
                break;

            case INCOMPLETE:
                taskToUpdate.setStatus(Status.INCOMPLETE);
                taskToUpdate.setCompletedAt(null);
                break;
        }

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

    private int estimateTime(Tasks newTask) {
        int estimation = taskEstimationService.estimateFromHistory(newTask)
                .orElseGet(
                        () -> taskEstimationService.estimateByPriorityBucket(newTask)
                );

        return roundToFriendlyInterval(estimation);
    }

    private int roundToFriendlyInterval(int minutes) {
        int bucketSize = 0;

        if (minutes <= 0) {
            return bucketSize;
        }

        if (minutes <= 15) {
            bucketSize = 5;
        } else if (minutes <= 60) {
            bucketSize = 15;
        } else if (minutes <= 180) {
            bucketSize = 30;   // 90, 120, 150, 180
        } else {
            bucketSize = 60;   // 240, 300, 360... hourly beyond 3h
        }

        return (int) (Math.ceil((double) minutes / bucketSize) * bucketSize);
    }

    private int calculateTaskTotalExp(Priority priority, int estimatedTime) {
        // formula:
        // total = base * priorityIndex + estimatedTime

        int baseExp = 10;
        return baseExp * priority.getWeight() + estimatedTime;
    }

    private List<Integer> calculateExpForSubTasks(int totalExp, int numberOfSubTasks) {
        if (numberOfSubTasks <= 0) {
            return new ArrayList<>();
        }

        int baseExp = totalExp / numberOfSubTasks;
        int remainder = totalExp % numberOfSubTasks;

        List<Integer> expValues = new ArrayList<>();

        for (int i = 0; i < numberOfSubTasks; i++) {
            int exp = baseExp;

            if (i < remainder) {
                exp++;
            }

            expValues.add(exp);
        }

        return expValues;
    }
}
