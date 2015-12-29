package io.mazur.fit.model.repository;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RepositorySection extends RealmObject {
    @PrimaryKey
    private String id;

    private RepositoryCategory repositoryCategory;
    private String title;
    private String mode;

    private RealmList<RepositoryExercise> exercises = new RealmList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRepositoryCategory(RepositoryCategory repositoryCategory) {
        this.repositoryCategory = repositoryCategory;
    }

    public RepositoryCategory getRepositoryCategory() {
        return repositoryCategory;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setExercises(RealmList<RepositoryExercise> exercises) {
        this.exercises = exercises;
    }

    public RealmList<RepositoryExercise> getExercises() {
        return exercises;
    }
}
