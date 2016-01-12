package com.bodyweight.fitness.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Category extends LinkedRoutine implements Serializable {
    private String mCategoryId;

    private String mTitle;

    private ArrayList<Section> mSections = new ArrayList<>();

    public Category(String categoryId, String title) {
        mCategoryId = categoryId;
        mTitle = title;
    }

    public String getCategoryId() {
        return mCategoryId;
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
