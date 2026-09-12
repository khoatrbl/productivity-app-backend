package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.SubTasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTasks, UUID> {
}
