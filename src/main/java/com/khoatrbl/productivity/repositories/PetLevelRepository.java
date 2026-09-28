package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.PetLevels;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PetLevelRepository extends JpaRepository<PetLevels, UUID> {
    Optional<PetLevels> findByLevel(int level);
}
