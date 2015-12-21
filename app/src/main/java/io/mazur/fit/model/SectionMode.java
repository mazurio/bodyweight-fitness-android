package io.mazur.fit.model;

import java.io.Serializable;

public enum SectionMode implements Serializable {
    PICK("pick"),
    ALL("all"),
    LEVELS("levels");

    private String mAsString;

    SectionMode(String asString) {
        mAsString = asString;
    }

    @Override
    public String toString() {
        return mAsString;
    }
}