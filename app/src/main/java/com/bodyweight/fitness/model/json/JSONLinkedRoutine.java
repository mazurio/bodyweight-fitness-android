package com.bodyweight.fitness.model.json;

import com.bodyweight.fitness.model.RoutineType;
import com.bodyweight.fitness.model.SectionMode;
import com.google.gson.annotations.SerializedName;

public class JSONLinkedRoutine {
    @SerializedName("id")
    private String mId;

    @SerializedName("categoryId")
    private String mCategoryId;

    @SerializedName("sectionId")
    private String mSectionId;

    @SerializedName("exerciseId")
    private String mExerciseId;

    @SerializedName("level")
    private String mLevel;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("youTubeId")
    private String mYouTubeId;

    @SerializedName("type")
    private String mType;

    /**
     * Mode can be one of those: All, Pick, Levels
     */
    @SerializedName("mode")
    private String mMode;

    @SerializedName("defaultSet")
    private String mDefaultSet;

    public String getId() {
        return mId;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public String getSectionId() {
        return mSectionId;
    }

    public String getExerciseId() {
        return mExerciseId;
    }

    public String getLevel() {
        return mLevel;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getYouTubeId() {
        return mYouTubeId;
    }

    public RoutineType getType() {
        if(mType.matches("category")) {
            return RoutineType.CATEGORY;
        } else if(mType.matches("section")) {
            return RoutineType.SECTION;
        } else {
            return RoutineType.EXERCISE;
        }
    }

    public SectionMode getMode() {
        if(mMode.matches("all")) {
            return SectionMode.ALL;
        } else if(mMode.matches("pick")) {
            return SectionMode.PICK;
        } else {
            return SectionMode.LEVELS;
        }
    }

    public String getDefaultSet() {
        return mDefaultSet;
    }
}