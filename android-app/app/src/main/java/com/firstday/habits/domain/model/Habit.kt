package com.firstday.habits.domain.model

import java.time.LocalDate

data class Habit(
    val id: Long = 0,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val frequency: Frequency,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val createdAt: LocalDate = LocalDate.now(),
    val archived: Boolean = false,
)

data class HabitWithCompletions(
    val habit: Habit,
    val completions: Set<LocalDate>,
)

data class DayCompletion(
    val date: LocalDate,
    val completed: Boolean,
    val scheduled: Boolean,
)

data class HabitStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val completionRate7: Float,
    val completionRate30: Float,
    val completionRate90: Float,
    val totalCompletions: Int,
)
