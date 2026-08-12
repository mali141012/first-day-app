package com.firstday.habits

import android.app.Application
import com.firstday.habits.data.db.FirstDayDatabase
import com.firstday.habits.data.prefs.SettingsStore
import com.firstday.habits.data.repository.HabitRepository
import com.firstday.habits.reminder.ReminderScheduler

class FirstDayApp : Application() {

    lateinit var database: FirstDayDatabase
        private set
    lateinit var repository: HabitRepository
        private set
    lateinit var settingsStore: SettingsStore
        private set

    override fun onCreate() {
        super.onCreate()
        database = FirstDayDatabase.get(this)
        repository = HabitRepository(database.habitDao(), database.completionDao())
        settingsStore = SettingsStore(this)
        ReminderScheduler.ensureChannel(this)
    }
}
