package com.learnliftai.app.data

import com.learnliftai.app.domain.model.StudyPath

object StudyPathRepository {
    val studyPaths = listOf(
        StudyPath(
            id = "english-vocabulary-speaking",
            title = "English Vocabulary & Speaking Prep",
            subtitle = "Build clearer everyday expression",
            description = "Practice useful vocabulary, speaking prompts, and confidence-building review for daily communication.",
            category = "Free",
            difficultyLabel = "Beginner friendly",
            estimatedDailyTime = "10 min/day",
            accentLabel = "Language",
            recommendedFor = "English learners and job seekers",
            icon = "EN",
            sortPriority = 1
        ),
        StudyPath(
            id = "job-interview-prep",
            title = "Job Interview Prep",
            subtitle = "Prepare stronger answers",
            description = "Review common interview themes, structure better responses, and build confidence before important conversations.",
            category = "Free",
            difficultyLabel = "Practical",
            estimatedDailyTime = "12 min/day",
            accentLabel = "Career",
            recommendedFor = "Job seekers preparing interview answers",
            icon = "JOB",
            sortPriority = 2
        ),
        StudyPath(
            id = "it-qa-interview-prep",
            title = "IT / QA Interview Prep",
            subtitle = "Sharpen technical fundamentals",
            description = "Study core QA concepts, testing vocabulary, and interview-style prompts for entry-level technical preparation.",
            category = "Free",
            difficultyLabel = "Foundational",
            estimatedDailyTime = "15 min/day",
            accentLabel = "Tech",
            recommendedFor = "Beginner IT and QA learners",
            icon = "QA",
            sortPriority = 3
        ),
        StudyPath(
            id = "sql-interview-prep",
            title = "SQL Interview Prep",
            subtitle = "Practice query thinking",
            description = "Review SQL fundamentals, joins, grouping, null handling, keys, indexes, and interview-style query reasoning.",
            category = "Premium Study Packs",
            difficultyLabel = "Beginner to intermediate",
            estimatedDailyTime = "15 min/day",
            accentLabel = "SQL",
            isPremium = true,
            freePreviewCount = 5,
            recommendedFor = "QA, data, analyst, and developer interview prep",
            icon = "SQL",
            sortPriority = 101
        ),
        StudyPath(
            id = "qa-advanced",
            title = "QA Advanced",
            subtitle = "Move beyond testing basics",
            description = "Build stronger QA judgment with strategy, risk, coverage, defect triage, release readiness, and reporting practice.",
            category = "Premium Study Packs",
            difficultyLabel = "Intermediate",
            estimatedDailyTime = "15 min/day",
            accentLabel = "QA+",
            isPremium = true,
            freePreviewCount = 5,
            recommendedFor = "QA learners ready for deeper interview and team scenarios",
            icon = "QA+",
            sortPriority = 102
        ),
        StudyPath(
            id = "automation-testing-basics",
            title = "Automation Testing Basics",
            subtitle = "Learn maintainable automation",
            description = "Study automation pyramid, UI and API checks, selectors, flaky tests, assertions, CI, and maintainability.",
            category = "Premium Study Packs",
            difficultyLabel = "Beginner to intermediate",
            estimatedDailyTime = "15 min/day",
            accentLabel = "Auto",
            isPremium = true,
            freePreviewCount = 5,
            recommendedFor = "Manual testers starting practical automation",
            icon = "AUTO",
            sortPriority = 103
        ),
        StudyPath(
            id = "python-basics",
            title = "Python Basics",
            subtitle = "Coming soon",
            description = "A beginner-friendly Python practice pack is planned for Premium.",
            category = "Premium Study Packs",
            difficultyLabel = "Beginner",
            estimatedDailyTime = "Coming soon",
            accentLabel = "Python",
            isPremium = true,
            isComingSoon = true,
            recommendedFor = "New programmers and automation learners",
            icon = "PY",
            sortPriority = 104
        ),
        StudyPath(
            id = "javascript-basics",
            title = "JavaScript Basics",
            subtitle = "Coming soon",
            description = "A beginner-friendly JavaScript practice pack is planned for Premium.",
            category = "Premium Study Packs",
            difficultyLabel = "Beginner",
            estimatedDailyTime = "Coming soon",
            accentLabel = "JS",
            isPremium = true,
            isComingSoon = true,
            recommendedFor = "Web beginners and frontend learners",
            icon = "JS",
            sortPriority = 105
        ),
        StudyPath(
            id = "business-english",
            title = "Business English",
            subtitle = "Coming soon",
            description = "A workplace communication and business English practice pack is planned for Premium.",
            category = "Premium Study Packs",
            difficultyLabel = "Beginner to intermediate",
            estimatedDailyTime = "Coming soon",
            accentLabel = "Business",
            isPremium = true,
            isComingSoon = true,
            recommendedFor = "Learners building workplace English confidence",
            icon = "BE",
            sortPriority = 106
        ),
        StudyPath(
            id = "technical-interview-prep",
            title = "Technical Interview Prep",
            subtitle = "Coming soon",
            description = "A broader technical interview practice pack is planned for Premium.",
            category = "Premium Study Packs",
            difficultyLabel = "Intermediate",
            estimatedDailyTime = "Coming soon",
            accentLabel = "Tech",
            isPremium = true,
            isComingSoon = true,
            recommendedFor = "Developers and technical candidates",
            icon = "INT",
            sortPriority = 107
        )
    ).sortedBy { it.sortPriority }

    fun findById(id: String?): StudyPath? {
        return studyPaths.firstOrNull { it.id == id }
    }
}
