package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.Pets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pets, UUID> {
    Optional<Pets> findByOwnerId(UUID ownerId);
}
