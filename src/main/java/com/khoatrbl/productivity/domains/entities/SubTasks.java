package com.khoatrbl.productivity.domains.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubTasks {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int exp;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private boolean isComplete;

    @ManyToOne
    @JoinColumn(name = "tasks_id", nullable = false)
    private Tasks task;
}
