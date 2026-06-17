package com.learnliftai.app.reviews

import com.learnliftai.app.domain.model.ReviewPromptState

object ReviewPromptPolicy {
    const val MinimumSuccessfulLearningActions = 3
    const val MinimumAppOpenDays = 2
    const val ReviewPromptCooldownMillis = 30L * 24L * 60L * 60L * 1000L

    fun canPrompt(
        state: ReviewPromptState,
        hasCompletedOnboarding: Boolean,
        isPositiveCompletion: Boolean,
        hasRecentNegativeState: Boolean,
        now: Long = System.currentTimeMillis()
    ): Boolean {
        if (!hasCompletedOnboarding) return false
        if (!isPositiveCompletion) return false
        if (hasRecentNegativeState) return false
        if (state.dontAskAgain) return false
        if (state.appOpenDays.size < MinimumAppOpenDays) return false
        if (state.successfulLearningActionsCount < MinimumSuccessfulLearningActions) return false

        val lastCooldownEvent = listOfNotNull(
            state.lastReviewPromptAttemptAt,
            state.lastReviewPromptDismissedAt
        ).maxOrNull()
        if (lastCooldownEvent != null && now - lastCooldownEvent < ReviewPromptCooldownMillis) {
            return false
        }

        return true
    }
}
