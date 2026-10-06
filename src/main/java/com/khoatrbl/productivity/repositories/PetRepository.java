package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.Pets;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pets, UUID> {
    Optional<Pets> findByOwnerId(UUID ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pets p WHERE p.owner.id = :ownerId")
    Optional<Pets> findByOwnerIdForUpdate(@Param("ownerId") UUID ownerId);

}
