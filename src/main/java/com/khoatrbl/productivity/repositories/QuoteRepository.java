package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.Quotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quotes, UUID> {
    long count();

    @Query(value = "SELECT * FROM quotes ORDER BY id LIMIT 1 OFFSET :offset", nativeQuery = true)
    Optional<Quotes> findByOffset(@Param("offset") long offset);
}
