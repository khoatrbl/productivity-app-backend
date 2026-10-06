package com.khoatrbl.productivity.domains.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Pets {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Users owner;

    @ManyToOne
    @JoinColumn(name = "pet_level_id", nullable = false)
    private PetLevels petLevel;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int petCurrentExp;

    @Column
    private int currentAffectionPoint;

    @Column(nullable = false)
    private int pettingsLeft = 5;

    @Column
    private Instant petCooldownUntil; // null = not napping

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetItems> items = new ArrayList<>();
}
