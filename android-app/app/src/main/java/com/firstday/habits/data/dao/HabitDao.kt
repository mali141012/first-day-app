package com.firstday.habits.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.firstday.habits.data.entity.CompletionEntity
import com.firstday.habits.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY createdAtEpochDay ASC")
    fun observeActive(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE archived = 1 ORDER BY createdAtEpochDay ASC")
    fun observeArchived(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY createdAtEpochDay ASC")
    fun observeAll(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun observeById(id: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): HabitEntity?

    @Query("SELECT * FROM habits")
    suspend fun getAll(): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("UPDATE habits SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("SELECT * FROM habits WHERE reminderHour IS NOT NULL AND archived = 0")
    suspend fun getHabitsWithReminders(): List<HabitEntity>
}

@Dao
interface CompletionDao {

    @Query("SELECT * FROM completions WHERE habitId = :habitId ORDER BY epochDay ASC")
    fun observeForHabit(habitId: Long): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completions WHERE habitId = :habitId ORDER BY epochDay ASC")
    suspend fun getAllForHabit(habitId: Long): List<CompletionEntity>

    @Query("SELECT * FROM completions ORDER BY epochDay ASC")
    suspend fun getAll(): List<CompletionEntity>

    @Query("SELECT * FROM completions ORDER BY epochDay ASC")
    fun observeAllHabitsAndCompletions(): Flow<List<CompletionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM completions WHERE habitId = :habitId AND epochDay = :epochDay)")
    suspend fun exists(habitId: Long, epochDay: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: CompletionEntity): Long

    @Query("DELETE FROM completions WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteByDate(habitId: Long, epochDay: Long)

    @Query("DELETE FROM completions WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: Long)

    @Query("DELETE FROM completions")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(completions: List<CompletionEntity>)

    @Query("SELECT COUNT(*) FROM completions")
    suspend fun count(): Int
}
