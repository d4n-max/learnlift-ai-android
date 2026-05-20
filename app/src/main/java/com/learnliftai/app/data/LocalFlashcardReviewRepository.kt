package com.learnliftai.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.learnliftai.app.domain.model.Flashcard
import com.learnliftai.app.domain.model.FlashcardReviewState
import com.learnliftai.app.domain.model.FlashcardReviewStatus
import com.learnliftai.app.domain.model.daysToMillis
import com.learnliftai.app.domain.model.nextKnownReviewIntervalDays
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.learnLiftFlashcardReviewDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "learnlift_flashcard_review"
)

class LocalFlashcardReviewRepository(
    private val context: Context
) {
    val reviewStates: Flow<List<FlashcardReviewState>> = context.learnLiftFlashcardReviewDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            decodeReviewStates(preferences[FlashcardReviewKeys.ReviewStatesJson].orEmpty())
        }

    suspend fun recordReview(
        flashcard: Flashcard,
        markedKnown: Boolean
    ) {
        if (flashcard.id.isBlank() || flashcard.pathId.isBlank()) return

        context.learnLiftFlashcardReviewDataStore.edit { preferences ->
            val current = decodeReviewStates(preferences[FlashcardReviewKeys.ReviewStatesJson].orEmpty())
            val key = reviewKey(flashcard.pathId, flashcard.id)
            val existing = current.firstOrNull { reviewKey(it.pathId, it.cardId) == key }
            val now = System.currentTimeMillis()
            val updatedState = existing
                .orEmpty(flashcard)
                .record(markedKnown = markedKnown, flashcard = flashcard, now = now)
            preferences[FlashcardReviewKeys.ReviewStatesJson] = encodeReviewStates(
                current
                    .filterNot { reviewKey(it.pathId, it.cardId) == key }
                    .plus(updatedState)
            )
        }
    }

    suspend fun resetReviewState() {
        context.learnLiftFlashcardReviewDataStore.edit { preferences ->
            preferences.remove(FlashcardReviewKeys.ReviewStatesJson)
        }
    }

    private fun FlashcardReviewState?.orEmpty(flashcard: Flashcard): FlashcardReviewState {
        return this ?: FlashcardReviewState(
            cardId = flashcard.id,
            pathId = flashcard.pathId,
            topic = flashcard.topic
        )
    }

    private fun FlashcardReviewState.record(
        markedKnown: Boolean,
        flashcard: Flashcard,
        now: Long
    ): FlashcardReviewState {
        return if (markedKnown) {
            val nextKnownCount = knownCount + 1
            val intervalDays = nextKnownReviewIntervalDays(knownCount)
            copy(
                pathId = flashcard.pathId,
                topic = flashcard.topic,
                status = FlashcardReviewStatus.Known,
                knownCount = nextKnownCount,
                lastReviewedAt = now,
                nextReviewAt = now + daysToMillis(intervalDays),
                intervalDays = intervalDays
            )
        } else {
            copy(
                pathId = flashcard.pathId,
                topic = flashcard.topic,
                status = FlashcardReviewStatus.Review,
                needsReviewCount = needsReviewCount + 1,
                lastReviewedAt = now,
                nextReviewAt = now,
                intervalDays = 0
            )
        }
    }

    private fun decodeReviewStates(rawJson: String): List<FlashcardReviewState> {
        if (rawJson.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(rawJson)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                FlashcardReviewState(
                    cardId = item.optString("cardId"),
                    pathId = item.optString("pathId"),
                    topic = item.optString("topic"),
                    status = item.optString("status").toReviewStatus(),
                    knownCount = item.optInt("knownCount"),
                    needsReviewCount = item.optInt("needsReviewCount"),
                    lastReviewedAt = item.optLongOrNull("lastReviewedAt"),
                    nextReviewAt = item.optLongOrNull("nextReviewAt"),
                    intervalDays = item.optInt("intervalDays")
                )
            }.filter { it.cardId.isNotBlank() && it.pathId.isNotBlank() }
        }.getOrDefault(emptyList())
    }

    private fun encodeReviewStates(items: List<FlashcardReviewState>): String {
        val array = JSONArray()
        items.sortedWith(
            compareBy<FlashcardReviewState> { it.pathId.lowercase() }
                .thenBy { it.cardId.lowercase() }
        ).forEach { item ->
            array.put(
                JSONObject()
                    .put("cardId", item.cardId)
                    .put("pathId", item.pathId)
                    .put("topic", item.topic)
                    .put("status", item.status.name)
                    .put("knownCount", item.knownCount)
                    .put("needsReviewCount", item.needsReviewCount)
                    .put("lastReviewedAt", item.lastReviewedAt ?: JSONObject.NULL)
                    .put("nextReviewAt", item.nextReviewAt ?: JSONObject.NULL)
                    .put("intervalDays", item.intervalDays)
            )
        }
        return array.toString()
    }

    private fun String.toReviewStatus(): FlashcardReviewStatus {
        return runCatching { FlashcardReviewStatus.valueOf(this) }
            .getOrDefault(FlashcardReviewStatus.New)
    }

    private fun JSONObject.optLongOrNull(name: String): Long? {
        return if (isNull(name)) null else optLong(name)
    }

    private fun reviewKey(pathId: String, cardId: String): String {
        return "${pathId.trim().lowercase()}::${cardId.trim().lowercase()}"
    }

    private object FlashcardReviewKeys {
        val ReviewStatesJson = stringPreferencesKey("flashcardReviewStatesJson")
    }
}
