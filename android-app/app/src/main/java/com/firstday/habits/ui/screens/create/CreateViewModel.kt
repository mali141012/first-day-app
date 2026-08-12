package com.firstday.habits.ui.screens.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firstday.habits.data.repository.HabitRepository
import com.firstday.habits.domain.model.Frequency
import com.firstday.habits.domain.model.FrequencyType
import com.firstday.habits.domain.model.Habit
import com.firstday.habits.domain.util.DateUtils
import com.firstday.habits.reminder.ReminderScheduler
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CreateUiState(
    val id: Long = 0,
    val name: String = "",
    val emoji: String = "🌱",
    val colorHex: String = "#2D6A4F",
    val frequencyType: FrequencyType = FrequencyType.DAILY,
    val selectedWeekdays: Set<Int> = setOf(1, 2, 3, 4, 5),
    val timesPerWeek: Int = 3,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val isEdit: Boolean = false,
    val archived: Boolean = false,
    val originalCreatedAt: LocalDate = LocalDate.now(),
)

class CreateViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: HabitRepository,
    private val context: Context,
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>("habitId") ?: 0L

    private val _state = MutableStateFlow(CreateUiState())
    val state: StateFlow<CreateUiState> = _state.asStateFlow()

    init {
        if (habitId != 0L) {
            viewModelScope.launch {
                repository.getHabit(habitId)?.let { habit ->
                    _state.value = CreateUiState(
                        id = habit.id,
                        name = habit.name,
                        emoji = habit.emoji,
                        colorHex = habit.colorHex,
                        frequencyType = habit.frequency.type,
                        selectedWeekdays = habit.frequency.weekdays,
                        timesPerWeek = habit.frequency.timesPerWeek,
                        reminderEnabled = habit.reminderHour != null,
                        reminderHour = habit.reminderHour ?: 9,
                        reminderMinute = habit.reminderMinute ?: 0,
                        isEdit = true,
                        archived = habit.archived,
                        originalCreatedAt = habit.createdAt,
                    )
                }
            }
        }
    }

    fun updateName(v: String) { _state.value = _state.value.copy(name = v) }
    fun updateEmoji(v: String) { _state.value = _state.value.copy(emoji = v) }
    fun updateColor(v: String) { _state.value = _state.value.copy(colorHex = v) }
    fun updateFrequencyType(v: FrequencyType) { _state.value = _state.value.copy(frequencyType = v) }
    fun toggleWeekday(day: Int) {
        val current = _state.value.selectedWeekdays
        _state.value = _state.value.copy(
            selectedWeekdays = if (current.contains(day)) current - day else current + day,
        )
    }
    fun updateTimesPerWeek(v: Int) {
        _state.value = _state.value.copy(timesPerWeek = v.coerceIn(1, 7))
    }
    fun updateReminderEnabled(v: Boolean) {
        _state.value = _state.value.copy(reminderEnabled = v)
    }
    fun updateReminderTime(hour: Int, minute: Int) {
        _state.value = _state.value.copy(reminderHour = hour, reminderMinute = minute)
    }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.name.isBlank()) return
        val frequency = when (s.frequencyType) {
            FrequencyType.DAILY -> Frequency.daily()
            FrequencyType.SPECIFIC_DAYS -> Frequency(FrequencyType.SPECIFIC_DAYS, s.selectedWeekdays)
            FrequencyType.X_TIMES_WEEK -> Frequency.timesPerWeek(s.timesPerWeek)
        }
        val habit = Habit(
            id = s.id,
            name = s.name.trim(),
            emoji = s.emoji,
            colorHex = s.colorHex,
            frequency = frequency,
            reminderHour = if (s.reminderEnabled) s.reminderHour else null,
            reminderMinute = if (s.reminderEnabled) s.reminderMinute else null,
            createdAt = if (s.isEdit) s.originalCreatedAt else LocalDate.now(),
            archived = s.archived,
        )
        viewModelScope.launch {
            if (s.isEdit) {
                repository.updateHabit(habit)
            } else {
                repository.insertHabit(habit)
            }
            if (s.reminderEnabled) {
                ReminderScheduler.schedule(context, habit)
            } else {
                ReminderScheduler.cancel(context, habit.id)
            }
            onDone()
        }
    }

    companion object {
        fun factory(
            savedStateHandle: SavedStateHandle,
            repository: HabitRepository,
            context: Context,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CreateViewModel(savedStateHandle, repository, context) as T
        }
    }
}
