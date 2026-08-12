package com.firstday.habits.domain.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

object DateUtils {

    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun toEpochDay(date: LocalDate): Long = date.toEpochDay()

    fun fromEpochDay(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    fun formatIso(date: LocalDate): String = date.format(isoFormatter)

    fun parseIso(text: String): LocalDate = LocalDate.parse(text, isoFormatter)

    fun today(): LocalDate = LocalDate.now()

    fun isScheduled(date: LocalDate, weekdays: Set<Int>): Boolean {
        val dayOfWeek = date.dayOfWeek.value % 7
        return weekdays.contains(dayOfWeek)
    }

    fun daysInRange(start: LocalDate, end: LocalDate): List<LocalDate> {
        val days = mutableListOf<LocalDate>()
        var d = start
        while (!d.isAfter(end)) {
            days.add(d)
            d = d.plusDays(1)
        }
        return days
    }

    fun lastNDays(n: Int, end: LocalDate = today()): List<LocalDate> =
        daysInRange(end.minusDays(n - 1L), end)

    fun startOfWeek(date: LocalDate, startOnSunday: Boolean): LocalDate {
        val dayOfWeek = date.dayOfWeek
        val adjusted = if (startOnSunday) {
            val sun = DayOfWeek.SUNDAY
            date.with(TemporalAdjusters.previousOrSame(sun))
        } else {
            date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }
        return adjusted
    }

    fun weeksInYearRange(
        start: LocalDate,
        end: LocalDate,
        startOnSunday: Boolean,
    ): List<LocalDate> {
        if (start.isAfter(end)) return emptyList()
        val firstWeekStart = startOfWeek(start, startOnSunday)
        val weeks = mutableListOf<LocalDate>()
        var w = firstWeekStart
        while (!w.isAfter(end)) {
            weeks.add(w)
            w = w.plusWeeks(1)
        }
        return weeks
    }

    fun dayOfWeekShort(date: LocalDate, startOnSunday: Boolean): String {
        val order = if (startOnSunday) {
            listOf("S", "M", "T", "W", "T", "F", "S")
        } else {
            listOf("M", "T", "W", "T", "F", "S", "S")
        }
        val idx = date.dayOfWeek.value % 7
        return order[idx]
    }

    fun monthLabel(date: LocalDate): String {
        val ym = YearMonth.from(date)
        return ym.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    }

    fun weekNumber(date: LocalDate): Int {
        return date.get(WeekFields.ISO.weekOfWeekBasedYear())
    }
}
