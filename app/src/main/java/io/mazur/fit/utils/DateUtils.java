package io.mazur.fit.utils;

import org.joda.time.DateTime;

import io.mazur.fit.adapter.CalendarAdapter;

public class DateUtils {
    public static DateTime getDate(int presenterPosition) {
        return getDate(presenterPosition, 0);
    }

    public static DateTime getDate(int presenterPosition, int daySelected) {
        if(presenterPosition == CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(daySelected);
        } else if (presenterPosition < CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .minusWeeks(CalendarAdapter.DEFAULT_POSITION - presenterPosition)
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(daySelected);
        } else {
            return new DateTime()
                    .plusWeeks(presenterPosition - CalendarAdapter.DEFAULT_POSITION)
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(daySelected);
        }
    }
}
