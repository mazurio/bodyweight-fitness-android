package io.mazur.fit.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Section extends LinkedRoutine implements Serializable {
    private String mTitle;
    private String mDescription;
    private SectionMode mSectionMode;

    private Category mCategory;
    private int mCurrentLevel = 0;

    private ArrayList<Exercise> mExercises = new ArrayList<>();

    public Section(String title, String description, SectionMode sectionMode) {
        mTitle = title;
        mDescription = description;
        mSectionMode = sectionMode;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public RoutineType getType() {
        return RoutineType.SECTION;
    }

    public SectionMode getSectionMode() {
        return mSectionMode;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCurrentLevel(int level) {
        if(getSectionMode() == SectionMode.LEVELS || getSectionMode() == SectionMode.PICK) {
            if(level < 0 || level >= getExercises().size()) {
                return;
            }

            mCurrentLevel = level;
        }
    }

    public void setCurrentLevel(Exercise exercise) {
        int current = 0;
        for(Exercise ex : getExercises()) {
            if(ex.equals(exercise)) {
                mCurrentLevel = current;
                return;
            }

            current++;
        }
    }

    public int getCurrentLevel() {
        return mCurrentLevel;
    }

    public int getAvailableLevels() {
        if(getSectionMode() == SectionMode.LEVELS || getSectionMode() == SectionMode.PICK) {
            return getExercises().size();
        } else {
            return 0;
        }
    }

    public void insertExercise(Exercise exercise) {
        exercise.setCategory(mCategory);
        exercise.setSection(this);

        mExercises.add(exercise);
    }

    public Exercise getCurrentExercise() {
        return mExercises.get(mCurrentLevel);
    }

    public ArrayList<Exercise> getExercises() {
        return mExercises;
    }
}