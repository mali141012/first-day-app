package com.firstday.habits.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firstday.habits.data.prefs.AppSettings
import com.firstday.habits.data.prefs.SettingsStore
import com.firstday.habits.data.repository.HabitRepository
import com.firstday.habits.domain.model.Habit
import com.firstday.habits.domain.util.DateUtils
import com.firstday.habits.domain.util.StatsCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val todayHabits: List<TodayHabitItem> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val settings: AppSettings = AppSettings(),
    val loading: Boolean = true,
)

data class TodayHabitItem(
    val habit: Habit,
    val completedToday: Boolean,
    val streak: Int,
)

class HomeViewModel(
    private val repository: HabitRepository,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeAllWithCompletions(),
        settingsStore.settings,
    ) { habitsWithComps, settings ->
        val today = DateUtils.today()
        val todayItems = habitsWithComps
            .filter { !it.habit.archived }
            .filter { StatsCalculator.isScheduledOn(today, it.habit.frequency) }
            .map { hwc ->
                TodayHabitItem(
                    habit = hwc.habit,
                    completedToday = hwc.completions.contains(today),
                    streak = StatsCalculator.calculateStreaks(
                        hwc.completions, hwc.habit.frequency, today,
                    ).first,
                )
            }
        HomeUiState(
            todayHabits = todayItems,
            completedCount = todayItems.count { it.completedToday },
            totalCount = todayItems.size,
            settings = settings,
            loading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun toggleCompletion(habitId: Long) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, DateUtils.today())
        }
    }

    companion object {
        fun factory(repository: HabitRepository, settingsStore: SettingsStore) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HomeViewModel(repository, settingsStore) as T
            }
    }
}
