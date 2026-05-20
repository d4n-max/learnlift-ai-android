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
import com.learnliftai.app.domain.model.OnboardingPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.learnLiftOnboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "learnlift_onboarding"
)

class LocalOnboardingRepository(
    private val context: Context
) {
    val preferences: Flow<OnboardingPreferences> = context.learnLiftOnboardingDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            OnboardingPreferences(
                hasCompletedOnboarding = preferences[OnboardingKeys.HasCompletedOnboarding] ?: false,
                onboardingGoal = preferences[OnboardingKeys.OnboardingGoal],
                recommendedStudyPathId = preferences[OnboardingKeys.RecommendedStudyPathId],
                dailyStudyMinutes = preferences[OnboardingKeys.DailyStudyMinutes] ?: 10,
                onboardingCompletedAt = preferences[OnboardingKeys.OnboardingCompletedAt]
            )
        }

    suspend fun completeOnboarding(
        onboardingGoal: String,
        recommendedStudyPathId: String,
        dailyStudyMinutes: Int
    ) {
        context.learnLiftOnboardingDataStore.edit { preferences ->
            preferences[OnboardingKeys.HasCompletedOnboarding] = true
            preferences[OnboardingKeys.OnboardingGoal] = onboardingGoal
            preferences[OnboardingKeys.RecommendedStudyPathId] = recommendedStudyPathId
            preferences[OnboardingKeys.DailyStudyMinutes] = dailyStudyMinutes
            preferences[OnboardingKeys.OnboardingCompletedAt] = System.currentTimeMillis()
        }
    }

    suspend fun resetOnboarding() {
        context.learnLiftOnboardingDataStore.edit { preferences ->
            preferences[OnboardingKeys.HasCompletedOnboarding] = false
            preferences.remove(OnboardingKeys.OnboardingGoal)
            preferences.remove(OnboardingKeys.RecommendedStudyPathId)
            preferences[OnboardingKeys.DailyStudyMinutes] = 10
            preferences.remove(OnboardingKeys.OnboardingCompletedAt)
        }
    }

    private object OnboardingKeys {
        val HasCompletedOnboarding = booleanPreferencesKey("hasCompletedOnboarding")
        val OnboardingGoal = stringPreferencesKey("onboardingGoal")
        val RecommendedStudyPathId = stringPreferencesKey("recommendedStudyPathId")
        val DailyStudyMinutes = intPreferencesKey("dailyStudyMinutes")
        val OnboardingCompletedAt = longPreferencesKey("onboardingCompletedAt")
    }
}
