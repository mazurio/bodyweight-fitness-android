package io.mazur.fit.model.json;

import com.google.gson.annotations.SerializedName;

import io.mazur.fit.model.RoutineType;
import io.mazur.fit.model.SectionMode;

public class JSONLinkedRoutine {
    @SerializedName("id")
    private String mId;

    @SerializedName("level")
    private String mLevel;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("type")
    private String mType;

    /**
     * Mode can be one of those: All, Pick, Levels
     */
    @SerializedName("mode")
    private String mMode;

    @SerializedName("allowTimeReps")
    private boolean mAllowTimeReps;

    @SerializedName("allowBodyweightReps")
    private boolean mAllowBodyweightReps;

    @SerializedName("defaultNumberOfSets")
    private Integer mDefaultNumberOfSets;

    public String getId() {
        return mId;
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

    public boolean allowTimeReps() {
        return mAllowTimeReps;
    }

    public boolean allowBodyweightReps() {
        return mAllowBodyweightReps;
    }

    public Integer getDefaultNumberOfSets() {
        if(mDefaultNumberOfSets != null) {
            return mDefaultNumberOfSets;
        }

        return 1;
    }
}