package com.learnliftai.app.ui.screens

import com.learnliftai.app.data.ai.StudyPlanDay
import com.learnliftai.app.data.ai.StudyPlanResponse

internal object ScreenshotDemoStudyPlan {
    fun isEnabled(isDebug: Boolean, buildFlagEnabled: Boolean): Boolean {
        return isDebug && buildFlagEnabled
    }

    fun response(studyPathTitle: String?): StudyPlanResponse {
        val pathTitle = studyPathTitle?.takeIf { it.isNotBlank() } ?: "Job Interview Prep"
        return StudyPlanResponse(
            title = "$pathTitle 7-Day Study Plan",
            days = listOf(
                StudyPlanDay(
                    day = 1,
                    focus = "Map your interview story",
                    tasks = listOf(
                        "Review your target role and top strengths.",
                        "Practice a 60-second introduction."
                    )
                ),
                StudyPlanDay(
                    day = 2,
                    focus = "Strengthen common answers",
                    tasks = listOf(
                        "Practice answers for strengths and weaknesses.",
                        "Turn one vague answer into a specific example."
                    )
                ),
                StudyPlanDay(
                    day = 3,
                    focus = "Practice STAR examples",
                    tasks = listOf(
                        "Draft two STAR stories from past work.",
                        "Check each story for situation, action, and result."
                    )
                ),
                StudyPlanDay(
                    day = 4,
                    focus = "Prepare motivation and goals",
                    tasks = listOf(
                        "Explain why this role fits your goals.",
                        "Connect one skill to the company's needs."
                    )
                ),
                StudyPlanDay(
                    day = 5,
                    focus = "Handle salary and logistics",
                    tasks = listOf(
                        "Prepare a calm salary range response.",
                        "Practice availability and work-style answers."
                    )
                ),
                StudyPlanDay(
                    day = 6,
                    focus = "Run a mock interview",
                    tasks = listOf(
                        "Answer five mixed interview questions.",
                        "Note where you paused or rambled."
                    )
                ),
                StudyPlanDay(
                    day = 7,
                    focus = "Review and polish",
                    tasks = listOf(
                        "Review your strongest examples.",
                        "Choose three topics for next week's review."
                    )
                )
            )
        )
    }
}
