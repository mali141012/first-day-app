package com.firstday.habits.domain.util

import com.firstday.habits.domain.model.DayCompletion
import com.firstday.habits.domain.model.Frequency
import com.firstday.habits.domain.model.FrequencyType
import com.firstday.habits.domain.model.HabitStats
import java.time.LocalDate

object StatsCalculator {

    fun isScheduledOn(date: LocalDate, frequency: Frequency): Boolean {
        val dayOfWeek = date.dayOfWeek.value % 7
        return when (frequency.type) {
            FrequencyType.DAILY -> true
            FrequencyType.SPECIFIC_DAYS -> frequency.weekdays.contains(dayOfWeek)
            FrequencyType.X_TIMES_WEEK -> true
        }
    }

    fun buildDayCompletions(
        completions: Set<LocalDate>,
        frequency: Frequency,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<DayCompletion> {
        val days = DateUtils.daysInRange(startDate, endDate)
        return days.map { date ->
            val scheduled = isScheduledOn(date, frequency)
            DayCompletion(
                date = date,
                completed = completions.contains(date),
                scheduled = scheduled,
            )
        }
    }

    fun calculateStreaks(
        completions: Set<LocalDate>,
        frequency: Frequency,
        today: LocalDate = DateUtils.today(),
    ): Pair<Int, Int> {
        if (completions.isEmpty()) return 0 to 0

        val currentStreak = calculateCurrentStreak(completions, frequency, today)
        val longestStreak = calculateLongestStreak(completions, frequency, today)
        return currentStreak to longestStreak
    }

    private fun calculateCurrentStreak(
        completions: Set<LocalDate>,
        frequency: Frequency,
        today: LocalDate,
    ): Int {
        var streak = 0
        var date = today
        if (!completions.contains(today)) {
            val yesterday = today.minusDays(1)
            if (completions.contains(yesterday)) {
                date = yesterday
            } else {
                return 0
            }
        }
        while (completions.contains(date)) {
            streak++
            date = date.minusDays(1)
        }
        return streak
    }

    private fun calculateLongestStreak(
        completions: Set<LocalDate>,
        frequency: Frequency,
        today: LocalDate,
    ): Int {
        if (completions.isEmpty()) return 0
        val sorted = completions.sorted()
        var longest = 1
        var current = 1
        for (i in 1 until sorted.size) {
            if (sorted[i] == sorted[i - 1].plusDays(1)) {
                current++
                if (current > longest) longest = current
            } else {
                current = 1
            }
        }
        return longest
    }

    fun completionRate(
        completions: Set<LocalDate>,
        frequency: Frequency,
        days: Int,
        today: LocalDate = DateUtils.today(),
    ): Float {
        val start = today.minusDays(days - 1L)
        val range = DateUtils.daysInRange(start, today)
        var scheduled = 0
        var done = 0
        for (date in range) {
            if (isScheduledOn(date, frequency)) {
                scheduled++
                if (completions.contains(date)) done++
            }
        }
        return if (scheduled == 0) 0f else done.toFloat() / scheduled
    }

    fun calculateStats(
        completions: Set<LocalDate>,
        frequency: Frequency,
        today: LocalDate = DateUtils.today(),
    ): HabitStats {
        val (current, longest) = calculateStreaks(completions, frequency, today)
        return HabitStats(
            currentStreak = current,
            longestStreak = longest,
            completionRate7 = completionRate(completions, frequency, 7, today),
            completionRate30 = completionRate(completions, frequency, 30, today),
            completionRate90 = completionRate(completions, frequency, 90, today),
            totalCompletions = completions.size,
        )
    }

    fun weeklyBarData(
        completions: Set<LocalDate>,
        frequency: Frequency,
        today: LocalDate = DateUtils.today(),
    ): List<DayCompletion> {
        val start = today.minusDays(6)
        return buildDayCompletions(completions, frequency, start, today)
    }

    fun monthlyBarData(
        completions: Set<LocalDate>,
        frequency: Frequency,
        today: LocalDate = DateUtils.today(),
    ): List<DayCompletion> {
        val start = today.minusDays(29)
        return buildDayCompletions(completions, frequency, start, today)
    }
}
