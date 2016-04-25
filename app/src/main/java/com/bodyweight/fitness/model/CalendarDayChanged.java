package com.bodyweight.fitness.model;

import com.bodyweight.fitness.adapter.CalendarAdapter;

import org.joda.time.DateTime;

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

    public DateTime getDate() {
        if(this.presenterSelected == CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.daySelected);
        } else if (this.presenterSelected < CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .minusWeeks(CalendarAdapter.DEFAULT_POSITION - this.presenterSelected)
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.daySelected);
        } else {
            return new DateTime()
                    .plusWeeks(this.presenterSelected - CalendarAdapter.DEFAULT_POSITION)
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.daySelected);
        }
    }
}
