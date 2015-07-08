package io.mazur.fit.model;

import java.io.Serializable;

public abstract class LinkedRoutine implements Serializable {
    public abstract String getTitle();
    public abstract RoutineType getType();
}
