package com.learnliftai.app.domain.model

enum class PremiumPlanStatus(
    val label: String,
    val helperText: String
) {
    Free(
        label = "Free",
        helperText = "Starter study paths, flashcards, quizzes, daily sessions, progress, and Smart Coach tips."
    ),
    PremiumComingSoon(
        label = "Premium",
        helperText = "More AI help, higher daily AI limits, and Premium-ready study support."
    )
}
