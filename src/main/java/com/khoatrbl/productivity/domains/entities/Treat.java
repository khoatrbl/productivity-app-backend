package com.khoatrbl.productivity.domains.entities;

import com.khoatrbl.productivity.domains.TreatTier;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "treats")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Treat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String treatName;

    @Column(nullable = false)
    private int exp;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TreatTier treatTier;

}
