package io.mazur.fit.model.repository;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RepositoryRoutine extends RealmObject {
    @PrimaryKey
    private String id;

    @Index
    private Date startTime;

    /**
     * Last updated time for workout only during the same day.
     */
    @Index
    private Date lastUpdatedTime;

    private RealmList<RepositoryCategory> categories = new RealmList<>();
    private RealmList<RepositoryExercise> exercises = new RealmList<>();

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

    public RealmList<RepositoryCategory> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<RepositoryCategory> categories) {
        this.categories = categories;
    }

    public RealmList<RepositoryExercise> getExercises() {
        return exercises;
    }

    public void setExercises(RealmList<RepositoryExercise> exercises) {
        this.exercises = exercises;
    }
}
