package com.learnliftai.app.domain

import com.learnliftai.app.domain.model.SmartCoachRecommendation
import com.learnliftai.app.domain.model.SmartCoachRecommendationType
import com.learnliftai.app.domain.model.TopicPerformance
import com.learnliftai.app.domain.model.UserProgress

object SmartCoachAdvisor {
    fun quizSummaryRecommendation(
        percentage: Int,
        weakTopics: List<String>
    ): SmartCoachRecommendation {
        val focusTopics = weakTopics.cleanFocusTopics()
        return when {
            percentage >= StrongScoreThreshold -> SmartCoachRecommendation(
                title = "Recommended Focus",
                message = if (focusTopics.isEmpty()) {
                    "Great score. Keep your streak going and try a fresh quiz tomorrow."
                } else {
                    "Great score. Keep your streak going, then lightly revisit the topics below."
                },
                focusTopics = focusTopics,
                actionLabel = "Keep the streak",
                type = SmartCoachRecommendationType.Encouragement
            )

            percentage >= PassingScoreThreshold -> SmartCoachRecommendation(
                title = "Recommended Focus",
                message = "Good progress. Review the topics you missed, then try another short quiz.",
                focusTopics = focusTopics,
                actionLabel = "Review missed topics",
                type = SmartCoachRecommendationType.Review
            )

            else -> SmartCoachRecommendation(
                title = "Recommended Focus",
                message = "Focus on the basics first. Review flashcards before taking another quiz.",
                focusTopics = focusTopics,
                actionLabel = "Start with flashcards",
                type = SmartCoachRecommendationType.Warning
            )
        }
    }

    fun dailySessionRecommendation(
        quizPercentage: Int?,
        reviewedCards: Int,
        needsReviewCards: Int,
        topicsToReview: List<String>
    ): SmartCoachRecommendation {
        val focusTopics = topicsToReview.cleanFocusTopics()
        val needsReviewRatio = if (reviewedCards == 0) 0f else needsReviewCards.toFloat() / reviewedCards

        return when {
            focusTopics.isNotEmpty() || needsReviewRatio >= NeedsReviewRatioThreshold -> SmartCoachRecommendation(
                title = "Study Coach Tip",
                message = "Review these topics next while they are still fresh from today's session.",
                focusTopics = focusTopics,
                actionLabel = "Review next",
                type = SmartCoachRecommendationType.Review
            )

            quizPercentage != null && quizPercentage < PassingScoreThreshold -> SmartCoachRecommendation(
                title = "Next Best Step",
                message = "Build confidence with flashcards before your next quiz attempt.",
                actionLabel = "Practice basics",
                type = SmartCoachRecommendationType.Warning
            )

            quizPercentage != null && quizPercentage >= StrongScoreThreshold -> SmartCoachRecommendation(
                title = "Study Coach Tip",
                message = "Strong session. Keep the streak going with a fresh daily session tomorrow.",
                actionLabel = "Keep going",
                type = SmartCoachRecommendationType.Encouragement
            )

            else -> SmartCoachRecommendation(
                title = "Next Best Step",
                message = "Nice work. A short quiz or another flashcard pass is a good next step.",
                actionLabel = "Choose a short practice",
                type = SmartCoachRecommendationType.NextStep
            )
        }
    }

    fun progressRecommendation(
        userProgress: UserProgress,
        topicPerformance: List<TopicPerformance> = emptyList()
    ): SmartCoachRecommendation {
        val weakTopics = topicPerformance.weakTopicNames()
        if (weakTopics.isNotEmpty()) {
            return SmartCoachRecommendation(
                title = "Recommended Focus",
                message = "Try an Adaptive Quiz focused on your weak topics: ${weakTopics.joinToString(", ")}.",
                focusTopics = weakTopics,
                actionLabel = "Start Adaptive Quiz",
                type = SmartCoachRecommendationType.Review
            )
        }

        val strongTopics = topicPerformance.strongTopicNames()
        if (strongTopics.isNotEmpty() && userProgress.totalQuizzesCompleted > 0) {
            return SmartCoachRecommendation(
                title = "Recommended Focus",
                message = "You're strong in ${strongTopics.first()}. Try a quiz to reinforce it.",
                focusTopics = strongTopics,
                actionLabel = "Reinforce strength",
                type = SmartCoachRecommendationType.Encouragement
            )
        }

        if (!userProgress.hasAnyProgress()) {
            return SmartCoachRecommendation(
                title = "Recommended Focus",
                message = "Start with a daily session to build your first local progress signal.",
                actionLabel = "Start daily session",
                type = SmartCoachRecommendationType.NextStep
            )
        }

        val flashcardRecommendation = flashcardProgressRecommendation(userProgress)
        if (flashcardRecommendation != null) {
            return flashcardRecommendation
        }

        if (userProgress.totalQuizzesCompleted > 0) {
            return when {
                userProgress.lastQuizPercentage >= StrongScoreThreshold -> SmartCoachRecommendation(
                    title = "Recommended Focus",
                    message = streakPrefix(userProgress) + "Your last quiz was strong. Try another quiz or a harder practice pass.",
                    actionLabel = "Try harder practice",
                    type = SmartCoachRecommendationType.Encouragement
                )

                userProgress.lastQuizPercentage >= PassingScoreThreshold -> SmartCoachRecommendation(
                    title = "Recommended Focus",
                    message = streakPrefix(userProgress) + "Good progress. Review a few flashcards, then retry a short quiz.",
                    actionLabel = "Review and retry",
                    type = SmartCoachRecommendationType.Review
                )

                else -> SmartCoachRecommendation(
                    title = "Recommended Focus",
                    message = "Your last quiz was tough. Start with flashcards before taking another quiz.",
                    actionLabel = "Review basics",
                    type = SmartCoachRecommendationType.Warning
                )
            }
        }

        return SmartCoachRecommendation(
            title = "Recommended Focus",
            message = streakPrefix(userProgress) + "Keep building momentum with a daily session.",
            actionLabel = "Continue streak",
            type = SmartCoachRecommendationType.NextStep
        )
    }

    fun homeRecommendation(
        userProgress: UserProgress,
        topicPerformance: List<TopicPerformance> = emptyList()
    ): SmartCoachRecommendation {
        if (!userProgress.hasAnyProgress()) {
            return SmartCoachRecommendation(
                title = "Today's Focus",
                message = "Start with a daily session to create your first local study signal.",
                actionLabel = "Start daily session",
                type = SmartCoachRecommendationType.NextStep
            )
        }

        return progressRecommendation(userProgress, topicPerformance).copy(title = "Today's Focus")
    }

    private fun flashcardProgressRecommendation(userProgress: UserProgress): SmartCoachRecommendation? {
        if (userProgress.totalFlashcardsReviewed <= 0) {
            return null
        }

        val needsReviewRatio = userProgress.totalNeedsReviewCards.toFloat() / userProgress.totalFlashcardsReviewed
        val knownRatio = userProgress.totalKnownCards.toFloat() / userProgress.totalFlashcardsReviewed

        return when {
            needsReviewRatio >= NeedsReviewRatioThreshold -> SmartCoachRecommendation(
                title = "Recommended Focus",
                message = "Several cards are marked Needs Review. Spend a short round on those before quizzing.",
                actionLabel = "Review flashcards",
                type = SmartCoachRecommendationType.Review
            )

            knownRatio >= KnownRatioThreshold -> SmartCoachRecommendation(
                title = "Recommended Focus",
                message = "Most reviewed cards are marked Known. You are ready for a quiz or daily session.",
                actionLabel = "Try a quiz",
                type = SmartCoachRecommendationType.Encouragement
            )

            else -> null
        }
    }

    private fun UserProgress.hasAnyProgress(): Boolean {
        return totalFlashcardsReviewed > 0 ||
            totalQuizzesCompleted > 0 ||
            currentStudyStreak > 0 ||
            lastStudyDate != null
    }

    private fun streakPrefix(userProgress: UserProgress): String {
        return if (userProgress.currentStudyStreak > 0) {
            "You have a ${userProgress.currentStudyStreak}-day streak. "
        } else {
            ""
        }
    }

    private fun List<String>.cleanFocusTopics(): List<String> {
        return map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(MaxFocusTopics)
    }

    private fun List<TopicPerformance>.weakTopicNames(): List<String> {
        return sortedWith(
            compareByDescending<TopicPerformance> { it.isWeakTopic }
                .thenByDescending { it.weaknessScore }
                .thenByDescending { it.wrongAnswers }
                .thenBy { it.topic.lowercase() }
        )
            .filter { it.needsReview || it.totalAttempts == 1 && it.wrongAnswers > 0 }
            .map { it.topic }
            .cleanFocusTopics()
    }

    private fun List<TopicPerformance>.strongTopicNames(): List<String> {
        return filter { it.totalAttempts >= 2 && it.accuracyPercent >= StrongScoreThreshold }
            .sortedWith(
                compareByDescending<TopicPerformance> { it.accuracyPercent }
                    .thenByDescending { it.totalAttempts }
                    .thenBy { it.topic.lowercase() }
            )
            .map { it.topic }
            .cleanFocusTopics()
    }

    private const val StrongScoreThreshold = 80
    private const val PassingScoreThreshold = 50
    private const val MaxFocusTopics = 3
    private const val NeedsReviewRatioThreshold = 0.5f
    private const val KnownRatioThreshold = 0.8f
}
