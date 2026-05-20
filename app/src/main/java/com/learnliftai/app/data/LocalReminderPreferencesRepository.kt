package com.learnliftai.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.learnliftai.app.domain.model.ReminderPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.learnLiftReminderDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "learnlift_reminders"
)

class LocalReminderPreferencesRepository(
    private val context: Context
) {
    val preferences: Flow<ReminderPreferences> = context.learnLiftReminderDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ReminderPreferences(
                remindersEnabled = preferences[ReminderKeys.RemindersEnabled] ?: false,
                reminderHour = preferences[ReminderKeys.ReminderHour] ?: 19,
                reminderMinute = preferences[ReminderKeys.ReminderMinute] ?: 0,
                lastReminderScheduledAt = preferences[ReminderKeys.LastReminderScheduledAt]
            )
        }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.learnLiftReminderDataStore.edit { preferences ->
            preferences[ReminderKeys.RemindersEnabled] = enabled
            if (!enabled) {
                preferences.remove(ReminderKeys.LastReminderScheduledAt)
            }
        }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.learnLiftReminderDataStore.edit { preferences ->
            preferences[ReminderKeys.ReminderHour] = hour.coerceIn(0, 23)
            preferences[ReminderKeys.ReminderMinute] = minute.coerceIn(0, 59)
        }
    }

    suspend fun markReminderScheduled() {
        context.learnLiftReminderDataStore.edit { preferences ->
            preferences[ReminderKeys.LastReminderScheduledAt] = System.currentTimeMillis()
        }
    }

    private object ReminderKeys {
        val RemindersEnabled = booleanPreferencesKey("remindersEnabled")
        val ReminderHour = intPreferencesKey("reminderHour")
        val ReminderMinute = intPreferencesKey("reminderMinute")
        val LastReminderScheduledAt = longPreferencesKey("lastReminderScheduledAt")
    }
}
