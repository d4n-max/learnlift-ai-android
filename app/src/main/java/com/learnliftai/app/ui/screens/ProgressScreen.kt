package com.learnliftai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
            title = "Progress",
            subtitle = selectedStudyPath?.title ?: "Choose a study path from Home to focus your progress."
        )

        if (selectedStudyPath == null && !userProgress.hasAnySavedProgress()) {
            EmptyState(
                title = "No progress yet",
                description = "Choose a study path, review flashcards, or complete a quiz to start saving local progress."
            )
        } else {
            LearnLiftCard {
                Text(
                    text = selectedStudyPath?.title ?: "No study path selected",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = "Saved on this device only.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            StatCard(
                label = "Current study streak",
                value = "${userProgress.currentStudyStreak} days",
                helperText = userProgress.lastStudyDate?.let { "Last studied $it" } ?: "Study today to start a streak"
            )
            StatCard(
                label = "Total flashcards reviewed",
                value = userProgress.totalFlashcardsReviewed.toString(),
                helperText = "Saved after first rating per card in a session"
            )
            StatCard(
                label = "Known cards",
                value = userProgress.totalKnownCards.toString(),
                helperText = "Cards marked Known"
            )
            StatCard(
                label = "Needs review cards",
                value = userProgress.totalNeedsReviewCards.toString(),
                helperText = "Cards marked Needs Review"
            )
            StatCard(
                label = "Total quizzes completed",
                value = userProgress.totalQuizzesCompleted.toString(),
                helperText = "Saved when the quiz summary is reached"
            )
            StatCard(
                label = "Last quiz score",
                value = if (userProgress.totalQuizzesCompleted == 0) "Not yet" else userProgress.lastQuizScore.toString(),
                helperText = if (userProgress.totalQuizzesCompleted == 0) {
                    "Complete a quiz to save a score"
                } else {
                    "${userProgress.lastQuizPercentage}% on the last completed quiz"
                }
            )
            StatCard(
                label = "Last quiz percentage",
                value = if (userProgress.totalQuizzesCompleted == 0) "Not yet" else "${userProgress.lastQuizPercentage}%",
                helperText = "Most recent completed quiz"
            )

            SecondaryActionButton(
                text = "Reset Progress Stats",
                onClick = { showResetConfirmation = true }
            )
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
                    Text(text = "Reset")
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

private fun UserProgress.hasAnySavedProgress(): Boolean {
    return totalFlashcardsReviewed > 0 ||
        totalKnownCards > 0 ||
        totalNeedsReviewCards > 0 ||
        totalQuizzesCompleted > 0 ||
        currentStudyStreak > 0
}
