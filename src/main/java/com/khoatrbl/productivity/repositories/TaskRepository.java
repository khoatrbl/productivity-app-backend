package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import com.khoatrbl.productivity.domains.entities.Tasks;
import com.khoatrbl.productivity.domains.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, UUID> {
    List<Tasks> findAllByUserId(UUID userId);

    Optional<Tasks> findByIdAndUserId(UUID taskId, UUID userId);

    List<Tasks> findByUserIdAndStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(UUID userId, Status status);

    List<Tasks> findByPriorityAndStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(Priority priority, Status status);

    List<Tasks> findByUserIdAndPriorityAndStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(UUID userId, Priority priority, Status status);
}
