package com.khoatrbl.productivity.domains.entities;

import com.khoatrbl.productivity.domains.Priority;
import com.khoatrbl.productivity.domains.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalTime dueTime;

    @Column(nullable = false)
    private int estimateMin;

    @Column(nullable = false)
    private int totalExp;

    @Column(nullable = false)
    private int coins;

    @Column(nullable = false)
    private int sprintInMinutes;

    @Column(nullable = false)
    private boolean startExpClaimed;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTasks> subTasks = new ArrayList<>();

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private Integer totalFocusedSeconds; // accumulated real focus time, never includes paused gaps

    @Column
    private LocalDateTime currentSessionStartedAt; // set on IN_PROGRESS, cleared on pause/complete

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }






}
