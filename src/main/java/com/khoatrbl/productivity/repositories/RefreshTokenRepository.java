package com.khoatrbl.productivity.repositories;

import com.khoatrbl.productivity.domains.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query("SELECT t FROM RefreshToken t JOIN FETCH t.user WHERE t.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHash(@Param("tokenHash") String tokenHash);

    List<RefreshToken> findAllByUserId(UUID userId);

    @Modifying
    @NativeQuery("DELETE FROM refresh_token WHERE revoked_at IS NOT NULL OR expires_at <= :now")
    int deleteExpiredTokens(@Param("now") Instant now);
}
