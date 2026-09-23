package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.services.RewardCalculationService;
import com.khoatrbl.productivity.services.TaskEstimationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardCalculationServiceImpl implements RewardCalculationService {

    private final TaskEstimationService taskEstimationService;

    private static final int BASE_EXP = 10;
    private static final int BASE_COINS = 5;

    @Override
    public int estimateTime(Tasks task) {
        int estimation = taskEstimationService.estimateFromHistory(task)
                .orElseGet(
                        () -> taskEstimationService.estimateByPriorityBucket(task)
                );

        return roundToFriendlyInterval(estimation);
    }

    @Override
    public int roundToFriendlyInterval(int minutes) {
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

    @Override
    public int calculateTasksTotalExp(Priority priority, int estimateTime) {
        // formula:
        // total = base * priorityIndex + estimatedTime

        return BASE_EXP * priority.getWeight() + estimateTime;
    }

    @Override
    public int calculateCoinForTask(Priority priority) {
        return (int) Math.ceil((double) (BASE_COINS * priority.getWeight()) / 2);
    }

    @Override
    public List<Integer> calculateExpForSubTasks(int totalExp, int numberOfSubTasks) {
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
