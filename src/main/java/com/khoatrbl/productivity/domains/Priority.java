package com.khoatrbl.productivity.domains;

import lombok.Getter;

@Getter
public enum Priority {
    URGENT(4),
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int weight;

    Priority(int weight) {
        this.weight = weight;
    }

}
