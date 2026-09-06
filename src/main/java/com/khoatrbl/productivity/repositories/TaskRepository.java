package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, UUID> {
    List<Tasks> findAllByUserId(UUID userId);

}
