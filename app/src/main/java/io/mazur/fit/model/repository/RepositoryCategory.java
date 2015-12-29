package io.mazur.fit.model.repository;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RepositoryCategory extends RealmObject {
    @PrimaryKey
    private String id;

    private RepositoryRoutine repositoryRoutine;

    private String title;

    private RealmList<RepositorySection> sections = new RealmList<>();
    private RealmList<RepositoryExercise> exercises = new RealmList<>();

    public void setRepositoryRoutine(RepositoryRoutine repositoryRoutine) {
        this.repositoryRoutine = repositoryRoutine;
    }

    public RepositoryRoutine getRepositoryRoutine() {
        return repositoryRoutine;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
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
