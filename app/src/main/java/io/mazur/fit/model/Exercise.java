package io.mazur.fit.model;

import java.io.Serializable;

public class Exercise extends LinkedRoutine implements Serializable {
    private String mId;
    private String mLevel;
    private String mTitle;
    private String mDescription;

    private boolean mAllowTimeReps = false;
    private boolean mAllowBodyweightReps = true;

    private int mDefaultNumberOfSets = 1;

    private Category mCategory;
    private Section mSection;

    private Exercise mPrevious;
    private Exercise mNext;

    public Exercise(String id, String level, String title, String description, boolean allowTimeReps, boolean allowBodyweightReps) {
        mId = id;
        mLevel = level;
        mTitle = title;
        mDescription = description;
        mAllowTimeReps = allowTimeReps;
        mAllowBodyweightReps = allowBodyweightReps;
    }

    public String getId() {
        return mId;
    }

    public String getLevel() {
        return mLevel;
    }

    public String getTitle() {
        return mTitle;
    }

    public RoutineType getType() {
        return RoutineType.EXERCISE;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean allowTimeReps() {
        return mAllowTimeReps;
    }

    public boolean allowBodyweightReps() {
        return mAllowBodyweightReps;
    }

    public int getDefaultNumberOfSets() {
        return mDefaultNumberOfSets;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setSection(Section section) {
        mSection = section;
    }

    public Section getSection() {
        return mSection;
    }

    public boolean isPrevious() {
        return mPrevious != null;
    }

    public void setPrevious(Exercise previous) {
        mPrevious = previous;
    }

    public Exercise getPrevious() {
        return mPrevious;
    }

    public boolean isNext() {
        return mNext != null;
    }

    public void setNext(Exercise next) {
        mNext = next;
    }

    public Exercise getNext() {
        return mNext;
    }
}