package io.mazur.fit.model.repository;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RepositoryExercise extends RealmObject {
    @PrimaryKey
    private String id;

    private String title;
    private String description;
    private String defaultSet;

    private boolean visible = false;

    private RepositoryRoutine routine;
    private RepositoryCategory category;
    private RepositorySection section;

    private RealmList<RepositorySet> sets = new RealmList<>();

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDefaultSet(String defaultSet) {
        this.defaultSet = defaultSet;
    }

    public String getDefaultSet() {
        return defaultSet;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setRoutine(RepositoryRoutine routine) {
        this.routine = routine;
    }

    public RepositoryRoutine getRoutine() {
        return routine;
    }

    public void setCategory(RepositoryCategory category) {
        this.category = category;
    }

    public RepositoryCategory getCategory() {
        return category;
    }

    public void setSection(RepositorySection section) {
        this.section = section;
    }

    public RepositorySection getSection() {
        return section;
    }

    public void setSets(RealmList<RepositorySet> sets) {
        this.sets = sets;
    }

    public RealmList<RepositorySet> getSets() {
        return this.sets;
    }
}
