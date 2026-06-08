package com.learnliftai.app.domain.model

data class StudyPath(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: String,
    val difficultyLabel: String,
    val estimatedDailyTime: String,
    val accentLabel: String,
    val isPremium: Boolean = false,
    val isComingSoon: Boolean = false,
    val freePreviewCount: Int = 0,
    val recommendedFor: String,
    val icon: String,
    val sortPriority: Int
)
