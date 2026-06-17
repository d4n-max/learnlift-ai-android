package com.learnliftai.app.data.ai

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.learnliftai.app.BuildConfig
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.learnLiftAiUsageDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "learnlift_ai_usage"
)

enum class AiUsageAction(
    val actionName: String
) {
    ExplainAnswer("explain_answer"),
    QuizSummary("quiz_summary"),
    StudyPlan("study_plan")
}

data class AiUsageState(
    val dateKey: String = todayDateKey(),
    val lastResetDate: String = todayDateKey(),
    val explainAnswerCount: Int = 0,
    val quizSummaryCount: Int = 0,
    val studyPlanCount: Int = 0
) {
    fun countFor(action: AiUsageAction): Int {
        return when (action) {
            AiUsageAction.ExplainAnswer -> explainAnswerCount
            AiUsageAction.QuizSummary -> quizSummaryCount
            AiUsageAction.StudyPlan -> studyPlanCount
        }
    }

    fun limitFor(action: AiUsageAction, isPremium: Boolean): Int {
        return if (isPremium) {
            when (action) {
                AiUsageAction.ExplainAnswer -> 50
                AiUsageAction.QuizSummary -> 20
                AiUsageAction.StudyPlan -> 10
            }
        } else {
            when (action) {
                AiUsageAction.ExplainAnswer -> 3
                AiUsageAction.QuizSummary -> 1
                AiUsageAction.StudyPlan -> 0
            }
        }
    }

    fun remainingFor(action: AiUsageAction, isPremium: Boolean): Int {
        return (limitFor(action, isPremium) - countFor(action)).coerceAtLeast(0)
    }
}

sealed interface AiUsageDecision {
    data class Allowed(val usage: AiUsageState) : AiUsageDecision
    data class Blocked(
        val message: String,
        val usage: AiUsageState
    ) : AiUsageDecision
}

class AiUsageRepository(
    private val context: Context
) {
    val usage: Flow<AiUsageState> = context.learnLiftAiUsageDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences.toTodayUsage()
        }

    suspend fun consumeIfAvailable(
        action: AiUsageAction,
        isPremium: Boolean
    ): AiUsageDecision {
        var decision: AiUsageDecision = AiUsageDecision.Blocked(
            message = limitMessage(isPremium),
            usage = AiUsageState()
        )

        context.learnLiftAiUsageDataStore.edit { preferences ->
            val currentUsage = preferences.toTodayUsage()
            val limit = currentUsage.limitFor(action, isPremium)
            val currentCount = currentUsage.countFor(action)
            val allowed = currentCount < limit

            if (allowed) {
                val nextUsage = currentUsage.increment(action)
                preferences[AiUsageKeys.DateKey] = nextUsage.dateKey
                preferences[AiUsageKeys.LastResetDate] = nextUsage.lastResetDate
                preferences[AiUsageKeys.ExplainAnswerCount] = nextUsage.explainAnswerCount
                preferences[AiUsageKeys.QuizSummaryCount] = nextUsage.quizSummaryCount
                preferences[AiUsageKeys.StudyPlanCount] = nextUsage.studyPlanCount
                decision = AiUsageDecision.Allowed(nextUsage)
            } else {
                preferences[AiUsageKeys.DateKey] = currentUsage.dateKey
                preferences[AiUsageKeys.LastResetDate] = currentUsage.lastResetDate
                preferences[AiUsageKeys.ExplainAnswerCount] = currentUsage.explainAnswerCount
                preferences[AiUsageKeys.QuizSummaryCount] = currentUsage.quizSummaryCount
                preferences[AiUsageKeys.StudyPlanCount] = currentUsage.studyPlanCount
                decision = AiUsageDecision.Blocked(
                    message = limitMessage(isPremium),
                    usage = currentUsage
                )
            }

            debugLog(
                "action=${action.actionName} plan=${if (isPremium) "Premium" else "Free"} " +
                    "count=$currentCount limit=$limit allowed=$allowed"
            )
        }

        return decision
    }

    private fun Preferences.toTodayUsage(): AiUsageState {
        val today = todayDateKey()
        return if (this[AiUsageKeys.DateKey] == today) {
            AiUsageState(
                dateKey = today,
                lastResetDate = this[AiUsageKeys.LastResetDate] ?: today,
                explainAnswerCount = this[AiUsageKeys.ExplainAnswerCount] ?: 0,
                quizSummaryCount = this[AiUsageKeys.QuizSummaryCount] ?: 0,
                studyPlanCount = this[AiUsageKeys.StudyPlanCount] ?: 0
            )
        } else {
            AiUsageState(dateKey = today, lastResetDate = today)
        }
    }

    private fun AiUsageState.increment(action: AiUsageAction): AiUsageState {
        return when (action) {
            AiUsageAction.ExplainAnswer -> copy(explainAnswerCount = explainAnswerCount + 1)
            AiUsageAction.QuizSummary -> copy(quizSummaryCount = quizSummaryCount + 1)
            AiUsageAction.StudyPlan -> copy(studyPlanCount = studyPlanCount + 1)
        }
    }

    private fun limitMessage(isPremium: Boolean): String {
        return if (isPremium) {
            "You've reached today's AI Coach safety limit. Please try again tomorrow."
        } else {
            "You've used today's free AI Coach explanations. Premium gives you more AI help for mistakes like this, and the local explanation is still available."
        }
    }

    private fun debugLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(LogTag, message)
        }
    }

    private object AiUsageKeys {
        val DateKey = stringPreferencesKey("dateKey")
        val LastResetDate = stringPreferencesKey("lastResetDate")
        val ExplainAnswerCount = intPreferencesKey("aiExplainAnswerCount")
        val QuizSummaryCount = intPreferencesKey("aiQuizSummaryCount")
        val StudyPlanCount = intPreferencesKey("aiStudyPlanCount")
    }

    private companion object {
        const val LogTag = "LearnLiftAiUsage"
    }
}

fun todayDateKey(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
