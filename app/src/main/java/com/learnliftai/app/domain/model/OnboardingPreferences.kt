package com.learnliftai.app.domain.model

data class OnboardingPreferences(
    val hasCompletedOnboarding: Boolean = false,
    val onboardingGoal: String? = null,
    val recommendedStudyPathId: String? = null,
    val dailyStudyMinutes: Int = 10,
    val onboardingCompletedAt: Long? = null
)

enum class OnboardingGoal(
    val label: String,
    val recommendedPathId: String
) {
    ImproveEnglishForWork(
        label = "Improve English for work",
        recommendedPathId = "english-vocabulary-speaking"
    ),
    PrepareForJobInterviews(
        label = "Prepare for job interviews",
        recommendedPathId = "job-interview-prep"
    ),
    PrepareForItQaInterviews(
        label = "Prepare for IT / QA interviews",
        recommendedPathId = "it-qa-interview-prep"
    ),
    BuildDailyLearningHabit(
        label = "Build a daily learning habit",
        recommendedPathId = "job-interview-prep"
    )
}

fun recommendedPathForGoal(
    goal: OnboardingGoal,
    existingSelectedPathId: String?
): String {
    return if (goal == OnboardingGoal.BuildDailyLearningHabit && existingSelectedPathId != null) {
        existingSelectedPathId
    } else {
        goal.recommendedPathId
    }
}
