package com.learnliftai.app.domain.model

data class StudyContent(
    val pathId: String,
    val flashcards: List<Flashcard>,
    val quizQuestions: List<QuizQuestion>
)
