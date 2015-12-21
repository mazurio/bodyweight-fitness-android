package io.mazur.fit.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmExercise extends RealmObject {
    @PrimaryKey
    private String id;

    private String title;
    private String description;
    private String defaultSet;

    private boolean visible = false;

    private RealmCategory category;
    private RealmSection section;

    private RealmList<RealmSet> sets = new RealmList<>();

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

    public RealmCategory getCategory() {
        return category;
    }

    public void setCategory(RealmCategory category) {
        this.category = category;
    }

    public RealmSection getSection() {
        return section;
    }

    public void setSection(RealmSection section) {
        this.section = section;
    }

    public void setSets(RealmList<RealmSet> sets) {
        this.sets = sets;
    }

    public RealmList<RealmSet> getSets() {
        return this.sets;
    }
}
