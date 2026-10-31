package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.components.LateRewardPolicy;
import com.khoatrbl.productivity.domains.Status;
import com.khoatrbl.productivity.domains.dtos.*;
import com.khoatrbl.productivity.domains.entities.SubTasks;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.InvalidDueDateException;
import com.khoatrbl.productivity.exceptions.InvalidDueTimeException;
import com.khoatrbl.productivity.exceptions.TaskNotInProgressException;
import com.khoatrbl.productivity.mappers.ProfileMapper;
import com.khoatrbl.productivity.mappers.TaskMapper;
import com.khoatrbl.productivity.repositories.TaskRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.PetService;
import com.khoatrbl.productivity.services.RewardCalculationService;
import com.khoatrbl.productivity.services.TaskService;
import com.khoatrbl.productivity.services.UserService;
import com.khoatrbl.productivity.utilities.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final RewardCalculationService rewardCalculationService;
    private final UserService userService;
    private final PetService petService;
    private final UserRepository userRepository;
    private final LateRewardPolicy lateRewardPolicy;

    private static final int START_TASK_EXP = 15;
    private static final double BONUS_PET_CHANCE = 0.4; // 40% chance
    private static final int MIN_BONUS_PETS = 1;
    private static final int MAX_BONUS_PETS_PER_TASK = 2;

    @Override
    public List<Tasks> getAllTasksByUserId(UUID userId) {
        return taskRepository.findAllByUserId(userId);
    }

    @Override
    public Tasks getTaskByUserIdAndTaskId(UUID userId, UUID taskId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found for task id: " + taskId));
    }

    @Override
    @Transactional
    public StartTaskExpClaimResponse claimStartExpReward(UUID userId, UUID taskId) {
        // Locked, so two quick "start" calls can't both claim the reward
        Tasks task = taskRepository.findByIdAndUserIdForUpdate(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found for task id: " + taskId));

        if (task.isStartExpClaimed()) {
            return StartTaskExpClaimResponse.builder()
                    .claimed(false)
                    .expGranted(0)
                    .profile(null)
                    .build();
        }

        task.setStartExpClaimed(true);

        UpdateExpRequest updateExpRequest = UpdateExpRequest.builder()
                .expGained(START_TASK_EXP)
                .build();

        Users user = userService.updateUserLevel(userId, updateExpRequest);
        ProfileDto profile = ProfileMapper.toDto(user);

        return StartTaskExpClaimResponse.builder()
                .claimed(true)
                .expGranted(START_TASK_EXP)
                .profile(profile)
                .build();
    }

    @Override
    @Transactional
    public Tasks createTask(UUID userId, CreateTaskRequest createTaskRequest) {
        Users owner = userService.getUserById(userId);

        LocalDate requestedDate = createTaskRequest.getDueDate();
        LocalTime requestedTime = createTaskRequest.getDueTime();
        validateDueDateTime(requestedDate, requestedTime, owner.getTimezone());

        Tasks newTask = Tasks.builder()
                .user(owner)
                .title(StringUtils.normalizeTitle(createTaskRequest.getTitle()))
                .description(StringUtils.normalizeDescription(createTaskRequest.getDescription()))
                .dueDate(requestedDate)
                .dueTime(requestedTime)
                // The deadline rewards are measured against. Never changes after this.
                .originalDueDate(requestedDate)
                .originalDueTime(requestedTime)
                .priority(createTaskRequest.getPriority())
                .status(Status.INCOMPLETE)
                .sprintInMinutes(createTaskRequest.getSprintInMinutes())
                .startExpClaimed(false)
                .completionRewardClaimed(false)
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
    @Transactional
    public Tasks updateTaskData(UUID userId, UUID taskId, UpdateTaskRequest updateTaskRequest) {
        Tasks taskToUpdate = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found for task id: " + taskId));

        LocalDate newDate = updateTaskRequest.getDueDate();
        LocalTime newTime = updateTaskRequest.getDueTime();

        boolean scheduleChanged =
                !newDate.equals(taskToUpdate.getDueDate()) || !newTime.equals(taskToUpdate.getDueTime());

        if (scheduleChanged) {
            validateDueDateTime(newDate, newTime, taskToUpdate.getUser().getTimezone());
        }

        taskToUpdate.setTitle(StringUtils.normalizeTitle(updateTaskRequest.getTitle()));
        taskToUpdate.setDescription(StringUtils.normalizeDescription(updateTaskRequest.getDescription()));
        // Only the current deadline moves; originalDueDate/originalDueTime stay as created
        taskToUpdate.setDueDate(newDate);
        taskToUpdate.setDueTime(newTime);
        taskToUpdate.setPriority(updateTaskRequest.getPriority());
        taskToUpdate.setSprintInMinutes(updateTaskRequest.getSprintInMinutes());

        int estimateTime = rewardCalculationService.estimateTime(taskToUpdate);
        taskToUpdate.setEstimateMin(estimateTime);

        int totalExp = rewardCalculationService.calculateTasksTotalExp(updateTaskRequest.getPriority(), estimateTime);
        taskToUpdate.setTotalExp(totalExp);

        int coins = rewardCalculationService.calculateCoinForTask(updateTaskRequest.getPriority());
        taskToUpdate.setCoins(coins);

        updateSubTasks(taskToUpdate, updateTaskRequest.getSubTasks(), totalExp);

        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public TaskDto updateTaskStatus(UUID userId, UUID taskId, UpdateTaskStatusRequest updateTaskStatusRequest) {
        // Lock order everywhere: task → user → pet (prevents deadlocks)
        Tasks taskToUpdate = taskRepository.findByIdAndUserIdForUpdate(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found for id: " + taskId));

        Status oldStatus = taskToUpdate.getStatus();
        Status newStatus = updateTaskStatusRequest.getTaskStatus();
        LocalDateTime now = LocalDateTime.now();

        boolean startingSession = newStatus == Status.IN_PROGRESS && oldStatus != Status.IN_PROGRESS;
        boolean closingSession = oldStatus == Status.IN_PROGRESS && newStatus != Status.IN_PROGRESS;

        if (startingSession) {
            if (taskToUpdate.getStartedAt() == null) {
                taskToUpdate.setStartedAt(now); // first-ever start, kept for record/UI only
            }
            taskToUpdate.setCurrentSessionStartedAt(now);

        } else if (closingSession && taskToUpdate.getCurrentSessionStartedAt() != null) {
            long sessionSeconds = Duration.between(taskToUpdate.getCurrentSessionStartedAt(), now).getSeconds();
            int previousTotal = taskToUpdate.getTotalFocusedSeconds() != null ? taskToUpdate.getTotalFocusedSeconds() : 0;

            taskToUpdate.setTotalFocusedSeconds(previousTotal + (int) Math.max(sessionSeconds, 0));
            taskToUpdate.setCurrentSessionStartedAt(null);
        }

        int bonusPetsGranted = 0;
        int expGranted = 0;
        int coinsGranted = 0;
        int rewardPercent = 100;
        long lateMinutes = 0;
        ProfileDto profile = null;

        if (newStatus == Status.COMPLETE
                && oldStatus != Status.COMPLETE
                && !taskToUpdate.isCompletionRewardClaimed()) {

            taskToUpdate.setCompletedAt(now);
            taskToUpdate.setCompletionRewardClaimed(true);

            // Finishing the task finishes every step
            taskToUpdate.getSubTasks().forEach(subTask -> subTask.setComplete(true));

            // Lateness is measured against the ORIGINAL deadline (option A),
            // falling back to the current one for any row the backfill missed
            LocalDate baseDate = taskToUpdate.getOriginalDueDate() != null
                    ? taskToUpdate.getOriginalDueDate() : taskToUpdate.getDueDate();
            LocalTime baseTime = taskToUpdate.getOriginalDueTime() != null
                    ? taskToUpdate.getOriginalDueTime() : taskToUpdate.getDueTime();

            lateMinutes = lateRewardPolicy.minutesLate(baseDate, baseTime, taskToUpdate.getUser().getTimezone());
            rewardPercent = lateRewardPolicy.percentFor(lateMinutes);

            // Rewards are decided here, from the stored task values. The client never sends amounts.
            expGranted = lateRewardPolicy.apply(taskToUpdate.getTotalExp(), rewardPercent);
            coinsGranted = lateRewardPolicy.apply(taskToUpdate.getCoins(), rewardPercent);

            Users user = userService.grantTaskReward(userId, expGranted, coinsGranted);
            profile = ProfileMapper.toDto(user);

            if (ThreadLocalRandom.current().nextDouble() < BONUS_PET_CHANCE) {
                int rolled = ThreadLocalRandom.current().nextInt(MIN_BONUS_PETS, MAX_BONUS_PETS_PER_TASK + 1);
                bonusPetsGranted = petService.grantBonusPets(userId, rolled);
            }
        }

        taskToUpdate.setStatus(newStatus);

        TaskDto dto = TaskMapper.toTaskDto(taskToUpdate);
        dto.setBonusPetsGranted(bonusPetsGranted);
        dto.setExpGranted(expGranted);
        dto.setCoinsGranted(coinsGranted);
        dto.setRewardPercent(rewardPercent);
        dto.setLateMinutes(lateMinutes);
        dto.setProfile(profile); // null unless this call completed the task

        return dto;
    }

    @Override
    @Transactional
    public void deleteTask(UUID userId, UUID taskId) {
        Tasks taskToDelete = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found for task id: " + taskId));

        taskRepository.delete(taskToDelete);
    }

    @Override
    public RewardEstimateResponse estimateReward(UUID userId, RewardEstimateRequest rewardEstimateRequest) {
        // A reference proxy, not a real fetch: estimateFromHistory only ever
        // calls .getUser().getId() off this, so no need to load the full entity.
        Users userRef = userRepository.getReferenceById(userId);

        Tasks transientTask = Tasks.builder()
                .user(userRef)
                .title(rewardEstimateRequest.getTitle() != null ? rewardEstimateRequest.getTitle() : "")
                .priority(rewardEstimateRequest.getPriority())
                .build(); // never persisted; exists only to satisfy the estimation methods' signatures

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

    @Override
    @Transactional
    public Tasks updateSubTaskStatus(UUID userId, UUID taskId, UUID subTaskId, UpdateSubTaskStatusRequest request) {
        Tasks task = taskRepository.findByIdAndUserId(taskId, userId) // ownership check
                .orElseThrow(() -> new EntityNotFoundException("Task not found for task id: " + taskId));

        // Steps can only be ticked while the task is being worked on
        if (task.getStatus() != Status.IN_PROGRESS) {
            throw new TaskNotInProgressException("Start the task before ticking off its steps.");
        }

        SubTasks subTask = task.getSubTasks().stream()
                .filter(s -> s.getId().equals(subTaskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found for id: " + subTaskId));

        subTask.setComplete(request.getIsComplete());
        return task; // managed entity: saved on commit
    }

    /* ---------- private helpers ---------- */

    private List<SubTasks> prepareSubTasksForCreate(Tasks task, List<CreateSubTaskRequest> createSubTaskRequests, int totalExp) {
        List<CreateSubTaskRequest> incoming = createSubTaskRequests != null ? createSubTaskRequests : List.of();

        // A mutable list: Hibernate needs to be able to manage it later (orphan removal, sorting)
        List<SubTasks> subtasks = incoming.stream()
                .map(subtask -> SubTasks.builder()
                        .content(subtask.getContent())
                        .position(subtask.getPosition())
                        .isComplete(false)
                        .task(task)
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        subtasks.sort(Comparator.comparingInt(SubTasks::getPosition));

        List<Integer> expForSubTasks = rewardCalculationService.calculateExpForSubTasks(totalExp, subtasks.size());
        for (int i = 0; i < subtasks.size(); i++) {
            subtasks.get(i).setExp(expForSubTasks.get(i));
        }

        return subtasks;
    }

    private void updateSubTasks(Tasks task, List<UpdateSubTaskRequest> requests, int totalExp) {
        List<UpdateSubTaskRequest> incoming = requests != null ? requests : List.of();
        List<SubTasks> subTasks = task.getSubTasks(); // the managed collection; mutate it, never replace it

        Map<UUID, SubTasks> existingById = subTasks.stream()
                .collect(Collectors.toMap(SubTasks::getId, s -> s));

        List<UUID> incomingIdList = incoming.stream()
                .map(UpdateSubTaskRequest::getId)
                .filter(Objects::nonNull)
                .toList();
        Set<UUID> incomingIds = new HashSet<>(incomingIdList);

        // The same subtask twice in one request is never valid
        if (incomingIds.size() != incomingIdList.size()) {
            throw new IllegalArgumentException("Duplicate subtask ids in request.");
        }

        // 1. Remove subtasks the client no longer sends (orphanRemoval deletes the rows)
        subTasks.removeIf(s -> !incomingIds.contains(s.getId()));

        // 2. Update existing ones in place, add new ones.
        //    Completion status is NOT taken from this request: steps are ticked only
        //    through updateSubTaskStatus while the task is in progress.
        for (UpdateSubTaskRequest request : incoming) {
            if (request.getId() == null) {
                subTasks.add(SubTasks.builder()
                        .content(request.getContent())
                        .position(request.getPosition())
                        .task(task)
                        .isComplete(false)
                        .build());
            } else {
                SubTasks existing = existingById.get(request.getId());
                if (existing == null) {
                    throw new EntityNotFoundException("Subtask not found for id: " + request.getId());
                }
                existing.setContent(request.getContent());
                existing.setPosition(request.getPosition());
                // no add(): it's already in the list
            }
        }

        // 3. Keep the list in position order, so EXP is assigned to the right steps
        subTasks.sort(Comparator.comparingInt(SubTasks::getPosition));

        // 4. Redistribute EXP across the final set
        List<Integer> expForSubTasks = rewardCalculationService.calculateExpForSubTasks(totalExp, subTasks.size());
        for (int i = 0; i < subTasks.size(); i++) {
            subTasks.get(i).setExp(expForSubTasks.get(i));
        }
    }

    private void validateDueDateTime(LocalDate dueDate, LocalTime dueTime, String timezone) {
        ZonedDateTime nowInUserZone = ZonedDateTime.now(ZoneId.of(timezone));
        LocalDate today = nowInUserZone.toLocalDate();
        LocalTime now = nowInUserZone.toLocalTime();

        if (dueDate.isBefore(today)) {
            throw new InvalidDueDateException("Due date cannot be in the past.");
        }
        if (dueDate.isEqual(today) && dueTime.isBefore(now)) {
            throw new InvalidDueTimeException("Due time cannot be in the past.");
        }
    }
}