package com.learnliftai.app.domain.model

private const val OneDayMillis = 24L * 60L * 60L * 1000L

enum class FlashcardReviewStatus {
    New,
    Learning,
    Review,
    Known
}

enum class FlashcardMode {
    All,
    SmartReview
}

data class FlashcardReviewState(
    val cardId: String,
    val pathId: String,
    val topic: String,
    val status: FlashcardReviewStatus = FlashcardReviewStatus.New,
    val knownCount: Int = 0,
    val needsReviewCount: Int = 0,
    val lastReviewedAt: Long? = null,
    val nextReviewAt: Long? = null,
    val intervalDays: Int = 0
) {
    fun isDue(now: Long = System.currentTimeMillis()): Boolean {
        return status == FlashcardReviewStatus.Review ||
            status == FlashcardReviewStatus.Learning ||
            nextReviewAt?.let { it <= now } == true
    }
}

data class FlashcardReviewSummary(
    val dueToday: Int = 0,
    val needsReview: Int = 0,
    val known: Int = 0,
    val newCards: Int = 0
)

fun flashcardReviewSummaryFor(
    pathId: String?,
    flashcards: List<Flashcard>,
    reviewStates: List<FlashcardReviewState>,
    now: Long = System.currentTimeMillis()
): FlashcardReviewSummary {
    val pathStates = reviewStates.filter { pathId == null || it.pathId == pathId }
    val statesByCardId = pathStates.associateBy { it.cardId }
    val availableCardIds = flashcards.map { it.id }.toSet()
    val availableStates = pathStates.filter { it.cardId in availableCardIds }

    return FlashcardReviewSummary(
        dueToday = availableStates.count { it.isDue(now) },
        needsReview = availableStates.count {
            it.status == FlashcardReviewStatus.Review || it.status == FlashcardReviewStatus.Learning
        },
        known = availableStates.count { it.status == FlashcardReviewStatus.Known },
        newCards = flashcards.count { statesByCardId[it.id] == null }
    )
}

fun smartReviewFlashcards(
    flashcards: List<Flashcard>,
    reviewStates: List<FlashcardReviewState>,
    now: Long = System.currentTimeMillis()
): List<Flashcard> {
    val statesByCardId = reviewStates.associateBy { it.cardId }
    return flashcards
        .filter { flashcard -> statesByCardId[flashcard.id]?.isDue(now) == true }
        .sortedWith(
            compareByDescending<Flashcard> {
                statesByCardId[it.id]?.status == FlashcardReviewStatus.Review ||
                    statesByCardId[it.id]?.status == FlashcardReviewStatus.Learning
            }
                .thenBy { statesByCardId[it.id]?.nextReviewAt ?: Long.MAX_VALUE }
                .thenBy { it.topic.lowercase() }
                .thenBy { it.id }
        )
}

fun nextKnownReviewIntervalDays(knownCount: Int): Int {
    return when (knownCount) {
        0 -> 1
        1 -> 3
        2 -> 7
        else -> 14
    }
}

fun daysToMillis(days: Int): Long {
    return days.coerceAtLeast(0) * OneDayMillis
}
