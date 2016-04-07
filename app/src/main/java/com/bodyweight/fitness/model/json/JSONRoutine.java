package com.bodyweight.fitness.model.json;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JSONRoutine {
    @SerializedName("routineId")
    private String mRoutineId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("subtitle")
    private String mSubtitle;

    @SerializedName("routine")
    private ArrayList<JSONLinkedRoutine> mJSONLinkedRoutines = new ArrayList<>();

    public String getRoutineId() {
        return mRoutineId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public ArrayList<JSONLinkedRoutine> getPartRoutines() {
        return mJSONLinkedRoutines;
    }

    public int getSize() {
        if(mJSONLinkedRoutines != null) {
            return mJSONLinkedRoutines.size();
        }

        return 0;
    }
}
