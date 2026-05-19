package com.learnliftai.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.data.ai.AiCoachRepository
import com.learnliftai.app.data.ai.AiCoachResult
import com.learnliftai.app.data.ai.AiCoachUiState
import com.learnliftai.app.data.ai.AiUsageAction
import com.learnliftai.app.data.ai.AiUsageDecision
import com.learnliftai.app.data.ai.AiUsageRepository
import com.learnliftai.app.data.ai.AiUsageState
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
    isPremiumActive: Boolean,
    aiUsageState: AiUsageState,
    aiUsageRepository: AiUsageRepository,
    onViewPremium: () -> Unit,
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
                isPremiumActive = isPremiumActive,
                aiUsageState = aiUsageState,
                aiUsageRepository = aiUsageRepository,
                onViewPremium = onViewPremium,
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
                isPremiumActive = isPremiumActive,
                aiUsageState = aiUsageState,
                aiUsageRepository = aiUsageRepository,
                onViewPremium = onViewPremium,
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
    isPremiumActive: Boolean,
    aiUsageState: AiUsageState,
    aiUsageRepository: AiUsageRepository,
    onViewPremium: () -> Unit,
    onAnswerSelected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var aiExplanationState by remember(question.id, selectedAnswerId) {
        mutableStateOf<AiCoachUiState<ExplainAnswerResponse>>(AiCoachUiState.Idle)
    }
    var aiExplanationLimitReached by remember(question.id, selectedAnswerId) {
        mutableStateOf(false)
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
            if (isCorrect) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = question.explanation,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if (selectedOption != null && correctOption != null) {
                val requestAiExplanation = {
                    if (aiExplanationState !is AiCoachUiState.Loading) {
                        coroutineScope.launch {
                            when (
                                val usageDecision = aiUsageRepository.consumeIfAvailable(
                                    action = AiUsageAction.ExplainAnswer,
                                    isPremium = isPremiumActive
                                )
                            ) {
                                is AiUsageDecision.Blocked -> {
                                    aiExplanationLimitReached = !isPremiumActive
                                    aiExplanationState = AiCoachUiState.Error(usageDecision.message)
                                    return@launch
                                }

                                is AiUsageDecision.Allowed -> {
                                    aiExplanationLimitReached = false
                                }
                            }
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
                    }
                }
                Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                AiUsageStatusText(
                    action = AiUsageAction.ExplainAnswer,
                    usageState = aiUsageState,
                    isPremiumActive = isPremiumActive
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                SecondaryActionButton(
                    text = if (aiExplanationState is AiCoachUiState.Loading) {
                        "AI Coach is thinking..."
                    } else {
                        "Explain with AI Coach"
                    },
                    onClick = requestAiExplanation,
                    enabled = aiExplanationState !is AiCoachUiState.Loading
                )
                AiExplainAnswerResult(
                    state = aiExplanationState,
                    localExplanation = question.explanation,
                    showUpgradeAction = aiExplanationLimitReached && !isPremiumActive,
                    onRetry = requestAiExplanation,
                    onViewPremium = onViewPremium
                )
                LocalExplanationBlock(explanation = question.explanation)
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
    isPremiumActive: Boolean,
    aiUsageState: AiUsageState,
    aiUsageRepository: AiUsageRepository,
    onViewPremium: () -> Unit,
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
    var aiSummaryLimitReached by remember(score, weakTopics) {
        mutableStateOf(false)
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
    val requestAiSummary = {
        if (aiSummaryState !is AiCoachUiState.Loading) {
            coroutineScope.launch {
                when (
                    val usageDecision = aiUsageRepository.consumeIfAvailable(
                        action = AiUsageAction.QuizSummary,
                        isPremium = isPremiumActive
                    )
                ) {
                    is AiUsageDecision.Blocked -> {
                        aiSummaryLimitReached = !isPremiumActive
                        aiSummaryState = AiCoachUiState.Error(usageDecision.message)
                        return@launch
                    }

                    is AiUsageDecision.Allowed -> {
                        aiSummaryLimitReached = false
                    }
                }
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
    }
    AiQuizSummarySection(
        state = aiSummaryState,
        usageState = aiUsageState,
        isPremiumActive = isPremiumActive,
        showUpgradeAction = aiSummaryLimitReached && !isPremiumActive,
        onGenerate = requestAiSummary,
        onViewPremium = onViewPremium
    )
}

@Composable
private fun AiExplainAnswerResult(
    state: AiCoachUiState<ExplainAnswerResponse>,
    localExplanation: String,
    showUpgradeAction: Boolean,
    onRetry: () -> Unit,
    onViewPremium: () -> Unit
) {
    when (state) {
        AiCoachUiState.Idle -> Unit
        AiCoachUiState.Loading -> {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            AiLoadingPanel(message = "AI Coach is thinking...")
        }

        is AiCoachUiState.Success -> {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            AiCoachPanel(
                subtitle = "Personalized explanation"
            ) {
                AiSectionText(
                    label = state.data.title,
                    text = state.data.conciseExplanation
                )
                AiSectionText(
                    label = "Why the correct answer works",
                    text = state.data.whyCorrectAnswerWorks
                )
                AiSectionText(
                    label = "Study tip",
                    text = state.data.studyTip,
                    accent = true
                )
                AiSectionText(
                    label = "Recommended topic",
                    text = state.data.recommendedTopic,
                    accent = true
                )
            }
        }

        is AiCoachUiState.Error -> {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            AiCoachPanel(
                subtitle = "Local explanation available"
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = localExplanation,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                SecondaryActionButton(
                    text = "Retry AI Coach",
                    onClick = onRetry
                )
                if (showUpgradeAction) {
                    Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                    PrimaryActionButton(
                        text = "View Premium",
                        onClick = onViewPremium
                    )
                }
            }
        }
    }
}

@Composable
private fun AiCoachPanel(
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.26f),
                shape = RoundedCornerShape(LearnLiftCorners.medium)
            )
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
                shape = RoundedCornerShape(LearnLiftCorners.medium)
            )
            .padding(LearnLiftSpacing.largeGap)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .size(width = 5.dp, height = 34.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(LearnLiftCorners.highlight)
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Coach",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            QuizMetaPill(text = "Premium-ready")
        }
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        content()
    }
}

@Composable
private fun AiLoadingPanel(message: String) {
    AiCoachPanel(subtitle = "Request in progress") {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.mediumGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AiSectionText(
    label: String,
    text: String,
    accent: Boolean = false
) {
    Text(
        text = label,
        color = if (accent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(LearnLiftSpacing.mediumGap))
}

@Composable
private fun LocalExplanationBlock(explanation: String) {
    Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
    Text(
        text = "Local explanation",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = explanation,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun AiQuizSummarySection(
    state: AiCoachUiState<QuizSummaryResponse>,
    usageState: AiUsageState,
    isPremiumActive: Boolean,
    showUpgradeAction: Boolean,
    onGenerate: () -> Unit,
    onViewPremium: () -> Unit
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
        AiUsageStatusText(
            action = AiUsageAction.QuizSummary,
            usageState = usageState,
            isPremiumActive = isPremiumActive
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        SecondaryActionButton(
            text = if (state is AiCoachUiState.Loading) {
                "Generating your AI study review..."
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
                AiLoadingPanel(message = "Generating your AI study review...")
            }

            is AiCoachUiState.Success -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                AiCoachPanel(subtitle = "AI Study Review") {
                    AiSectionText(
                        label = "Summary",
                        text = state.data.summary
                    )
                    AiSectionText(
                        label = "Recommended focus",
                        text = state.data.recommendedFocus.joinToString(", "),
                        accent = true
                    )
                    AiSectionText(
                        label = "Next session suggestion",
                        text = state.data.nextSessionSuggestion
                    )
                    AiSectionText(
                        label = "Encouragement",
                        text = state.data.encouragement,
                        accent = true
                    )
                }
            }

            is AiCoachUiState.Error -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                AiCoachPanel(subtitle = "Local recommendation available") {
                    Text(
                        text = "${state.message} The local Recommended Focus above is still available.",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                    SecondaryActionButton(
                        text = "Retry AI Study Review",
                        onClick = onGenerate
                    )
                    if (showUpgradeAction) {
                        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                        PrimaryActionButton(
                            text = "View Premium",
                            onClick = onViewPremium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiUsageStatusText(
    action: AiUsageAction,
    usageState: AiUsageState,
    isPremiumActive: Boolean
) {
    val text = if (isPremiumActive) {
        "Premium AI access active"
    } else {
        val limit = usageState.limitFor(action, isPremium = false)
        val remaining = usageState.remainingFor(action, isPremium = false)
        if (limit == 0) {
            "Premium required for this AI action"
        } else if (remaining > 0) {
            "Free AI previews left today: $remaining"
        } else {
            "AI previews reset tomorrow"
        }
    }
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold
    )
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
