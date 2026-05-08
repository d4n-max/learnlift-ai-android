package com.learnliftai.app.domain.model

data class QuizQuestion(
    val id: String,
    val pathId: String,
    val question: String,
    val options: List<QuizOption>,
    val correctAnswerId: String,
    val explanation: String,
    val topic: String,
    val difficulty: String
)
