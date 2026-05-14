package com.learnliftai.app.domain.model

data class SmartCoachRecommendation(
    val title: String,
    val message: String,
    val focusTopics: List<String> = emptyList(),
    val actionLabel: String? = null,
    val type: SmartCoachRecommendationType = SmartCoachRecommendationType.NextStep
)

enum class SmartCoachRecommendationType {
    Encouragement,
    Review,
    Warning,
    NextStep
}
