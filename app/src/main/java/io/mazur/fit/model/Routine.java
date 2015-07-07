package io.mazur.fit.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Routine {
    @SerializedName("routine")
    private ArrayList<PartRoutine> mPartRoutines = new ArrayList<>();

    public ArrayList<PartRoutine> getPartRoutines() {
        return mPartRoutines;
    }

    public PartRoutine getFirstExercise() {
        for(PartRoutine partRoutine : getPartRoutines()) {
            if(partRoutine.getType() == RoutineType.EXERCISE) {
                return partRoutine;
            }
        }

        return new PartRoutine();
    }

    public int getSize() {
        if(mPartRoutines != null) {
            return mPartRoutines.size();
        }

        return 0;
    }

    public class PartRoutine {
        @SerializedName("id")
        private String mId;

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

        /**
         * Position of the section where exercise belongs to.
         */
        private int mSectionPosition = 0;

        private PartRoutine mPrevious;

        private PartRoutine mNext;

        public String getId() {
            return mId;
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

        public void setSectionPosition(int sectionPosition) {
            mSectionPosition = sectionPosition;
        }

        public int getSectionPosition() {
            return mSectionPosition;
        }

        public void setPrevious(PartRoutine previous) {
            mPrevious = previous;
        }

        public PartRoutine getPrevious() {
            return mPrevious;
        }

        public void setNext(PartRoutine next) {
            mNext = next;
        }

        public PartRoutine getNext() {
            return mNext;
        }

        public boolean isPrevious() {
            if(mPrevious == null) {
                return false;
            }

            return true;
        }

        public boolean isNext() {
            if(mNext == null) {
                return false;
            }

            return true;
        }
    }
}
