package com.bodyweight.fitness.model

import com.bodyweight.fitness.adapter.CalendarAdapter
import org.joda.time.DateTime

data class CalendarDay(val page: Int = 60, val day: Int = 3) {
    fun getDate(): DateTime {
        if (this.page == CalendarAdapter.DEFAULT_POSITION) {
            return DateTime()
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.day)
        } else if (this.page < CalendarAdapter.DEFAULT_POSITION) {
            return DateTime()
                    .minusWeeks(CalendarAdapter.DEFAULT_POSITION - this.page)
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.day)
        }

        return DateTime()
                .plusWeeks(this.page - CalendarAdapter.DEFAULT_POSITION)
                .dayOfWeek()
                .withMinimumValue()
                .plusDays(this.day)
    }
}