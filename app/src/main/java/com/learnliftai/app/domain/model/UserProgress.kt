package com.learnliftai.app.domain.model

data class UserProgress(
    val selectedStudyPathId: String? = null,
    val totalFlashcardsReviewed: Int = 0,
    val totalKnownCards: Int = 0,
    val totalNeedsReviewCards: Int = 0,
    val totalQuizzesCompleted: Int = 0,
    val lastQuizScore: Int = 0,
    val lastQuizPercentage: Int = 0,
    val currentStudyStreak: Int = 0,
    val lastStudyDate: String? = null
)
