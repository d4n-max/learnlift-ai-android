package com.learnliftai.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.learnliftai.app.domain.model.UserProgress
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.learnLiftProgressDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "learnlift_progress"
)

class LocalProgressRepository(
    private val context: Context
) {
    val progress: Flow<UserProgress> = context.learnLiftProgressDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserProgress(
                selectedStudyPathId = preferences[ProgressKeys.SelectedStudyPathId],
                totalFlashcardsReviewed = preferences[ProgressKeys.TotalFlashcardsReviewed] ?: 0,
                totalKnownCards = preferences[ProgressKeys.TotalKnownCards] ?: 0,
                totalNeedsReviewCards = preferences[ProgressKeys.TotalNeedsReviewCards] ?: 0,
                totalQuizzesCompleted = preferences[ProgressKeys.TotalQuizzesCompleted] ?: 0,
                lastQuizScore = preferences[ProgressKeys.LastQuizScore] ?: 0,
                lastQuizPercentage = preferences[ProgressKeys.LastQuizPercentage] ?: 0,
                currentStudyStreak = preferences[ProgressKeys.CurrentStudyStreak] ?: 0,
                lastStudyDate = preferences[ProgressKeys.LastStudyDate]
            )
        }

    suspend fun setSelectedStudyPathId(pathId: String) {
        context.learnLiftProgressDataStore.edit { preferences ->
            preferences[ProgressKeys.SelectedStudyPathId] = pathId
        }
    }

    suspend fun recordFlashcardReview(
        reviewedDelta: Int,
        knownDelta: Int,
        needsReviewDelta: Int
    ) {
        context.learnLiftProgressDataStore.edit { preferences ->
            preferences[ProgressKeys.TotalFlashcardsReviewed] =
                ((preferences[ProgressKeys.TotalFlashcardsReviewed] ?: 0) + reviewedDelta).coerceAtLeast(0)
            preferences[ProgressKeys.TotalKnownCards] =
                ((preferences[ProgressKeys.TotalKnownCards] ?: 0) + knownDelta).coerceAtLeast(0)
            preferences[ProgressKeys.TotalNeedsReviewCards] =
                ((preferences[ProgressKeys.TotalNeedsReviewCards] ?: 0) + needsReviewDelta).coerceAtLeast(0)
            if (reviewedDelta > 0) {
                updateStudyStreak(preferences)
            }
        }
    }

    suspend fun recordQuizCompleted(
        score: Int,
        percentage: Int
    ) {
        context.learnLiftProgressDataStore.edit { preferences ->
            preferences[ProgressKeys.TotalQuizzesCompleted] =
                (preferences[ProgressKeys.TotalQuizzesCompleted] ?: 0) + 1
            preferences[ProgressKeys.LastQuizScore] = score
            preferences[ProgressKeys.LastQuizPercentage] = percentage
            updateStudyStreak(preferences)
        }
    }

    suspend fun recordDailySessionCompleted(
        reviewedCards: Int,
        knownCards: Int,
        needsReviewCards: Int,
        quizAnswered: Int,
        quizCorrect: Int,
        quizPercentage: Int
    ) {
        context.learnLiftProgressDataStore.edit { preferences ->
            preferences[ProgressKeys.TotalFlashcardsReviewed] =
                ((preferences[ProgressKeys.TotalFlashcardsReviewed] ?: 0) + reviewedCards).coerceAtLeast(0)
            preferences[ProgressKeys.TotalKnownCards] =
                ((preferences[ProgressKeys.TotalKnownCards] ?: 0) + knownCards).coerceAtLeast(0)
            preferences[ProgressKeys.TotalNeedsReviewCards] =
                ((preferences[ProgressKeys.TotalNeedsReviewCards] ?: 0) + needsReviewCards).coerceAtLeast(0)

            if (quizAnswered > 0) {
                preferences[ProgressKeys.TotalQuizzesCompleted] =
                    (preferences[ProgressKeys.TotalQuizzesCompleted] ?: 0) + 1
                preferences[ProgressKeys.LastQuizScore] = quizCorrect
                preferences[ProgressKeys.LastQuizPercentage] = quizPercentage
            }

            if (reviewedCards > 0 || quizAnswered > 0) {
                updateStudyStreak(preferences)
            }
        }
    }

    suspend fun resetProgressStats() {
        context.learnLiftProgressDataStore.edit { preferences ->
            preferences[ProgressKeys.TotalFlashcardsReviewed] = 0
            preferences[ProgressKeys.TotalKnownCards] = 0
            preferences[ProgressKeys.TotalNeedsReviewCards] = 0
            preferences[ProgressKeys.TotalQuizzesCompleted] = 0
            preferences[ProgressKeys.LastQuizScore] = 0
            preferences[ProgressKeys.LastQuizPercentage] = 0
            preferences[ProgressKeys.CurrentStudyStreak] = 0
            preferences.remove(ProgressKeys.LastStudyDate)
        }
    }

    private fun updateStudyStreak(preferences: androidx.datastore.preferences.core.MutablePreferences) {
        val today = formattedDate(0)
        val yesterday = formattedDate(-1)
        val lastStudyDate = preferences[ProgressKeys.LastStudyDate]
        val currentStreak = preferences[ProgressKeys.CurrentStudyStreak] ?: 0

        when (lastStudyDate) {
            today -> Unit
            yesterday -> preferences[ProgressKeys.CurrentStudyStreak] = currentStreak + 1
            else -> preferences[ProgressKeys.CurrentStudyStreak] = 1
        }
        preferences[ProgressKeys.LastStudyDate] = today
    }

    private fun formattedDate(dayOffset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }

    private object ProgressKeys {
        val SelectedStudyPathId = stringPreferencesKey("selectedStudyPathId")
        val TotalFlashcardsReviewed = intPreferencesKey("totalFlashcardsReviewed")
        val TotalKnownCards = intPreferencesKey("totalKnownCards")
        val TotalNeedsReviewCards = intPreferencesKey("totalNeedsReviewCards")
        val TotalQuizzesCompleted = intPreferencesKey("totalQuizzesCompleted")
        val LastQuizScore = intPreferencesKey("lastQuizScore")
        val LastQuizPercentage = intPreferencesKey("lastQuizPercentage")
        val CurrentStudyStreak = intPreferencesKey("currentStudyStreak")
        val LastStudyDate = stringPreferencesKey("lastStudyDate")
    }
}
