package com.firstday.habits.data.repository

import com.firstday.habits.data.dao.CompletionDao
import com.firstday.habits.data.dao.HabitDao
import com.firstday.habits.data.entity.CompletionEntity
import com.firstday.habits.data.entity.HabitEntity
import com.firstday.habits.data.entity.toDomain
import com.firstday.habits.data.entity.toEntity
import com.firstday.habits.domain.model.Habit
import com.firstday.habits.domain.model.HabitWithCompletions
import com.firstday.habits.domain.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao,
) {

    fun observeActiveHabits(): Flow<List<Habit>> =
        habitDao.observeActive().map { list -> list.map { it.toDomain() } }

    fun observeArchivedHabits(): Flow<List<Habit>> =
        habitDao.observeArchived().map { list -> list.map { it.toDomain() } }

    fun observeHabit(id: Long): Flow<Habit?> =
        habitDao.observeById(id).map { it?.toDomain() }

    fun observeHabitWithCompletions(id: Long): Flow<HabitWithCompletions?> =
        combine(observeHabit(id), completionDao.observeForHabit(id)) { habit, completions ->
            if (habit == null) null
            else HabitWithCompletions(
                habit = habit,
                completions = completions.map { DateUtils.fromEpochDay(it.epochDay) }.toSet(),
            )
        }

    fun observeAllWithCompletions(): Flow<List<HabitWithCompletions>> =
        combine(habitDao.observeAll(), completionDao.observeAllHabitsAndCompletions()) { habits, comps ->
            habits.map { h ->
                HabitWithCompletions(
                    habit = h.toDomain(),
                    completions = comps.filter { it.habitId == h.id }
                        .map { DateUtils.fromEpochDay(it.epochDay) }
                        .toSet(),
                )
            }
        }

    suspend fun getHabit(id: Long): Habit? = habitDao.getById(id)?.toDomain()

    suspend fun insertHabit(habit: Habit): Long = habitDao.insert(habit.toEntity())

    suspend fun updateHabit(habit: Habit) = habitDao.update(habit.toEntity())

    suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit.toEntity())

    suspend fun setArchived(id: Long, archived: Boolean) = habitDao.setArchived(id, archived)

    suspend fun toggleCompletion(habitId: Long, date: LocalDate) {
        val epochDay = DateUtils.toEpochDay(date)
        if (completionDao.exists(habitId, epochDay)) {
            completionDao.deleteByDate(habitId, epochDay)
        } else {
            completionDao.insert(CompletionEntity(habitId = habitId, epochDay = epochDay))
        }
    }

    suspend fun isCompleted(habitId: Long, date: LocalDate): Boolean =
        completionDao.exists(habitId, DateUtils.toEpochDay(date))

    suspend fun getHabitsWithReminders(): List<Habit> =
        habitDao.getHabitsWithReminders().map { it.toDomain() }

    suspend fun getAllHabitsRaw(): List<HabitEntity> = habitDao.getAll()

    suspend fun getAllCompletionsRaw(): List<CompletionEntity> = completionDao.getAll()

    suspend fun replaceAll(
        habits: List<HabitEntity>,
        completions: List<CompletionEntity>,
    ) {
        completionDao.deleteAll()
        habits.forEach { entity ->
            val existing = habitDao.getById(entity.id)
            if (existing != null) {
                habitDao.update(entity)
            } else {
                val newId = habitDao.insert(entity.copy(id = 0))
                completions.filter { it.habitId == entity.id }.forEach {
                    completionDao.insert(it.copy(habitId = newId, id = 0))
                }
            }
        }
        val orphanIds = habits.map { it.id }.toSet()
        habitDao.getAll().filter { it.id !in orphanIds }.forEach { habitDao.delete(it) }
    }
}
