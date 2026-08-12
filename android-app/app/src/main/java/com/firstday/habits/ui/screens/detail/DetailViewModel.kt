package com.firstday.habits.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firstday.habits.data.prefs.AppSettings
import com.firstday.habits.data.prefs.SettingsStore
import com.firstday.habits.data.repository.HabitRepository
import com.firstday.habits.domain.model.DayCompletion
import com.firstday.habits.domain.model.Habit
import com.firstday.habits.domain.model.HabitStats
import com.firstday.habits.domain.util.DateUtils
import com.firstday.habits.domain.util.StatsCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DetailUiState(
    val habit: Habit? = null,
    val completions: Set<LocalDate> = emptySet(),
    val stats: HabitStats? = null,
    val heatmapData: List<DayCompletion> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val loading: Boolean = true,
    val todayCompleted: Boolean = false,
)

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: HabitRepository,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    val habitId: Long = savedStateHandle.get<Long>("habitId") ?: 0L

    val uiState: StateFlow<DetailUiState> = combine(
        repository.observeHabitWithCompletions(habitId),
        settingsStore.settings,
    ) { hwc, settings ->
        if (hwc == null) {
            DetailUiState(loading = false, settings = settings)
        } else {
            val today = DateUtils.today()
            val stats = StatsCalculator.calculateStats(hwc.completions, hwc.habit.frequency, today)
            val heatmapStart = today.minusDays(364)
            val heatmap = StatsCalculator.buildDayCompletions(
                hwc.completions, hwc.habit.frequency, heatmapStart, today,
            )
            DetailUiState(
                habit = hwc.habit,
                completions = hwc.completions,
                stats = stats,
                heatmapData = heatmap,
                settings = settings,
                loading = false,
                todayCompleted = hwc.completions.contains(today),
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState())

    fun toggleToday() {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, DateUtils.today())
        }
    }

    fun toggleDate(date: LocalDate) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, date)
        }
    }

    fun archive() {
        viewModelScope.launch {
            repository.setArchived(habitId, true)
        }
    }

    fun delete() {
        viewModelScope.launch {
            val habit = repository.getHabit(habitId) ?: return@launch
            repository.deleteHabit(habit)
        }
    }

    companion object {
        fun factory(
            savedStateHandle: SavedStateHandle,
            repository: HabitRepository,
            settingsStore: SettingsStore,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DetailViewModel(savedStateHandle, repository, settingsStore) as T
        }
    }
}
