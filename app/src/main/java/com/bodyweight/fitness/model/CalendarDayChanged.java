package com.bodyweight.fitness.model;

public class CalendarDayChanged {
    public int daySelected;
    public int presenterSelected;

    public CalendarDayChanged() {
        this.daySelected = 3;
        this.presenterSelected = 60;
    }

    public CalendarDayChanged(int daySelected, int presenterSelected) {
        this.daySelected = daySelected;
        this.presenterSelected = presenterSelected;
    }
}
