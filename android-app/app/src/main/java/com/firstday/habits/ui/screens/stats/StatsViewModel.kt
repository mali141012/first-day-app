package com.firstday.habits.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firstday.habits.data.prefs.AppSettings
import com.firstday.habits.data.prefs.SettingsStore
import com.firstday.habits.data.repository.HabitRepository
import com.firstday.habits.domain.model.DayCompletion
import com.firstday.habits.domain.util.StatsCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val weeklyData: List<DayCompletion> = emptyList(),
    val monthlyData: List<DayCompletion> = emptyList(),
    val totalCompletions: Int = 0,
    val bestStreak: Int = 0,
    val activeHabitCount: Int = 0,
    val settings: AppSettings = AppSettings(),
    val loading: Boolean = true,
    val hasData: Boolean = false,
)

class StatsViewModel(
    private val repository: HabitRepository,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        repository.observeAllWithCompletions(),
        settingsStore.settings,
    ) { habitsWithComps, settings ->
        if (habitsWithComps.isEmpty()) {
            StatsUiState(settings = settings, loading = false, hasData = false)
        } else {
            val allCompletions = habitsWithComps.flatMap { it.completions }.toSet()
            val activeHabits = habitsWithComps.filter { !it.habit.archived }

            val weekly = activeHabits.flatMap { hwc ->
                StatsCalculator.weeklyBarData(hwc.completions, hwc.habit.frequency)
            }.let { combined ->
                combineByDate(combined)
            }

            val monthly = activeHabits.flatMap { hwc ->
                StatsCalculator.monthlyBarData(hwc.completions, hwc.habit.frequency)
            }.let { combined ->
                combineByDate(combined)
            }

            val bestStreak = activeHabits.maxOf { hwc ->
                StatsCalculator.calculateStreaks(hwc.completions, hwc.habit.frequency).second
            }

            StatsUiState(
                weeklyData = weekly,
                monthlyData = monthly,
                totalCompletions = allCompletions.size,
                bestStreak = bestStreak,
                activeHabitCount = activeHabits.size,
                settings = settings,
                loading = false,
                hasData = true,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    private fun combineByDate(items: List<DayCompletion>): List<DayCompletion> {
        return items.groupBy { it.date }.map { (date, comps) ->
            DayCompletion(
                date = date,
                completed = comps.any { it.completed },
                scheduled = comps.any { it.scheduled },
            )
        }.sortedBy { it.date }
    }

    companion object {
        fun factory(repository: HabitRepository, settingsStore: SettingsStore) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    StatsViewModel(repository, settingsStore) as T
            }
    }
}
