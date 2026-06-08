package com.learnliftai.app.data.ai

data class ExplainAnswerRequest(
    val studyPathId: String,
    val topic: String,
    val difficulty: String,
    val question: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val staticExplanation: String
)

data class ExplainAnswerResponse(
    val title: String,
    val conciseExplanation: String,
    val whyCorrectAnswerWorks: String,
    val studyTip: String,
    val recommendedTopic: String
)

data class QuizSummaryRequest(
    val studyPathId: String,
    val studyPathTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val numberCorrect: Int,
    val numberWrong: Int,
    val incorrectTopics: List<String>,
    val weakTopics: List<String>,
    val wrongQuestions: List<WrongQuestionSample>,
    val difficultySummary: String
)

data class WrongQuestionSample(
    val question: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val topic: String,
    val difficulty: String
)

data class QuizSummaryResponse(
    val summary: String,
    val recommendedFocus: List<String>,
    val nextSessionSuggestion: String,
    val encouragement: String
)

data class StudyPlanRequest(
    val studyPathId: String,
    val studyPathTitle: String,
    val onboardingGoal: String?,
    val dailyStudyMinutes: Int?,
    val weakTopics: List<String>,
    val dueSmartReviewCount: Int,
    val recentQuizSummary: String?,
    val planState: String,
    val days: Int,
    val level: String
)

data class StudyPlanResponse(
    val title: String,
    val days: List<StudyPlanDay>
)

data class StudyPlanDay(
    val day: Int,
    val focus: String,
    val tasks: List<String>
)

sealed interface AiCoachResult<out T> {
    data class Success<T>(val data: T) : AiCoachResult<T>
    data class Error(val message: String = AiCoachUnavailableMessage) : AiCoachResult<Nothing>
}

sealed interface AiCoachUiState<out T> {
    data object Idle : AiCoachUiState<Nothing>
    data object Loading : AiCoachUiState<Nothing>
    data class Success<T>(val data: T) : AiCoachUiState<T>
    data class Error(val message: String = AiCoachUnavailableWithFallbackMessage) : AiCoachUiState<Nothing>
}

const val AiCoachUnavailableMessage = "AI Coach is temporarily unavailable."
const val AiCoachUnavailableWithFallbackMessage =
    "AI Coach is temporarily unavailable. Here's the local explanation instead."
