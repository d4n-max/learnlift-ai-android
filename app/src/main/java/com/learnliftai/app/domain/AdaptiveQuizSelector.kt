package com.learnliftai.app.domain

import com.learnliftai.app.domain.model.QuizQuestion
import com.learnliftai.app.domain.model.TopicPerformance

data class AdaptiveQuizSelection(
    val questions: List<QuizQuestion>,
    val focusedTopics: List<String>,
    val hasWeakTopicData: Boolean
)

object AdaptiveQuizSelector {
    fun selectQuestions(
        questions: List<QuizQuestion>,
        topicPerformance: List<TopicPerformance>,
        desiredQuestionCount: Int = DefaultQuestionCount
    ): AdaptiveQuizSelection {
        if (questions.isEmpty()) {
            return AdaptiveQuizSelection(
                questions = emptyList(),
                focusedTopics = emptyList(),
                hasWeakTopicData = false
            )
        }

        val targetCount = desiredQuestionCount.coerceAtMost(questions.size)
        val weakTopics = topicPerformance
            .filter { it.needsReview || it.totalAttempts == 1 && it.wrongAnswers > 0 }
            .sortedWith(
                compareByDescending<TopicPerformance> { it.isWeakTopic }
                    .thenByDescending { it.weaknessScore }
                    .thenByDescending { it.wrongAnswers }
                    .thenBy { it.topic.lowercase() }
            )
            .map { it.topic }
            .distinct()

        if (weakTopics.isEmpty()) {
            return AdaptiveQuizSelection(
                questions = balancedQuestions(questions, targetCount),
                focusedTopics = emptyList(),
                hasWeakTopicData = false
            )
        }

        val weakQuestions = questions
            .filter { question -> weakTopics.any { it.equals(question.topic, ignoreCase = true) } }
            .shuffled()
        val mixedQuestions = questions
            .filterNot { question -> weakTopics.any { it.equals(question.topic, ignoreCase = true) } }
            .shuffled()
        val confidenceBuilders = questions
            .filter { question ->
                question.difficulty.contains("easy", ignoreCase = true) ||
                    question.difficulty.contains("beginner", ignoreCase = true)
            }
            .shuffled()

        val weakTarget = (targetCount * WeakTopicRatio).toInt().coerceAtLeast(1)
        val mixedTarget = (targetCount * MixedReviewRatio).toInt().coerceAtLeast(1)
        val selected = mutableListOf<QuizQuestion>()
        selected.addUnique(weakQuestions.take(weakTarget))
        selected.addUnique(mixedQuestions.take(mixedTarget))
        selected.addUnique(confidenceBuilders.take(targetCount - selected.size))
        selected.addUnique((weakQuestions + mixedQuestions + questions.shuffled()).take(targetCount - selected.size))

        return AdaptiveQuizSelection(
            questions = selected.take(targetCount),
            focusedTopics = weakTopics.take(MaxFocusedTopics),
            hasWeakTopicData = true
        )
    }

    private fun balancedQuestions(
        questions: List<QuizQuestion>,
        targetCount: Int
    ): List<QuizQuestion> {
        val grouped = questions.groupBy { it.topic }
            .values
            .map { it.shuffled() }
            .toMutableList()
        val selected = mutableListOf<QuizQuestion>()
        var index = 0
        while (selected.size < targetCount && grouped.any { index < it.size }) {
            grouped.forEach { topicQuestions ->
                if (selected.size < targetCount && index < topicQuestions.size) {
                    selected.add(topicQuestions[index])
                }
            }
            index += 1
        }
        selected.addUnique(questions.shuffled().take(targetCount - selected.size))
        return selected.take(targetCount)
    }

    private fun MutableList<QuizQuestion>.addUnique(items: List<QuizQuestion>) {
        items.forEach { item ->
            if (none { it.id == item.id }) {
                add(item)
            }
        }
    }

    private const val DefaultQuestionCount = 10
    private const val MaxFocusedTopics = 3
    private const val WeakTopicRatio = 0.7f
    private const val MixedReviewRatio = 0.2f
}

enum class QuizMode {
    Normal,
    Adaptive
}
