package io.mazur.fit.model;

import java.io.Serializable;

public enum RoutineType implements Serializable {
    CATEGORY,
    SECTION,
    EXERCISE,
    EXERCISE_ACTIVE;

    private static RoutineType[] values = RoutineType.values();

    public static RoutineType fromInt(int value) {
        return values[value];
    }
}
