package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.Quotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quotes, UUID> {

    @Query(value = "SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Quotes> findRandomQuote();
}
