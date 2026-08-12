package com.firstday.habits.data.repository

import com.firstday.habits.data.entity.CompletionEntity
import com.firstday.habits.data.entity.HabitEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupFile(
    val version: Int = 1,
    val exportedAt: String,
    val habits: List<BackupHabit>,
    val completions: List<BackupCompletion>,
)

@Serializable
data class BackupHabit(
    val id: Long,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val frequencyType: String,
    val weekdays: String,
    val timesPerWeek: Int,
    val reminderHour: Int?,
    val reminderMinute: Int?,
    val createdAtEpochDay: Long,
    val archived: Boolean,
)

@Serializable
data class BackupCompletion(
    val habitId: Long,
    val epochDay: Long,
)

fun HabitEntity.toBackup(): BackupHabit = BackupHabit(
    id, name, emoji, colorHex, frequencyType, weekdays, timesPerWeek,
    reminderHour, reminderMinute, createdAtEpochDay, archived,
)

fun BackupHabit.toEntity(): HabitEntity = HabitEntity(
    id, name, emoji, colorHex, frequencyType, weekdays, timesPerWeek,
    reminderHour, reminderMinute, createdAtEpochDay, archived,
)

fun CompletionEntity.toBackup(): BackupCompletion = BackupCompletion(habitId, epochDay)

fun BackupCompletion.toEntity(): CompletionEntity = CompletionEntity(
    habitId = habitId,
    epochDay = epochDay,
)
