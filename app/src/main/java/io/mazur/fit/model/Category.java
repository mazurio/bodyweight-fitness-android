package io.mazur.fit.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Category extends LinkedRoutine implements Serializable {
    private String mTitle;

    private ArrayList<Section> mSections = new ArrayList<>();

    public Category(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public RoutineType getType() {
        return RoutineType.CATEGORY;
    }

    public void insertSection(Section section) {
        section.setCategory(this);

        mSections.add(section);
    }

    public ArrayList<Section> getSections() {
        return mSections;
    }
}
