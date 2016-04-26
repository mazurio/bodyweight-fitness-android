package com.bodyweight.fitness.model.repository;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RepositoryCategory extends RealmObject {
    @PrimaryKey @Required
    private String id;

    @Index
    private String categoryId;

    private String title;

    private RepositoryRoutine routine;

    private RealmList<RepositorySection> sections = new RealmList<>();
    private RealmList<RepositoryExercise> exercises = new RealmList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setRoutine(RepositoryRoutine routine) {
        this.routine = routine;
    }

    public RepositoryRoutine getRoutine() {
        return routine;
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
