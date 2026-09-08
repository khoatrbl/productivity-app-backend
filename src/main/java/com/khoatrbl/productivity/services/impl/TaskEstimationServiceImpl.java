package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.repositories.TaskRepository;
import com.khoatrbl.productivity.services.TaskEstimationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskEstimationServiceImpl implements TaskEstimationService {

    private final TaskRepository taskRepository;
    private final Map<Priority, Integer> HARD_DEFAULTS = Map.of(
            Priority.URGENT, 90,
            Priority.HIGH, 60,
            Priority.MEDIUM, 45,
            Priority.LOW, 20
    );

    @Override
    public Optional<Integer> estimateFromHistory(Tasks newTask) {
        List<Tasks> completedTasks = taskRepository
                .findByUserIdAndStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(
                        newTask.getUser().getId(),
                        Status.COMPLETE
                );

        // 1. Exact title match (normalized) — cheapest, most reliable
        List<Tasks> exactMatches = completedTasks.stream().filter(
                task -> normalize(task.getTitle()).equals(normalize(newTask.getTitle()))
        ).toList();

        if (!exactMatches.isEmpty()) {
            return Optional.of(recencyWeightedAverage(exactMatches));
        }

        // 2. Fuzzy match via token overlap (Jaccard similarity) — catches
        // "clean the house" vs "clean my house" without any NLP
        List<Tasks> fuzzy = completedTasks.stream()
                .filter(t -> jaccardSimilarity(t.getTitle(), newTask.getTitle()) >= 0.5)
                .toList();

        if (!fuzzy.isEmpty()) {
            return Optional.of(recencyWeightedAverage(fuzzy));
        }

        return Optional.empty(); // cold start, fall back to priority-bucket median
    }

    @Override
    public int estimateByPriorityBucket(Tasks newTask) {
        // Tier 1: this user's own history at this priority
        List<Long> personal = actualDurations(
                taskRepository.findByUserIdAndPriorityAndStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(
                        newTask.getUser().getId(), newTask.getPriority(), Status.COMPLETE)
        );

        if (personal.size() >= 3) {
            return (int) median(personal);
        }

        // Tier 2: global history at this priority (cross-user)
        List<Long> global = actualDurations(
                taskRepository.findByPriorityAndStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(
                        newTask.getPriority(), Status.COMPLETE)
        );

        if (!global.isEmpty()) {
            return (int) median(global);
        }

        // Tier 3: true cold start — no data exists anywhere yet
        return HARD_DEFAULTS.get(newTask.getPriority());
    }

    private int recencyWeightedAverage(List<Tasks> matches) {

        List<Tasks> sorted = new ArrayList<>(matches); // defensive copy — never assume caller's list is mutable
        sorted.sort(Comparator.comparing(Tasks::getCompletedAt));

        double weightSum = 0;
        double valueSum = 0;
        double decay = 0.7;

        for (int i = 0; i < matches.size(); i++) {
            double weight = Math.pow(decay, matches.size() - 1 - i);
            long minutes = Duration.between(
                    matches.get(i).getStartedAt(),
                    matches.get(i).getCompletedAt()
            ).toMinutes();

            valueSum += weight * minutes;
            weightSum += weight;
        }
        return (int) Math.round(valueSum / weightSum);
    }

    private double jaccardSimilarity(String a, String b) {
        Set<String> tokensA = tokenSet(a);
        Set<String> tokensB = tokenSet(b);
        Set<String> intersection = new HashSet<>(tokensA);
        intersection.retainAll(tokensB);
        Set<String> union = new HashSet<>(tokensA);
        union.addAll(tokensB);
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    private Set<String> tokenSet(String text) {
        return Arrays.stream(normalize(text).split("\\s+"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }

    private String normalize(String s) {
        return s.toLowerCase(Locale.ROOT).trim();
    }

    private List<Long> actualDurations(List<Tasks> tasks) {
        return tasks.stream()
                .map(task -> Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes())
                .filter(duration -> duration > 0)
                .sorted()
                .toList();
    }

    private double median(List<Long> sorted) {
        int n = sorted.size(), mid = n / 2;
        return n % 2 == 0 ? (sorted.get(mid - 1) + sorted.get(mid)) / 2.0 : sorted.get(mid);
    }



}
