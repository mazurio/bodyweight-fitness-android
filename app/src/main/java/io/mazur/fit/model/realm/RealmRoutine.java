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
    private Date startTime;

    /**
     * Last updated time for workout only during the same day.
     */
    @Index
    private Date lastUpdatedTime;

    private RealmList<RealmCategory> categories = new RealmList<>();
    private RealmList<RealmExercise> exercises = new RealmList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public RealmList<RealmCategory> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<RealmCategory> categories) {
        this.categories = categories;
    }

    public RealmList<RealmExercise> getExercises() {
        return exercises;
    }

    public void setExercises(RealmList<RealmExercise> exercises) {
        this.exercises = exercises;
    }
}
