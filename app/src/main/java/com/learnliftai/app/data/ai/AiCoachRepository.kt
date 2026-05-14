package com.learnliftai.app.data.ai

class AiCoachRepository(
    private val service: AiCoachService = AiCoachService()
) {
    suspend fun explainAnswer(request: ExplainAnswerRequest): AiCoachResult<ExplainAnswerResponse> {
        return service.explainAnswer(request)
    }

    suspend fun quizSummary(request: QuizSummaryRequest): AiCoachResult<QuizSummaryResponse> {
        return service.quizSummary(request)
    }

    suspend fun studyPlan(request: StudyPlanRequest): AiCoachResult<StudyPlanResponse> {
        return service.studyPlan(request)
    }
}
