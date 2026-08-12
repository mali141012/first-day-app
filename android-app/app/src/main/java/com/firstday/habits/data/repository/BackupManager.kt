package com.firstday.habits.data.repository

import com.firstday.habits.data.dao.CompletionDao
import com.firstday.habits.data.dao.HabitDao
import com.firstday.habits.data.entity.toBackup
import com.firstday.habits.data.entity.toEntity
import kotlinx.serialization.json.Json
import java.time.LocalDate

class BackupManager(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao,
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun exportJson(): String {
        val habits = habitDao.getAll().map { it.toBackup() }
        val completions = completionDao.getAll().map { it.toBackup() }
        val backup = BackupFile(
            exportedAt = LocalDate.now().toString(),
            habits = habits,
            completions = completions,
        )
        return json.encodeToString(BackupFile.serializer(), backup)
    }

    suspend fun importJson(content: String): Boolean {
        return try {
            val backup = json.decodeFromString(BackupFile.serializer(), content)
            val habits = backup.habits.map { it.toEntity() }
            val completions = backup.completions.map { it.toEntity() }
            completionDao.deleteAll()
            val idMap = mutableMapOf<Long, Long>()
            habits.forEach { entity ->
                val oldId = entity.id
                val newId = habitDao.insert(entity.copy(id = 0))
                idMap[oldId] = newId
            }
            completions.forEach { comp ->
                val newHabitId = idMap[comp.habitId] ?: return@forEach
                completionDao.insert(comp.copy(habitId = newHabitId, id = 0))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
