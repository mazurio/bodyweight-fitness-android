package io.mazur.fit.model;

public enum SectionMode {
    PICK,
    ALL,
    LEVELS;

    private static SectionMode[] values = SectionMode.values();

    public static SectionMode fromInt(int value) {
        return values[value];
    }
}