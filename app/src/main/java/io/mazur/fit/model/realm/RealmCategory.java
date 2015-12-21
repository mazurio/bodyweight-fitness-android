package io.mazur.fit.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmCategory extends RealmObject {
    @PrimaryKey
    private String id;

    private RealmRoutine realmRoutine;

    private String title;

    private RealmList<RealmSection> sections = new RealmList<>();
    private RealmList<RealmExercise> exercises = new RealmList<>();

    public void setRealmRoutine(RealmRoutine realmRoutine) {
        this.realmRoutine = realmRoutine;
    }

    public RealmRoutine getRealmRoutine() {
        return realmRoutine;
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

    public void setSections(RealmList<RealmSection> sections) {
        this.sections = sections;
    }

    public RealmList<RealmSection> getSections() {
        return sections;
    }

    public void setExercises(RealmList<RealmExercise> exercises) {
        this.exercises = exercises;
    }

    public RealmList<RealmExercise> getExercises() {
        return exercises;
    }
}
