package com.firstday.habits.data.entity

import com.firstday.habits.domain.model.Frequency
import com.firstday.habits.domain.model.FrequencyType
import com.firstday.habits.domain.model.Habit
import com.firstday.habits.domain.util.DateUtils

fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    name = name,
    emoji = emoji,
    colorHex = colorHex,
    frequency = Frequency(
        type = FrequencyType.valueOf(frequencyType),
        weekdays = if (weekdays.isBlank()) emptySet() else weekdays.split(",").map { it.toInt() }.toSet(),
        timesPerWeek = timesPerWeek,
    ),
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    createdAt = DateUtils.fromEpochDay(createdAtEpochDay),
    archived = archived,
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    name = name,
    emoji = emoji,
    colorHex = colorHex,
    frequencyType = frequency.type.name,
    weekdays = frequency.weekdays.joinToString(","),
    timesPerWeek = frequency.timesPerWeek,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    createdAtEpochDay = DateUtils.toEpochDay(createdAt),
    archived = archived,
)
