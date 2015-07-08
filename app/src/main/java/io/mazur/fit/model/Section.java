package io.mazur.fit.model;

import java.util.ArrayList;

public class Section extends LinkedRoutine {
    private String mTitle;
    private SectionMode mSectionMode;

    private Category mCategory;
    private int mCurrentLevel = 0;

    private ArrayList<Exercise> mExercises = new ArrayList<>();

    public Section(String title, SectionMode sectionMode) {
        mTitle = title;
        mSectionMode = sectionMode;
    }

    public String getTitle() {
        return mTitle;
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

    public void levelUp() {
        if(getSectionMode() == SectionMode.LEVELS) {
            if((mCurrentLevel + 1) < 0 || (mCurrentLevel + 1) >= getExercises().size()) {
                return;
            }

            mCurrentLevel += 1;
        }
    }

    public void levelDown() {
        if(getSectionMode() == SectionMode.LEVELS) {
            if((mCurrentLevel - 1) < 0 || (mCurrentLevel - 1) >= getExercises().size()) {
                return;
            }

            mCurrentLevel -= 1;
        }
    }

    public void setCurrentLevel(int level) {
        if(getSectionMode() == SectionMode.LEVELS) {
            if(level < 0 || level >= getExercises().size()) {
                return;
            }

            mCurrentLevel = level;
        }
    }

    public int getCurrentLevel() {
        return mCurrentLevel;
    }

    public int getAvailableLevels() {
        if(getSectionMode() == SectionMode.LEVELS) {
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