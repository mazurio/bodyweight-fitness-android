package io.mazur.fit.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmSection extends RealmObject {
    @PrimaryKey
    private String id;

    private RealmCategory realmCategory;
    private String title;
    private String mode;

    private RealmList<RealmExercise> exercises = new RealmList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRealmCategory(RealmCategory realmCategory) {
        this.realmCategory = realmCategory;
    }

    public RealmCategory getRealmCategory() {
        return realmCategory;
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

    public void setExercises(RealmList<RealmExercise> exercises) {
        this.exercises = exercises;
    }

    public RealmList<RealmExercise> getExercises() {
        return exercises;
    }
}
