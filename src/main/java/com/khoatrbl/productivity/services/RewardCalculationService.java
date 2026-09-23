package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.entities.Tasks;

import java.util.List;

public interface RewardCalculationService {
    int estimateTime(Tasks task);

    int roundToFriendlyInterval(int minutes);

    int calculateTasksTotalExp(Priority priority, int estimateTime);

    int calculateCoinForTask(Priority priority);

    List<Integer> calculateExpForSubTasks(int totalExp, int numberOfSubTasks);
}
