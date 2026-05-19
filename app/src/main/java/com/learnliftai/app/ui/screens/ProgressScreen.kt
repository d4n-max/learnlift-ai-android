package com.learnliftai.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.learnliftai.app.data.ai.StudyPlanRequest
import com.learnliftai.app.data.ai.StudyPlanResponse
import com.learnliftai.app.domain.SmartCoachAdvisor
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.domain.model.UserProgress
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.PremiumTeaserCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.SmartCoachRecommendationCard
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing
import kotlinx.coroutines.launch

@Composable
fun ProgressScreen(
    selectedStudyPath: StudyPath?,
    userProgress: UserProgress,
    isPremiumActive: Boolean,
    onOpenSettings: () -> Unit,
    onViewPremium: () -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirmation by remember { mutableStateOf(false) }
    val aiCoachRepository = remember { AiCoachRepository() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        SectionHeader(
            title = "Overall Progress",
            subtitle = "Progress on this device is saved locally across all study paths."
        )

        StreakHighlightCard(userProgress = userProgress)
        StudyPathProgressCard(selectedStudyPath = selectedStudyPath)
        ProgressRecommendationSection(userProgress = userProgress)

        SectionHeader(title = "Study stats")
        StatCard(
            label = "Total flashcards reviewed",
            value = userProgress.totalFlashcardsReviewed.toString(),
            helperText = "All cards marked during review on this device"
        )
        StatCard(
            label = "Known cards",
            value = userProgress.totalKnownCards.toString(),
            helperText = "Cards marked Known across all paths"
        )
        StatCard(
            label = "Needs review",
            value = userProgress.totalNeedsReviewCards.toString(),
            helperText = "Cards marked Needs Review across all paths"
        )
        StatCard(
            label = "Quizzes completed",
            value = userProgress.totalQuizzesCompleted.toString(),
            helperText = "Completed quiz sessions on this device"
        )

        KnownNeedsReviewBreakdown(userProgress = userProgress)
        QuizPerformanceCard(userProgress = userProgress)
        StudyPlanAiSection(
            selectedStudyPath = selectedStudyPath,
            aiCoachRepository = aiCoachRepository
        )
        AdvancedInsightsTeaser(
            isPremiumActive = isPremiumActive,
            onViewPremium = onViewPremium
        )

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
private fun StudyPlanAiSection(
    selectedStudyPath: StudyPath?,
    aiCoachRepository: AiCoachRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var studyPlanState by remember(selectedStudyPath?.id) {
        mutableStateOf<AiCoachUiState<StudyPlanResponse>>(AiCoachUiState.Idle)
    }

    LearnLiftCard {
        Text(
            text = "7-Day Study Plan",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Optional AI planning based on your selected study path. No personal profile data is sent.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        val requestStudyPlan = {
            val path = selectedStudyPath
            if (path != null && studyPlanState !is AiCoachUiState.Loading) {
                coroutineScope.launch {
                    studyPlanState = AiCoachUiState.Loading
                    studyPlanState = when (
                        val result = aiCoachRepository.studyPlan(
                            StudyPlanRequest(
                                studyPathId = path.id,
                                goal = "Build confidence with ${path.title}",
                                days = 7,
                                level = "beginner"
                            )
                        )
                    ) {
                        is AiCoachResult.Success -> AiCoachUiState.Success(result.data)
                        is AiCoachResult.Error -> AiCoachUiState.Error(result.message)
                    }
                }
            }
        }
        PrimaryActionButton(
            text = if (studyPlanState is AiCoachUiState.Loading) {
                "AI Coach is drafting..."
            } else {
                "Generate 7-Day Study Plan"
            },
            onClick = requestStudyPlan,
            enabled = selectedStudyPath != null && studyPlanState !is AiCoachUiState.Loading
        )
        when (val state = studyPlanState) {
            AiCoachUiState.Idle -> Unit
            AiCoachUiState.Loading -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                ProgressAiLoadingPanel(message = "AI Coach is drafting a short plan.")
            }

            is AiCoachUiState.Success -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                ProgressAiPanel(subtitle = "7-day plan") {
                    Text(
                        text = state.data.title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                    state.data.days.forEach { day ->
                        ProgressAiDayBlock(
                            day = day.day,
                            focus = day.focus,
                            tasks = day.tasks
                        )
                    }
                }
            }

            is AiCoachUiState.Error -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                ProgressAiPanel(subtitle = "Local recommendation available") {
                    Text(
                        text = "${state.message} Keep using the local Recommended Focus above for now.",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                    SecondaryActionButton(
                        text = "Retry 7-Day Plan",
                        onClick = requestStudyPlan
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressAiPanel(
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
        }
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        content()
    }
}

@Composable
private fun ProgressAiLoadingPanel(message: String) {
    ProgressAiPanel(subtitle = "Request in progress") {
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
private fun ProgressAiDayBlock(
    day: Int,
    focus: String,
    tasks: List<String>
) {
    Text(
        text = "Day $day",
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
        text = focus,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = tasks.joinToString(separator = "\n") { "- $it" },
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
        style = MaterialTheme.typography.bodySmall
    )
    Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
}

@Composable
private fun AdvancedInsightsTeaser(
    isPremiumActive: Boolean,
    onViewPremium: () -> Unit
) {
    PremiumTeaserCard(
        title = if (isPremiumActive) "Premium Insights" else "Advanced Insights",
        description = if (isPremiumActive) {
            "Premium is active. Deeper topic trends and personalized guidance are planned next."
        } else {
            "Premium will unlock deeper topic trends and personalized guidance."
        },
        actionText = if (isPremiumActive) "View Premium" else "View Premium",
        onActionClick = onViewPremium
    )
}

@Composable
private fun ProgressRecommendationSection(userProgress: UserProgress) {
    SectionHeader(
        title = "Recommended Focus",
        subtitle = "Local guidance based only on progress saved on this device."
    )
    SmartCoachRecommendationCard(
        recommendation = SmartCoachAdvisor.progressRecommendation(userProgress),
        localGuidanceLabel = "Local rule-based guidance - no live AI or data sharing"
    )
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
            description = "Select a path from Home. Overall progress totals will continue to stay local on this device."
        )
    } else {
        LearnLiftCard {
            Text(
                text = "Selected study path",
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
                text = "Review flashcards to see overall Known and Needs Review totals here.",
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
