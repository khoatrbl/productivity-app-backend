package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LevelRepository extends JpaRepository<Level, UUID> {
    Optional<Level> findByLevel(int level);
}
