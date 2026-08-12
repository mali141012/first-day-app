package com.firstday.habits.data.db

import androidx.room.TypeConverter
import com.firstday.habits.domain.model.Frequency
import com.firstday.habits.domain.model.FrequencyType

class Converters {

    @TypeConverter
    fun frequencyTypeToString(type: FrequencyType): String = type.name

    @TypeConverter
    fun stringToFrequencyType(value: String): FrequencyType =
        FrequencyType.valueOf(value)

    @TypeConverter
    fun weekdaysToString(days: Set<Int>): String = days.joinToString(",")

    @TypeConverter
    fun stringToWeekdays(value: String): Set<Int> {
        if (value.isBlank()) return emptySet()
        return value.split(",").map { it.trim().toInt() }.toSet()
    }
}
