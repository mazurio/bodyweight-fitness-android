package io.mazur.fit.model;

import java.io.Serializable;

public enum SectionMode implements Serializable {
    PICK,
    ALL,
    LEVELS;

    private static SectionMode[] values = SectionMode.values();

    public static SectionMode fromInt(int value) {
        return values[value];
    }
}