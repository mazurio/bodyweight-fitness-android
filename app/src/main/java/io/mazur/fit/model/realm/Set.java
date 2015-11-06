package io.mazur.fit.model.realm;

/**
 * Timed Set = 1m 59s
 * Set = Weight (0), Reps (0)
 */
public class Set {
    private boolean mIsTimed = false;

    private double mWeight = 0;
    private int mReps = 0;
    private int mSeconds = 0;

    public void setIsTimed(boolean isTimed) {
        mIsTimed = isTimed;
    }

    public boolean isTimed() {
        return mIsTimed;
    }

    public void setWeight(double weight) {
        mWeight = weight;
    }

    public double getWeight() {
        return mWeight;
    }

    public void setReps(int reps) {
        mReps = reps;
    }

    public int getReps() {
        return mReps;
    }

    public void setSeconds(int seconds) {
        mSeconds = seconds;
    }

    public int getSeconds() {
        return mSeconds;
    }
}
