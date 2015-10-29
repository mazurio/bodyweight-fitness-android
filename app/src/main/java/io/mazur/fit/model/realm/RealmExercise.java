package io.mazur.fit.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmExercise extends RealmObject {
    private String id;

    private String title;
    private String description;
    private String section; // todo: should be RealmObject
    private int sectionOrder;

    private RealmList<RealmSet> sets;

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

    public void setSection(String section) {
        this.section = section;
    }

    public String getSection() {
        return section;
    }

    /**
     * Used to determine the order of this exercise in the section, e.g. 1 or 2 or last, etc.
     */
    public void setSectionOrder(int sectionOrder) {
        this.sectionOrder = sectionOrder;
    }

    public int getSectionOrder() {
        return sectionOrder;
    }

    public void setSets(RealmList<RealmSet> sets) {
        this.sets = sets;
    }

    public RealmList<RealmSet> getSets() {
        return sets;
    }
}
