package com.firstday.habits.domain.util

import java.time.LocalDate

object WeekPlan {

    fun daysForWeek(
        weekStart: LocalDate,
        startOnSunday: Boolean,
    ): List<LocalDate> {
        val days = mutableListOf<LocalDate>()
        var d = weekStart
        val firstDow = if (startOnSunday) 7 else 1
        repeat(7) {
            days.add(d)
            d = d.plusDays(1)
        }
        return days
    }

    fun currentWeekStart(startOnSunday: Boolean): LocalDate =
        DateUtils.startOfWeek(DateUtils.today(), startOnSunday)
}
