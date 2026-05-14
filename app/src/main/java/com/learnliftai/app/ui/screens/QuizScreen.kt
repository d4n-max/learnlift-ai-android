package com.learnliftai.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.data.ai.AiCoachRepository
import com.learnliftai.app.data.ai.AiCoachResult
import com.learnliftai.app.data.ai.AiCoachUiState
import com.learnliftai.app.data.ai.ExplainAnswerRequest
import com.learnliftai.app.data.ai.ExplainAnswerResponse
import com.learnliftai.app.data.ai.QuizSummaryRequest
import com.learnliftai.app.data.ai.QuizSummaryResponse
import com.learnliftai.app.domain.SmartCoachAdvisor
import com.learnliftai.app.domain.model.QuizOption
import com.learnliftai.app.domain.model.QuizQuestion
import com.learnliftai.app.domain.model.StudyContent
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.SmartCoachRecommendationCard
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    selectedStudyPath: StudyPath?,
    selectedStudyContent: StudyContent?,
    onQuizCompleted: (score: Int, percentage: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val quizQuestions = selectedStudyContent?.quizQuestions.orEmpty()
    val aiCoachRepository = remember { AiCoachRepository() }

    if (selectedStudyPath == null) {
        QuizEmptyState(
            title = "Choose a study path first",
            description = "Pick a study path from Home, then quiz questions will appear here for focused practice.",
            modifier = modifier
        )
        return
    }

    if (quizQuestions.isEmpty()) {
        QuizEmptyState(
            title = "No quiz questions yet",
            description = "${selectedStudyPath.title} does not have quiz questions available yet.",
            modifier = modifier
        )
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isQuizComplete by remember { mutableStateOf(false) }
    val selectedAnswers = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(selectedStudyPath.id) {
        currentIndex = 0
        isQuizComplete = false
        selectedAnswers.clear()
    }

    val safeIndex = currentIndex.coerceIn(0, quizQuestions.lastIndex)
    val score = rememberQuizScore(quizQuestions, selectedAnswers)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        SectionHeader(
            title = "Quiz",
            subtitle = selectedStudyPath.title
        )

        if (isQuizComplete) {
            QuizSummary(
                studyPathId = selectedStudyPath.id,
                score = score,
                weakTopics = rememberWeakTopics(quizQuestions, selectedAnswers),
                aiCoachRepository = aiCoachRepository,
                onRestartQuiz = {
                    currentIndex = 0
                    isQuizComplete = false
                    selectedAnswers.clear()
                }
            )
        } else {
            val currentQuestion = quizQuestions[safeIndex]
            val selectedAnswerId = selectedAnswers[currentQuestion.id]

            QuizProgress(
                currentIndex = safeIndex,
                totalQuestions = quizQuestions.size
            )
            QuizQuestionCard(
                studyPathId = selectedStudyPath.id,
                question = currentQuestion,
                selectedAnswerId = selectedAnswerId,
                aiCoachRepository = aiCoachRepository,
                onAnswerSelected = { optionId ->
                    if (selectedAnswers[currentQuestion.id] == null) {
                        selectedAnswers[currentQuestion.id] = optionId
                    }
                }
            )
            QuizScoreStats(score = score)
            if (selectedAnswerId != null) {
                PrimaryActionButton(
                    text = if (safeIndex == quizQuestions.lastIndex) "See summary" else "Next question",
                    onClick = {
                        if (safeIndex == quizQuestions.lastIndex) {
                            onQuizCompleted(score.correctAnswers, score.percentage)
                            isQuizComplete = true
                        } else {
                            currentIndex = safeIndex + 1
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun QuizEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.Center
    ) {
        EmptyState(
            title = title,
            description = description
        )
    }
}

@Composable
private fun QuizProgress(
    currentIndex: Int,
    totalQuestions: Int
) {
    LearnLiftCard {
        Text(
            text = "Question ${currentIndex + 1} of $totalQuestions",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalQuestions.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    }
}

@Composable
private fun QuizQuestionCard(
    studyPathId: String,
    question: QuizQuestion,
    selectedAnswerId: String?,
    aiCoachRepository: AiCoachRepository,
    onAnswerSelected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var aiExplanationState by remember(question.id, selectedAnswerId) {
        mutableStateOf<AiCoachUiState<ExplainAnswerResponse>>(AiCoachUiState.Idle)
    }

    LearnLiftCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
        ) {
            QuizMetaPill(text = question.topic)
            QuizMetaPill(text = question.difficulty)
        }
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = question.question,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        question.options.forEach { option ->
            QuizOptionCard(
                option = option,
                correctAnswerId = question.correctAnswerId,
                selectedAnswerId = selectedAnswerId,
                onAnswerSelected = onAnswerSelected
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        }
        if (selectedAnswerId != null) {
            val isCorrect = selectedAnswerId == question.correctAnswerId
            val selectedOption = question.options.firstOrNull { it.id == selectedAnswerId }
            val correctOption = question.options.firstOrNull { it.id == question.correctAnswerId }
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = if (isCorrect) "Correct" else "Not quite",
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = question.explanation,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium
            )
            if (!isCorrect && selectedOption != null && correctOption != null) {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                SecondaryActionButton(
                    text = if (aiExplanationState is AiCoachUiState.Loading) {
                        "Asking AI Coach..."
                    } else {
                        "Explain with AI Coach"
                    },
                    onClick = {
                        coroutineScope.launch {
                            aiExplanationState = AiCoachUiState.Loading
                            aiExplanationState = when (
                                val result = aiCoachRepository.explainAnswer(
                                    ExplainAnswerRequest(
                                        studyPathId = studyPathId,
                                        topic = question.topic,
                                        difficulty = question.difficulty,
                                        question = question.question,
                                        selectedAnswer = selectedOption.text,
                                        correctAnswer = correctOption.text,
                                        staticExplanation = question.explanation
                                    )
                                )
                            ) {
                                is AiCoachResult.Success -> AiCoachUiState.Success(result.data)
                                is AiCoachResult.Error -> AiCoachUiState.Error(result.message)
                            }
                        }
                    },
                    enabled = aiExplanationState !is AiCoachUiState.Loading
                )
                AiExplainAnswerResult(state = aiExplanationState)
            }
        }
    }
}

@Composable
private fun QuizOptionCard(
    option: QuizOption,
    correctAnswerId: String,
    selectedAnswerId: String?,
    onAnswerSelected: (String) -> Unit
) {
    val hasAnswered = selectedAnswerId != null
    val isSelected = selectedAnswerId == option.id
    val isCorrect = option.id == correctAnswerId
    val borderColor = when {
        hasAnswered && isCorrect -> MaterialTheme.colorScheme.primary
        hasAnswered && isSelected -> MaterialTheme.colorScheme.secondary
        isSelected -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
    }
    val containerColor = when {
        hasAnswered && isCorrect -> MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        hasAnswered && isSelected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !hasAnswered) { onAnswerSelected(option.id) },
        shape = RoundedCornerShape(LearnLiftCorners.button),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = option.text,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected || hasAnswered && isCorrect) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun QuizScoreStats(score: QuizScore) {
    StatCard(
        label = "Current score",
        value = "${score.percentage}%",
        helperText = "${score.correctAnswers} correct - ${score.incorrectAnswers} incorrect"
    )
}

@Composable
private fun QuizSummary(
    studyPathId: String,
    score: QuizScore,
    weakTopics: List<String>,
    aiCoachRepository: AiCoachRepository,
    onRestartQuiz: () -> Unit
) {
    val recommendation = SmartCoachAdvisor.quizSummaryRecommendation(
        percentage = score.percentage,
        weakTopics = weakTopics
    )
    val coroutineScope = rememberCoroutineScope()
    var aiSummaryState by remember(score, weakTopics) {
        mutableStateOf<AiCoachUiState<QuizSummaryResponse>>(AiCoachUiState.Idle)
    }

    LearnLiftCard {
        Text(
            text = "Quiz summary",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "${score.percentage}%",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "${score.correctAnswers} correct and ${score.incorrectAnswers} incorrect out of ${score.totalQuestions} questions",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "Weak topics",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (weakTopics.isEmpty()) {
                "No weak topics this time. Strong work."
            } else {
                weakTopics.joinToString(separator = ", ")
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryActionButton(
            text = "Restart quiz",
            onClick = onRestartQuiz
        )
    }
    SmartCoachRecommendationCard(recommendation = recommendation)
    AiQuizSummarySection(
        state = aiSummaryState,
        onGenerate = {
            coroutineScope.launch {
                aiSummaryState = AiCoachUiState.Loading
                aiSummaryState = when (
                    val result = aiCoachRepository.quizSummary(
                        QuizSummaryRequest(
                            studyPathId = studyPathId,
                            score = score.correctAnswers,
                            totalQuestions = score.totalQuestions,
                            incorrectTopics = weakTopics,
                            weakTopics = weakTopics
                        )
                    )
                ) {
                    is AiCoachResult.Success -> AiCoachUiState.Success(result.data)
                    is AiCoachResult.Error -> AiCoachUiState.Error(result.message)
                }
            }
        }
    )
}

@Composable
private fun AiExplainAnswerResult(state: AiCoachUiState<ExplainAnswerResponse>) {
    when (state) {
        AiCoachUiState.Idle -> Unit
        AiCoachUiState.Loading -> {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "AI Coach is preparing a short explanation.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        is AiCoachUiState.Success -> {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Text(
                text = state.data.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = state.data.conciseExplanation,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = state.data.whyCorrectAnswerWorks,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Study tip: ${state.data.studyTip}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        is AiCoachUiState.Error -> {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = state.message,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AiQuizSummarySection(
    state: AiCoachUiState<QuizSummaryResponse>,
    onGenerate: () -> Unit
) {
    LearnLiftCard {
        Text(
            text = "AI Study Review",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Optional AI help. Uses only this quiz score and missed topics.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        SecondaryActionButton(
            text = if (state is AiCoachUiState.Loading) {
                "Generating review..."
            } else {
                "Generate AI Study Review"
            },
            onClick = onGenerate,
            enabled = state !is AiCoachUiState.Loading
        )
        when (state) {
            AiCoachUiState.Idle -> Unit
            AiCoachUiState.Loading -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = "AI Coach is reviewing this quiz.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            is AiCoachUiState.Success -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                Text(
                    text = state.data.summary,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = "Focus: ${state.data.recommendedFocus.joinToString(", ")}",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = state.data.nextSessionSuggestion,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = state.data.encouragement,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            is AiCoachUiState.Error -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = "${state.message} The local Recommended Focus above is still available.",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun QuizMetaPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                shape = RoundedCornerShape(LearnLiftCorners.highlight)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold
    )
}

private data class QuizScore(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val percentage: Int
)

private fun rememberQuizScore(
    questions: List<QuizQuestion>,
    selectedAnswers: Map<String, String>
): QuizScore {
    val correctAnswers = questions.count { question ->
        selectedAnswers[question.id] == question.correctAnswerId
    }
    val answeredCount = selectedAnswers.size
    val incorrectAnswers = answeredCount - correctAnswers
    val percentage = if (questions.isEmpty()) {
        0
    } else {
        (correctAnswers * 100) / questions.size
    }

    return QuizScore(
        totalQuestions = questions.size,
        correctAnswers = correctAnswers,
        incorrectAnswers = incorrectAnswers,
        percentage = percentage
    )
}

private fun rememberWeakTopics(
    questions: List<QuizQuestion>,
    selectedAnswers: Map<String, String>
): List<String> {
    return questions
        .filter { question -> selectedAnswers[question.id] != question.correctAnswerId }
        .map { it.topic }
        .distinct()
}
