package io.mazur.fit.model.json;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JSONRoutine {
    @SerializedName("routine")
    private ArrayList<JSONLinkedRoutine> mJSONLinkedRoutines = new ArrayList<>();

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
