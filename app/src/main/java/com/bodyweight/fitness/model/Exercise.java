package com.bodyweight.fitness.model;

import java.io.Serializable;

public class Exercise extends LinkedRoutine implements Serializable {
    private String mExerciseId;
    private String mId;
    private String mLevel;
    private String mTitle;
    private String mDescription;
    private String mYouTubeId;
    private String mGifId;
    private String mGifUrl;
    private String mDefaultSet;

    private Category mCategory;
    private Section mSection;

    private Exercise mPrevious;
    private Exercise mNext;

    public Exercise(
            String exerciseId,
            String id,
            String level,
            String title,
            String description,
            String youTubeId,
            String gifId,
            String gifUrl,
            String defaultSet) {
        mExerciseId = exerciseId;
        mId = id;
        mLevel = level;
        mTitle = title;
        mDescription = description;
        mYouTubeId = youTubeId;
        mGifId = gifId;
        mGifUrl = gifUrl;
        mDefaultSet = defaultSet;
    }

    public String getExerciseId() {
        return mExerciseId;
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

    public String getYouTubeId() {
        return mYouTubeId;
    }

    public String getGifId() {
        return mGifId;
    }

    public String getGifUrl() {
        return mGifUrl;
    }

    public boolean isTimedSet() {
        return mDefaultSet.equalsIgnoreCase("timed");
    }

    public String getDefaultSet() {
        return mDefaultSet;
    }

    public boolean hasProgressions() {
        return (getSection().getSectionMode().equals(SectionMode.LEVELS) || getSection().getSectionMode().equals(SectionMode.PICK));
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