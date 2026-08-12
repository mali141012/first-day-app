package com.firstday.habits.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firstday.habits.data.db.FirstDayDatabase
import com.firstday.habits.data.prefs.AppSettings
import com.firstday.habits.data.prefs.DarkModePref
import com.firstday.habits.data.prefs.SettingsStore
import com.firstday.habits.data.repository.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SettingsEvent { EXPORT_SUCCESS, EXPORT_FAILED, IMPORT_SUCCESS, IMPORT_FAILED }

class SettingsViewModel(
    private val context: Context,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsStore.settings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings(),
    )

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    fun setDarkMode(mode: DarkModePref) {
        viewModelScope.launch { settingsStore.setDarkMode(mode) }
    }

    fun setStartOfWeekSunday(value: Boolean) {
        viewModelScope.launch { settingsStore.setStartOfWeekSunday(value) }
    }

    fun exportToUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val json = withContext(Dispatchers.IO) {
                    val db = FirstDayDatabase.get(context)
                    val mgr = BackupManager(db.habitDao(), db.completionDao())
                    mgr.exportJson()
                }
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(json.toByteArray())
                    }
                }
                _events.emit(SettingsEvent.EXPORT_SUCCESS)
            } catch (e: Exception) {
                _events.emit(SettingsEvent.EXPORT_FAILED)
            }
        }
    }

    fun importFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val content = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        input.bufferedReader().readText()
                    } ?: throw IllegalStateException("Cannot read file")
                }
                val success = withContext(Dispatchers.IO) {
                    val db = FirstDayDatabase.get(context)
                    val mgr = BackupManager(db.habitDao(), db.completionDao())
                    mgr.importJson(content)
                }
                _events.emit(if (success) SettingsEvent.IMPORT_SUCCESS else SettingsEvent.IMPORT_FAILED)
            } catch (e: Exception) {
                _events.emit(SettingsEvent.IMPORT_FAILED)
            }
        }
    }

    companion object {
        fun factory(context: Context, settingsStore: SettingsStore) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SettingsViewModel(context, settingsStore) as T
            }
    }
}
