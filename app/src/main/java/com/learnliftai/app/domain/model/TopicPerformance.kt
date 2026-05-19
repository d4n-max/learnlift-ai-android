package com.learnliftai.app.domain.model

data class TopicPerformance(
    val pathId: String,
    val topic: String,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val totalAttempts: Int = 0,
    val lastPracticedAt: Long = 0L,
    val lastWrongAt: Long? = null,
    val easyAttempts: Int = 0,
    val mediumAttempts: Int = 0,
    val hardAttempts: Int = 0
) {
    val accuracyPercent: Int
        get() = if (totalAttempts <= 0) 0 else (correctAnswers * 100) / totalAttempts

    val weaknessScore: Int
        get() = if (totalAttempts <= 0) {
            0
        } else {
            wrongAnswers * 2 + (100 - accuracyPercent) / 20 + if (lastWrongAt != null) 1 else 0
        }

    val isWeakTopic: Boolean
        get() = totalAttempts >= 2 && wrongAnswers > 0 && accuracyPercent < 70

    val needsReview: Boolean
        get() = isWeakTopic || wrongAnswers >= 2

    val statusLabel: String
        get() = when {
            totalAttempts < 2 -> "Not enough data"
            isWeakTopic -> "Needs review"
            accuracyPercent >= 80 -> "Strong"
            else -> "Improving"
        }
}
