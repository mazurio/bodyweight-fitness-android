package io.mazur.fit.model.repository;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RepositorySet extends RealmObject {
    @PrimaryKey
    private String id;

    private boolean isTimed = false;

    private double weight = 0;
    private int reps = 0;
    private int seconds = 0;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setIsTimed(boolean isTimed) {
        this.isTimed = isTimed;
    }

    public boolean isTimed() {
        return this.isTimed;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getReps() {
        return this.reps;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return this.seconds;
    }
}
