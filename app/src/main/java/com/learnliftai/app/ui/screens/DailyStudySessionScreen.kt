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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.domain.model.Flashcard
import com.learnliftai.app.domain.model.QuizOption
import com.learnliftai.app.domain.model.QuizQuestion
import com.learnliftai.app.domain.model.StudyContent
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun DailyStudySessionScreen(
    selectedStudyPath: StudyPath?,
    selectedStudyContent: StudyContent?,
    onFinishSession: (
        reviewedCards: Int,
        knownCards: Int,
        needsReviewCards: Int,
        quizAnswered: Int,
        quizCorrect: Int,
        quizPercentage: Int
    ) -> Unit,
    onReturnHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedStudyPath == null || selectedStudyContent == null) {
        DailySessionEmptyState(
            title = "Choose a study path first",
            description = "Pick a path from Home, then start a guided daily session.",
            onReturnHome = onReturnHome,
            modifier = modifier
        )
        return
    }

    val sessionFlashcards = remember(selectedStudyPath.id) {
        selectedStudyContent.flashcards.take(DailySessionItemLimit)
    }
    val sessionQuestions = remember(selectedStudyPath.id) {
        selectedStudyContent.quizQuestions.take(DailySessionItemLimit)
    }

    if (sessionFlashcards.isEmpty() && sessionQuestions.isEmpty()) {
        DailySessionEmptyState(
            title = "No daily content yet",
            description = "${selectedStudyPath.title} does not have session content available yet.",
            onReturnHome = onReturnHome,
            modifier = modifier
        )
        return
    }

    var phase by remember { mutableStateOf(DailySessionPhase.Intro) }
    var flashcardIndex by remember { mutableIntStateOf(0) }
    var quizIndex by remember { mutableIntStateOf(0) }
    var isAnswerRevealed by remember { mutableStateOf(false) }
    var selectedQuizAnswerId by remember { mutableStateOf<String?>(null) }
    var knownCount by remember { mutableIntStateOf(0) }
    var needsReviewCount by remember { mutableIntStateOf(0) }
    var quizCorrectCount by remember { mutableIntStateOf(0) }
    var hasSavedSession by remember { mutableStateOf(false) }
    var isFlashcardActionLocked by remember { mutableStateOf(false) }
    var isQuizAnswerLocked by remember { mutableStateOf(false) }
    var isQuizAdvanceLocked by remember { mutableStateOf(false) }
    var isFinishingSession by remember { mutableStateOf(false) }
    val incorrectTopics = remember { mutableStateListOf<String>() }
    val needsReviewTopics = remember { mutableStateListOf<String>() }

    LaunchedEffect(selectedStudyPath.id) {
        phase = DailySessionPhase.Intro
        flashcardIndex = 0
        quizIndex = 0
        isAnswerRevealed = false
        selectedQuizAnswerId = null
        knownCount = 0
        needsReviewCount = 0
        quizCorrectCount = 0
        hasSavedSession = false
        isFlashcardActionLocked = false
        isQuizAnswerLocked = false
        isQuizAdvanceLocked = false
        isFinishingSession = false
        incorrectTopics.clear()
        needsReviewTopics.clear()
    }

    LaunchedEffect(phase, flashcardIndex, quizIndex) {
        isFlashcardActionLocked = false
        isQuizAnswerLocked = false
        isQuizAdvanceLocked = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        when (phase) {
            DailySessionPhase.Intro -> DailySessionIntro(
                selectedStudyPath = selectedStudyPath,
                flashcardCount = sessionFlashcards.size,
                quizQuestionCount = sessionQuestions.size,
                onStart = {
                    phase = if (sessionFlashcards.isNotEmpty()) {
                        DailySessionPhase.Flashcards
                    } else {
                        DailySessionPhase.Quiz
                    }
                },
                onReturnHome = onReturnHome
            )

            DailySessionPhase.Flashcards -> DailySessionFlashcardStep(
                flashcard = sessionFlashcards[flashcardIndex],
                currentIndex = flashcardIndex,
                totalCards = sessionFlashcards.size,
                isAnswerRevealed = isAnswerRevealed,
                onRevealAnswer = { isAnswerRevealed = true },
                onKnown = {
                    if (!isFlashcardActionLocked) {
                        isFlashcardActionLocked = true
                        knownCount += 1
                        moveFromFlashcard(
                            flashcardIndex = flashcardIndex,
                            lastFlashcardIndex = sessionFlashcards.lastIndex,
                            hasQuizQuestions = sessionQuestions.isNotEmpty(),
                            onMoveNextCard = {
                                flashcardIndex += 1
                                isAnswerRevealed = false
                            },
                            onMoveToQuiz = {
                                phase = DailySessionPhase.Quiz
                                isAnswerRevealed = false
                            },
                            onMoveToSummary = { phase = DailySessionPhase.Summary }
                        )
                    }
                },
                onNeedsReview = {
                    if (!isFlashcardActionLocked) {
                        isFlashcardActionLocked = true
                        needsReviewCount += 1
                        needsReviewTopics.add(sessionFlashcards[flashcardIndex].topic)
                        moveFromFlashcard(
                            flashcardIndex = flashcardIndex,
                            lastFlashcardIndex = sessionFlashcards.lastIndex,
                            hasQuizQuestions = sessionQuestions.isNotEmpty(),
                            onMoveNextCard = {
                                flashcardIndex += 1
                                isAnswerRevealed = false
                            },
                            onMoveToQuiz = {
                                phase = DailySessionPhase.Quiz
                                isAnswerRevealed = false
                            },
                            onMoveToSummary = { phase = DailySessionPhase.Summary }
                        )
                    }
                }
            )

            DailySessionPhase.Quiz -> DailySessionQuizStep(
                question = sessionQuestions[quizIndex],
                currentIndex = quizIndex,
                totalQuestions = sessionQuestions.size,
                selectedAnswerId = selectedQuizAnswerId,
                onAnswerSelected = { optionId ->
                    if (selectedQuizAnswerId == null && !isQuizAnswerLocked) {
                        isQuizAnswerLocked = true
                        selectedQuizAnswerId = optionId
                        val question = sessionQuestions[quizIndex]
                        if (optionId == question.correctAnswerId) {
                            quizCorrectCount += 1
                        } else {
                            incorrectTopics.add(question.topic)
                        }
                    }
                },
                onNext = {
                    if (!isQuizAdvanceLocked) {
                        isQuizAdvanceLocked = true
                        if (quizIndex == sessionQuestions.lastIndex) {
                            phase = DailySessionPhase.Summary
                        } else {
                            quizIndex += 1
                            selectedQuizAnswerId = null
                        }
                    }
                }
            )

            DailySessionPhase.Summary -> DailySessionSummary(
                reviewedCards = knownCount + needsReviewCount,
                knownCards = knownCount,
                needsReviewCards = needsReviewCount,
                quizCorrect = quizCorrectCount,
                quizTotal = sessionQuestions.size,
                topicsToReview = (incorrectTopics + needsReviewTopics).distinct(),
                isFinishEnabled = !isFinishingSession,
                onFinish = {
                    val quizPercentage = if (sessionQuestions.isEmpty()) {
                        0
                    } else {
                        (quizCorrectCount * 100) / sessionQuestions.size
                    }
                    if (!hasSavedSession && !isFinishingSession) {
                        isFinishingSession = true
                        hasSavedSession = true
                        onFinishSession(
                            knownCount + needsReviewCount,
                            knownCount,
                            needsReviewCount,
                            sessionQuestions.size,
                            quizCorrectCount,
                            quizPercentage
                        )
                        onReturnHome()
                    }
                }
            )
        }
    }
}

@Composable
private fun DailySessionEmptyState(
    title: String,
    description: String,
    onReturnHome: () -> Unit,
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
            description = description,
            actionText = "Back to Home",
            onActionClick = onReturnHome
        )
    }
}

@Composable
private fun DailySessionIntro(
    selectedStudyPath: StudyPath,
    flashcardCount: Int,
    quizQuestionCount: Int,
    onStart: () -> Unit,
    onReturnHome: () -> Unit
) {
    SectionHeader(
        title = "Daily Session",
        subtitle = selectedStudyPath.title
    )
    LearnLiftCard {
        Text(
            text = "A focused session for today",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Expected time: ${selectedStudyPath.estimatedDailyTime}",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "Includes $flashcardCount flashcards and $quizQuestionCount quiz questions.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryActionButton(
            text = "Start session",
            onClick = onStart
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        SecondaryActionButton(
            text = "Back to Home",
            onClick = onReturnHome
        )
    }
}

@Composable
private fun DailySessionFlashcardStep(
    flashcard: Flashcard,
    currentIndex: Int,
    totalCards: Int,
    isAnswerRevealed: Boolean,
    onRevealAnswer: () -> Unit,
    onKnown: () -> Unit,
    onNeedsReview: () -> Unit
) {
    SectionHeader(
        title = "Flashcards",
        subtitle = "Flashcard ${currentIndex + 1} of $totalCards"
    )
    LearnLiftCard {
        MetaPillRow(first = flashcard.topic, second = flashcard.difficulty)
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = flashcard.question,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        if (isAnswerRevealed) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.sectionGap))
            Text(
                text = "Answer",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = flashcard.answer,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.84f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            PrimaryActionButton(
                text = "Known",
                onClick = onKnown
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            SecondaryActionButton(
                text = "Needs Review",
                onClick = onNeedsReview
            )
        } else {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.sectionGap))
            PrimaryActionButton(
                text = "Reveal answer",
                onClick = onRevealAnswer
            )
        }
    }
}

@Composable
private fun DailySessionQuizStep(
    question: QuizQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedAnswerId: String?,
    onAnswerSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    SectionHeader(
        title = "Quiz",
        subtitle = "Question ${currentIndex + 1} of $totalQuestions"
    )
    LearnLiftCard {
        MetaPillRow(first = question.topic, second = question.difficulty)
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = question.question,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        question.options.forEach { option ->
            DailySessionOptionCard(
                option = option,
                correctAnswerId = question.correctAnswerId,
                selectedAnswerId = selectedAnswerId,
                onAnswerSelected = onAnswerSelected
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        }
        if (selectedAnswerId != null) {
            val isCorrect = selectedAnswerId == question.correctAnswerId
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = if (isCorrect) "Correct" else "Not quite",
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = question.explanation,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            PrimaryActionButton(
                text = if (currentIndex == totalQuestions - 1) "See summary" else "Next question",
                onClick = onNext
            )
        }
    }
}

@Composable
private fun DailySessionSummary(
    reviewedCards: Int,
    knownCards: Int,
    needsReviewCards: Int,
    quizCorrect: Int,
    quizTotal: Int,
    topicsToReview: List<String>,
    isFinishEnabled: Boolean,
    onFinish: () -> Unit
) {
    val quizPercentage = if (quizTotal == 0) 0 else (quizCorrect * 100) / quizTotal

    SectionHeader(
        title = "Session Summary",
        subtitle = "Nice work. This session is ready to save."
    )
    StatCard(
        label = "Cards reviewed",
        value = reviewedCards.toString(),
        helperText = "$knownCards known - $needsReviewCards need review"
    )
    StatCard(
        label = "Quiz score",
        value = if (quizTotal == 0) "No quiz" else "$quizPercentage%",
        helperText = "$quizCorrect correct out of $quizTotal questions"
    )
    LearnLiftCard {
        Text(
            text = "Topics to review",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (topicsToReview.isEmpty()) {
                "No weak topics from this session. Keep the momentum going."
            } else {
                topicsToReview.joinToString(separator = ", ")
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryActionButton(
            text = "Finish and return Home",
            onClick = onFinish,
            enabled = isFinishEnabled
        )
    }
}

@Composable
private fun DailySessionOptionCard(
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
private fun MetaPillRow(first: String, second: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)) {
        SessionMetaPill(text = first)
        SessionMetaPill(text = second)
    }
}

@Composable
private fun SessionMetaPill(text: String) {
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

private fun moveFromFlashcard(
    flashcardIndex: Int,
    lastFlashcardIndex: Int,
    hasQuizQuestions: Boolean,
    onMoveNextCard: () -> Unit,
    onMoveToQuiz: () -> Unit,
    onMoveToSummary: () -> Unit
) {
    when {
        flashcardIndex < lastFlashcardIndex -> onMoveNextCard()
        hasQuizQuestions -> onMoveToQuiz()
        else -> onMoveToSummary()
    }
}

private enum class DailySessionPhase {
    Intro,
    Flashcards,
    Quiz,
    Summary
}

private const val DailySessionItemLimit = 5
