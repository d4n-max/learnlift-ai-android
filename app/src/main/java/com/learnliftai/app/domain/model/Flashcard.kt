package com.learnliftai.app.domain.model

data class Flashcard(
    val id: String,
    val pathId: String,
    val question: String,
    val answer: String,
    val topic: String,
    val difficulty: String
)
