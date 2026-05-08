package com.learnliftai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.domain.model.UserProgress
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun ProgressScreen(
    selectedStudyPath: StudyPath?,
    userProgress: UserProgress,
    onOpenSettings: () -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        SectionHeader(
            title = "Your Progress",
            subtitle = "Small steps compound into real skills."
        )

        StreakHighlightCard(userProgress = userProgress)
        StudyPathProgressCard(selectedStudyPath = selectedStudyPath)

        SectionHeader(title = "Study stats")
        StatCard(
            label = "Flashcards reviewed",
            value = userProgress.totalFlashcardsReviewed.toString(),
            helperText = "Total cards marked during review"
        )
        StatCard(
            label = "Known cards",
            value = userProgress.totalKnownCards.toString(),
            helperText = "Cards marked Known"
        )
        StatCard(
            label = "Needs review",
            value = userProgress.totalNeedsReviewCards.toString(),
            helperText = "Cards marked Needs Review"
        )
        StatCard(
            label = "Quizzes completed",
            value = userProgress.totalQuizzesCompleted.toString(),
            helperText = "Completed quiz sessions"
        )

        KnownNeedsReviewBreakdown(userProgress = userProgress)
        QuizPerformanceCard(userProgress = userProgress)

        SecondaryActionButton(
            text = "Reset Progress Stats",
            onClick = { showResetConfirmation = true }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onOpenSettings) {
                Text(
                    text = "Settings",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text(text = "Reset progress stats?") },
            text = {
                Text(
                    text = "This clears saved stats and streaks on this device. Your selected study path will stay selected."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirmation = false
                        onResetProgress()
                    }
                ) {
                    Text(
                        text = "Reset",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun StreakHighlightCard(userProgress: UserProgress) {
    LearnLiftCard {
        Text(
            text = "Current study streak",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "${userProgress.currentStudyStreak} days",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = userProgress.lastStudyDate?.let {
                "Last studied $it. Keep the rhythm steady."
            } ?: "Review a card or complete a quiz to start your streak.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StudyPathProgressCard(selectedStudyPath: StudyPath?) {
    if (selectedStudyPath == null) {
        EmptyState(
            title = "Choose a study path",
            description = "Select a path from Home to connect your progress to a clear goal."
        )
    } else {
        LearnLiftCard {
            Text(
                text = "Study path",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = selectedStudyPath.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = selectedStudyPath.subtitle,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.84f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = selectedStudyPath.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun KnownNeedsReviewBreakdown(userProgress: UserProgress) {
    if (userProgress.totalFlashcardsReviewed <= 0) {
        LearnLiftCard {
            Text(
                text = "Flashcard breakdown",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Review flashcards to see Known and Needs Review totals here.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    val knownPercentage = (userProgress.totalKnownCards * 100) / userProgress.totalFlashcardsReviewed
    val needsReviewPercentage = (userProgress.totalNeedsReviewCards * 100) / userProgress.totalFlashcardsReviewed

    LearnLiftCard {
        Text(
            text = "Flashcard breakdown",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "${userProgress.totalKnownCards} known ($knownPercentage%)",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "${userProgress.totalNeedsReviewCards} need review ($needsReviewPercentage%)",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun QuizPerformanceCard(userProgress: UserProgress) {
    LearnLiftCard {
        Text(
            text = "Quiz performance",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (userProgress.totalQuizzesCompleted == 0) {
                "No quiz completed yet"
            } else {
                "${userProgress.lastQuizScore} correct - ${userProgress.lastQuizPercentage}%"
            },
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = quizPerformanceMessage(userProgress),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun quizPerformanceMessage(userProgress: UserProgress): String {
    if (userProgress.totalQuizzesCompleted == 0) {
        return "Complete a quiz to see your score."
    }

    return when {
        userProgress.lastQuizPercentage >= 80 -> "Great work - keep the momentum going."
        userProgress.lastQuizPercentage >= 50 -> "Good progress - review weak areas."
        else -> "Keep practicing - every session helps."
    }
}
