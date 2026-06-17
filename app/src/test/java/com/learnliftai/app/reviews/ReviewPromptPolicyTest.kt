package com.learnliftai.app.reviews

import com.learnliftai.app.domain.model.ReviewPromptState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewPromptPolicyTest {
    private val eligibleState = ReviewPromptState(
        successfulLearningActionsCount = 3,
        completedDailySessionsCount = 3,
        appOpenDays = setOf("2026-06-16", "2026-06-17")
    )

    @Test
    fun canPromptWhenAllThresholdsAreMet() {
        assertTrue(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState,
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptBeforeOnboardingIsCompleted() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState,
                hasCompletedOnboarding = false,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptFromNonPositiveSurface() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState,
                hasCompletedOnboarding = true,
                isPositiveCompletion = false,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptAfterRecentNegativeState() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState,
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = true,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptBeforeTwoActiveDays() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState.copy(appOpenDays = setOf("2026-06-17")),
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptBeforeThreeSuccessfulLearningActions() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState.copy(successfulLearningActionsCount = 2),
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptDuringCooldownAfterAttempt() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState.copy(lastReviewPromptAttemptAt = Now - OneDayMillis),
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptDuringCooldownAfterDismissal() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState.copy(lastReviewPromptDismissedAt = Now - OneDayMillis),
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun canPromptAfterCooldownExpires() {
        assertTrue(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState.copy(lastReviewPromptAttemptAt = Now - ReviewPromptPolicy.ReviewPromptCooldownMillis - 1),
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    @Test
    fun cannotPromptWhenUserOptedOut() {
        assertFalse(
            ReviewPromptPolicy.canPrompt(
                state = eligibleState.copy(dontAskAgain = true),
                hasCompletedOnboarding = true,
                isPositiveCompletion = true,
                hasRecentNegativeState = false,
                now = Now
            )
        )
    }

    private companion object {
        const val OneDayMillis = 24L * 60L * 60L * 1000L
        const val Now = 1_800_000_000_000L
    }
}
