package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.entities.Tasks;

import java.util.Optional;

public interface TaskEstimationService {
    Optional<Integer> estimateFromHistory(Tasks newTask);

    int estimateByPriorityBucket(Tasks newTask);

}
