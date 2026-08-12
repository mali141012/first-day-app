package com.firstday.habits.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
