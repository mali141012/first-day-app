package com.firstday.habits.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.firstday.habits.data.dao.CompletionDao
import com.firstday.habits.data.dao.HabitDao
import com.firstday.habits.data.entity.CompletionEntity
import com.firstday.habits.data.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, CompletionEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class FirstDayDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun completionDao(): CompletionDao

    companion object {
        @Volatile private var INSTANCE: FirstDayDatabase? = null

        fun get(context: Context): FirstDayDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FirstDayDatabase::class.java,
                    "firstday.db",
                ).build().also { INSTANCE = it }
            }
    }
}
