package com.learnliftai.app.domain.model

data class ReviewPromptState(
    val successfulLearningActionsCount: Int = 0,
    val completedDailySessionsCount: Int = 0,
    val successfulSmartReviewCount: Int = 0,
    val successfulAiCoachExplanationsCount: Int = 0,
    val appOpenDays: Set<String> = emptySet(),
    val lastActiveDay: String? = null,
    val lastReviewPromptAttemptAt: Long? = null,
    val lastReviewPromptDismissedAt: Long? = null,
    val dontAskAgain: Boolean = false
)
