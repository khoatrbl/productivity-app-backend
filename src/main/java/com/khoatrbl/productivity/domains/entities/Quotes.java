package com.khoatrbl.productivity.domains.entities;

import com.khoatrbl.productivity.domains.QuoteCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quotes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Quotes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String quote;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int calmExp;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPersist() {
        this.createdAt = LocalDateTime.now();
    }
}
