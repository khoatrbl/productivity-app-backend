package com.khoatrbl.productivity.domains.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    public static final Duration ABSOLUTE_TTL = Duration.ofDays(30);
    public static final Duration SLIDING_TTL = Duration.ofDays(7);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private Users user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant absoluteExpiresAt;

    @Column
    private Instant revokedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public boolean isUsable(Instant now) {
        return this.revokedAt == null
                && expiresAt.isAfter(now)
                && absoluteExpiresAt.isAfter(now);
    }

    public void extend(Instant now) {
        Instant slid = now.plus(SLIDING_TTL);
        this.expiresAt = slid.isBefore(this.absoluteExpiresAt) ? slid : absoluteExpiresAt;
    }

    public void revoke(Instant now) {
        if (this.revokedAt == null) {
            this.revokedAt = now;
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(SLIDING_TTL);
        this.absoluteExpiresAt = this.createdAt.plus(ABSOLUTE_TTL);
    }


}
