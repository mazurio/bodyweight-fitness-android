package io.mazur.fit.model;

public enum RoutineType {
    CATEGORY,
    SECTION,
    EXERCISE;

    private static RoutineType[] values = RoutineType.values();

    public static RoutineType fromInt(int value) {
        return values[value];
    }
}
