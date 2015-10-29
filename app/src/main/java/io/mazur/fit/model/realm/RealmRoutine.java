package io.mazur.fit.model.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RealmRoutine extends RealmObject {
    @PrimaryKey
    private String id;

    @Index
    private Date date;

    private RealmList<RealmExercise> exercises;

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public RealmList<RealmExercise> getExercises() {
        return exercises;
    }

    public void setExercises(RealmList<RealmExercise> exercises) {
        this.exercises = exercises;
    }
}
