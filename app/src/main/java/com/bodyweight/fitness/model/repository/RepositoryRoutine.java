package com.bodyweight.fitness.model.repository;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RepositoryRoutine extends RealmObject {
    @PrimaryKey @Required
    private String id;

    @Index
    private String routineId;

    private String title;
    private String subtitle;

    @Index
    private Date startTime;

    @Index
    private Date lastUpdatedTime;

    private RealmList<RepositoryCategory> categories = new RealmList<>();
    private RealmList<RepositorySection> sections = new RealmList<>();
    private RealmList<RepositoryExercise> exercises = new RealmList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getRoutineId() {
        return routineId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubtitle() {
        return subtitle;
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

    public void setCategories(RealmList<RepositoryCategory> categories) {
        this.categories = categories;
    }

    public RealmList<RepositoryCategory> getCategories() {
        return categories;
    }

    public void setSections(RealmList<RepositorySection> sections) {
        this.sections = sections;
    }

    public RealmList<RepositorySection> getSections() {
        return sections;
    }

    public void setExercises(RealmList<RepositoryExercise> exercises) {
        this.exercises = exercises;
    }

    public RealmList<RepositoryExercise> getExercises() {
        return exercises;
    }
}
