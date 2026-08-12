package com.firstday.habits.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppSettings(
    val darkMode: DarkModePref = DarkModePref.SYSTEM,
    val startOfWeekSunday: Boolean = false,
)

enum class DarkModePref { SYSTEM, ON, OFF }

class SettingsStore(private val context: Context) {

    private object Keys {
        val DARK_MODE = intPreferencesKey("dark_mode")
        val START_SUNDAY = booleanPreferencesKey("start_sunday")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            darkMode = DarkModePref.entries.getOrElse(prefs[Keys.DARK_MODE] ?: 0) { DarkModePref.SYSTEM },
            startOfWeekSunday = prefs[Keys.START_SUNDAY] ?: false,
        )
    }

    suspend fun setDarkMode(mode: DarkModePref) {
        context.dataStore.edit { it[Keys.DARK_MODE] = mode.ordinal }
    }

    suspend fun setStartOfWeekSunday(value: Boolean) {
        context.dataStore.edit { it[Keys.START_SUNDAY] = value }
    }
}
