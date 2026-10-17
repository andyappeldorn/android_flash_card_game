package com.elementary.flashcards.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    private val store = context.applicationContext.settingsDataStore

    val soundEnabled: Flow<Boolean> = store.data.map { prefs ->
        prefs[SoundEnabled] ?: true
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[SoundEnabled] = enabled }
    }

    private companion object {
        val SoundEnabled = booleanPreferencesKey("sound_enabled")
    }
}
