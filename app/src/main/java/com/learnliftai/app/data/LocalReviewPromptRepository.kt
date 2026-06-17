package com.learnliftai.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.learnliftai.app.domain.model.ReviewPromptState
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.learnLiftReviewPromptDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "learnlift_review_prompt"
)

class LocalReviewPromptRepository(
    private val context: Context
) {
    val state: Flow<ReviewPromptState> = context.learnLiftReviewPromptDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map(::decodeState)

    suspend fun recordAppOpenDay(): ReviewPromptState {
        val today = formattedDate()
        return updateState { current ->
            val recentDays = (current.appOpenDays + today)
                .sorted()
                .takeLast(MaxTrackedActiveDays)
                .toSet()
            current.copy(
                appOpenDays = recentDays,
                lastActiveDay = today
            )
        }
    }

    suspend fun recordDailySessionCompleted(successful: Boolean): ReviewPromptState {
        return updateState { current ->
            current.copy(
                completedDailySessionsCount = current.completedDailySessionsCount + 1,
                successfulLearningActionsCount = if (successful) {
                    current.successfulLearningActionsCount + 1
                } else {
                    current.successfulLearningActionsCount
                }
            )
        }
    }

    suspend fun recordSuccessfulQuizCompleted(): ReviewPromptState {
        return updateState { current ->
            current.copy(
                successfulLearningActionsCount = current.successfulLearningActionsCount + 1
            )
        }
    }

    suspend fun markReviewPromptAttempted(now: Long = System.currentTimeMillis()): ReviewPromptState {
        return updateState { current ->
            current.copy(lastReviewPromptAttemptAt = now)
        }
    }

    suspend fun markReviewPromptDismissed(now: Long = System.currentTimeMillis()): ReviewPromptState {
        return updateState { current ->
            current.copy(lastReviewPromptDismissedAt = now)
        }
    }

    private suspend fun updateState(transform: (ReviewPromptState) -> ReviewPromptState): ReviewPromptState {
        var updatedState = ReviewPromptState()
        context.learnLiftReviewPromptDataStore.edit { preferences ->
            updatedState = transform(decodeState(preferences))
            encodeState(preferences, updatedState)
        }
        return updatedState
    }

    private fun decodeState(preferences: Preferences): ReviewPromptState {
        return ReviewPromptState(
            successfulLearningActionsCount = preferences[ReviewPromptKeys.SuccessfulLearningActionsCount] ?: 0,
            completedDailySessionsCount = preferences[ReviewPromptKeys.CompletedDailySessionsCount] ?: 0,
            successfulSmartReviewCount = preferences[ReviewPromptKeys.SuccessfulSmartReviewCount] ?: 0,
            successfulAiCoachExplanationsCount = preferences[ReviewPromptKeys.SuccessfulAiCoachExplanationsCount] ?: 0,
            appOpenDays = decodeActiveDays(preferences[ReviewPromptKeys.AppOpenDays].orEmpty()),
            lastActiveDay = preferences[ReviewPromptKeys.LastActiveDay],
            lastReviewPromptAttemptAt = preferences[ReviewPromptKeys.LastReviewPromptAttemptAt],
            lastReviewPromptDismissedAt = preferences[ReviewPromptKeys.LastReviewPromptDismissedAt],
            dontAskAgain = preferences[ReviewPromptKeys.DontAskAgain] ?: false
        )
    }

    private fun encodeState(
        preferences: androidx.datastore.preferences.core.MutablePreferences,
        state: ReviewPromptState
    ) {
        preferences[ReviewPromptKeys.SuccessfulLearningActionsCount] = state.successfulLearningActionsCount
        preferences[ReviewPromptKeys.CompletedDailySessionsCount] = state.completedDailySessionsCount
        preferences[ReviewPromptKeys.SuccessfulSmartReviewCount] = state.successfulSmartReviewCount
        preferences[ReviewPromptKeys.SuccessfulAiCoachExplanationsCount] = state.successfulAiCoachExplanationsCount
        preferences[ReviewPromptKeys.AppOpenDays] = state.appOpenDays.sorted().takeLast(MaxTrackedActiveDays).joinToString(",")
        state.lastActiveDay?.let {
            preferences[ReviewPromptKeys.LastActiveDay] = it
        } ?: preferences.remove(ReviewPromptKeys.LastActiveDay)
        state.lastReviewPromptAttemptAt?.let {
            preferences[ReviewPromptKeys.LastReviewPromptAttemptAt] = it
        } ?: preferences.remove(ReviewPromptKeys.LastReviewPromptAttemptAt)
        state.lastReviewPromptDismissedAt?.let {
            preferences[ReviewPromptKeys.LastReviewPromptDismissedAt] = it
        } ?: preferences.remove(ReviewPromptKeys.LastReviewPromptDismissedAt)
        preferences[ReviewPromptKeys.DontAskAgain] = state.dontAskAgain
    }

    private fun decodeActiveDays(raw: String): Set<String> {
        return raw
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .takeLast(MaxTrackedActiveDays)
            .toSet()
    }

    private fun formattedDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
    }

    private object ReviewPromptKeys {
        val SuccessfulLearningActionsCount = intPreferencesKey("successfulLearningActionsCount")
        val CompletedDailySessionsCount = intPreferencesKey("completedDailySessionsCount")
        val SuccessfulSmartReviewCount = intPreferencesKey("successfulSmartReviewCount")
        val SuccessfulAiCoachExplanationsCount = intPreferencesKey("successfulAiCoachExplanationsCount")
        val AppOpenDays = stringPreferencesKey("appOpenDays")
        val LastActiveDay = stringPreferencesKey("lastActiveDay")
        val LastReviewPromptAttemptAt = longPreferencesKey("lastReviewPromptAttemptAt")
        val LastReviewPromptDismissedAt = longPreferencesKey("lastReviewPromptDismissedAt")
        val DontAskAgain = booleanPreferencesKey("dontAskAgain")
    }

    private companion object {
        const val MaxTrackedActiveDays = 30
    }
}
