package io.mazur.fit.model;

public class Exercise extends LinkedRoutine {
    private String mId;
    private String mTitle;
    private String mDescription;

    private Category mCategory;
    private Section mSection;

    private Exercise mPrevious;
    private Exercise mNext;

    public Exercise(String id, String title, String description) {
        mId = id;
        mTitle = title;
        mDescription = description;
    }

    public String getId() {
        return mId;
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