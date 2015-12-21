package io.mazur.fit.model;

public enum WeightMeasurementUnit {
    kg("kg"),
    lbs("lbs");

    private String mAsString;

    WeightMeasurementUnit(String asString) {
        mAsString = asString;
    }

    public static WeightMeasurementUnit get(String value) {
        if (value.equalsIgnoreCase("lbs")) {
            return lbs;
        }

        return kg;
    }

    @Override
    public String toString() {
        return mAsString;
    }
}
