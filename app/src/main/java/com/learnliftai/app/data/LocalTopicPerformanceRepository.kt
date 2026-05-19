package com.learnliftai.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.learnliftai.app.domain.model.TopicPerformance
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.learnLiftTopicPerformanceDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "learnlift_topic_performance"
)

class LocalTopicPerformanceRepository(
    private val context: Context
) {
    val topicPerformance: Flow<List<TopicPerformance>> = context.learnLiftTopicPerformanceDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            decodeTopicPerformance(preferences[TopicPerformanceKeys.TopicStatsJson].orEmpty())
        }

    suspend fun recordQuizAnswer(
        pathId: String,
        topic: String,
        difficulty: String,
        isCorrect: Boolean
    ) {
        updateTopic(
            pathId = pathId,
            topic = topic,
            difficulty = difficulty,
            isCorrectSignal = isCorrect,
            isWrongSignal = !isCorrect
        )
    }

    suspend fun recordFlashcardReview(
        pathId: String,
        topic: String,
        difficulty: String,
        markedKnown: Boolean
    ) {
        updateTopic(
            pathId = pathId,
            topic = topic,
            difficulty = difficulty,
            isCorrectSignal = markedKnown,
            isWrongSignal = !markedKnown
        )
    }

    suspend fun resetTopicPerformance() {
        context.learnLiftTopicPerformanceDataStore.edit { preferences ->
            preferences.remove(TopicPerformanceKeys.TopicStatsJson)
        }
    }

    private suspend fun updateTopic(
        pathId: String,
        topic: String,
        difficulty: String,
        isCorrectSignal: Boolean,
        isWrongSignal: Boolean
    ) {
        val normalizedTopic = topic.trim()
        if (pathId.isBlank() || normalizedTopic.isBlank()) return

        context.learnLiftTopicPerformanceDataStore.edit { preferences ->
            val current = decodeTopicPerformance(preferences[TopicPerformanceKeys.TopicStatsJson].orEmpty())
            val key = topicKey(pathId, normalizedTopic)
            val now = System.currentTimeMillis()
            val updated = current
                .filterNot { topicKey(it.pathId, it.topic) == key }
                .plus(
                    current.firstOrNull { topicKey(it.pathId, it.topic) == key }
                        .orEmpty(pathId, normalizedTopic)
                        .recordSignal(
                            difficulty = difficulty,
                            isCorrectSignal = isCorrectSignal,
                            isWrongSignal = isWrongSignal,
                            now = now
                        )
                )
            preferences[TopicPerformanceKeys.TopicStatsJson] = encodeTopicPerformance(updated)
        }
    }

    private fun TopicPerformance?.orEmpty(pathId: String, topic: String): TopicPerformance {
        return this ?: TopicPerformance(pathId = pathId, topic = topic)
    }

    private fun TopicPerformance.recordSignal(
        difficulty: String,
        isCorrectSignal: Boolean,
        isWrongSignal: Boolean,
        now: Long
    ): TopicPerformance {
        val normalizedDifficulty = difficulty.lowercase()
        return copy(
            correctAnswers = correctAnswers + if (isCorrectSignal) 1 else 0,
            wrongAnswers = wrongAnswers + if (isWrongSignal) 1 else 0,
            totalAttempts = totalAttempts + 1,
            lastPracticedAt = now,
            lastWrongAt = if (isWrongSignal) now else lastWrongAt,
            easyAttempts = easyAttempts + if ("easy" in normalizedDifficulty || "beginner" in normalizedDifficulty) 1 else 0,
            mediumAttempts = mediumAttempts + if ("medium" in normalizedDifficulty || "intermediate" in normalizedDifficulty) 1 else 0,
            hardAttempts = hardAttempts + if ("hard" in normalizedDifficulty || "advanced" in normalizedDifficulty) 1 else 0
        )
    }

    private fun decodeTopicPerformance(rawJson: String): List<TopicPerformance> {
        if (rawJson.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(rawJson)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                TopicPerformance(
                    pathId = item.optString("pathId"),
                    topic = item.optString("topic"),
                    correctAnswers = item.optInt("correctAnswers"),
                    wrongAnswers = item.optInt("wrongAnswers"),
                    totalAttempts = item.optInt("totalAttempts"),
                    lastPracticedAt = item.optLong("lastPracticedAt"),
                    lastWrongAt = item.optLongOrNull("lastWrongAt"),
                    easyAttempts = item.optInt("easyAttempts"),
                    mediumAttempts = item.optInt("mediumAttempts"),
                    hardAttempts = item.optInt("hardAttempts")
                )
            }.filter { it.pathId.isNotBlank() && it.topic.isNotBlank() }
        }.getOrDefault(emptyList())
    }

    private fun encodeTopicPerformance(items: List<TopicPerformance>): String {
        val array = JSONArray()
        items.sortedWith(
            compareBy<TopicPerformance> { it.pathId.lowercase() }
                .thenBy { it.topic.lowercase() }
        ).forEach { item ->
            array.put(
                JSONObject()
                    .put("pathId", item.pathId)
                    .put("topic", item.topic)
                    .put("correctAnswers", item.correctAnswers)
                    .put("wrongAnswers", item.wrongAnswers)
                    .put("totalAttempts", item.totalAttempts)
                    .put("lastPracticedAt", item.lastPracticedAt)
                    .put("lastWrongAt", item.lastWrongAt ?: JSONObject.NULL)
                    .put("easyAttempts", item.easyAttempts)
                    .put("mediumAttempts", item.mediumAttempts)
                    .put("hardAttempts", item.hardAttempts)
            )
        }
        return array.toString()
    }

    private fun JSONObject.optLongOrNull(name: String): Long? {
        return if (isNull(name)) null else optLong(name)
    }

    private fun topicKey(pathId: String, topic: String): String {
        return "${pathId.trim().lowercase()}::${topic.trim().lowercase()}"
    }

    private object TopicPerformanceKeys {
        val TopicStatsJson = stringPreferencesKey("topicStatsJson")
    }
}
