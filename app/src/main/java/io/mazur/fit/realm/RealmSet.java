package io.mazur.fit.realm;

import io.realm.RealmObject;

public class RealmSet extends RealmObject {
    private String id;

    private int numberOfReps;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setNumberOfReps(int numberOfReps) {
        this.numberOfReps = numberOfReps;
    }

    public int getNumberOfReps() {
        return numberOfReps;
    }
}
