package com.firstday.habits.domain.model

enum class FrequencyType { DAILY, SPECIFIC_DAYS, X_TIMES_WEEK }

data class Frequency(
    val type: FrequencyType,
    val weekdays: Set<Int> = emptySet(),
    val timesPerWeek: Int = 0,
) {
    companion object {
        fun daily() = Frequency(FrequencyType.DAILY)
        fun weekdays() = Frequency(FrequencyType.SPECIFIC_DAYS, setOf(1, 2, 3, 4, 5))
        fun weekends() = Frequency(FrequencyType.SPECIFIC_DAYS, setOf(0, 6))
        fun timesPerWeek(n: Int) = Frequency(FrequencyType.X_TIMES_WEEK, timesPerWeek = n)
    }
}
