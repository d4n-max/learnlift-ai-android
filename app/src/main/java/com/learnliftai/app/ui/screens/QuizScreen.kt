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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.domain.model.QuizOption
import com.learnliftai.app.domain.model.QuizQuestion
import com.learnliftai.app.domain.model.StudyContent
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun QuizScreen(
    selectedStudyPath: StudyPath?,
    selectedStudyContent: StudyContent?,
    modifier: Modifier = Modifier
) {
    val quizQuestions = selectedStudyContent?.quizQuestions.orEmpty()

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
                score = score,
                weakTopics = rememberWeakTopics(quizQuestions, selectedAnswers),
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
                question = currentQuestion,
                selectedAnswerId = selectedAnswerId,
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
    question: QuizQuestion,
    selectedAnswerId: String?,
    onAnswerSelected: (String) -> Unit
) {
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
    score: QuizScore,
    weakTopics: List<String>,
    onRestartQuiz: () -> Unit
) {
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
