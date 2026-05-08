package com.learnliftai.app.data

import com.learnliftai.app.domain.model.StudyPath

object StudyPathRepository {
    val studyPaths = listOf(
        StudyPath(
            id = "english-vocabulary-speaking",
            title = "English Vocabulary & Speaking Prep",
            subtitle = "Build clearer everyday expression",
            description = "Practice useful vocabulary, speaking prompts, and confidence-building review for daily communication.",
            difficultyLabel = "Beginner friendly",
            estimatedDailyTime = "10 min/day",
            accentLabel = "Language"
        ),
        StudyPath(
            id = "job-interview-prep",
            title = "Job Interview Prep",
            subtitle = "Prepare stronger answers",
            description = "Review common interview themes, structure better responses, and build confidence before important conversations.",
            difficultyLabel = "Practical",
            estimatedDailyTime = "12 min/day",
            accentLabel = "Career"
        ),
        StudyPath(
            id = "it-qa-interview-prep",
            title = "IT / QA Interview Prep",
            subtitle = "Sharpen technical fundamentals",
            description = "Study core QA concepts, testing vocabulary, and interview-style prompts for entry-level technical preparation.",
            difficultyLabel = "Foundational",
            estimatedDailyTime = "15 min/day",
            accentLabel = "Tech"
        )
    )

    fun findById(id: String?): StudyPath? {
        return studyPaths.firstOrNull { it.id == id }
    }
}
