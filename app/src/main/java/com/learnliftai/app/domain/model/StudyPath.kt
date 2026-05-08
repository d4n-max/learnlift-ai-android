package com.learnliftai.app.domain.model

data class StudyPath(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val difficultyLabel: String,
    val estimatedDailyTime: String,
    val accentLabel: String
)
