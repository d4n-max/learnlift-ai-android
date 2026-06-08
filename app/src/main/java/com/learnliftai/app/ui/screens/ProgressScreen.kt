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
import com.learnliftai.app.data.ai.AiUsageAction
import com.learnliftai.app.data.ai.AiUsageDecision
import com.learnliftai.app.data.ai.AiUsageRepository
import com.learnliftai.app.data.ai.AiUsageState
import com.learnliftai.app.data.ai.StudyPlanRequest
import com.learnliftai.app.data.ai.StudyPlanResponse
import com.learnliftai.app.domain.SmartCoachAdvisor
import com.learnliftai.app.domain.model.FlashcardReviewSummary
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.domain.model.TopicPerformance
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
    onboardingGoal: String?,
    dailyStudyMinutes: Int,
    isPremiumActive: Boolean,
    aiUsageState: AiUsageState,
    aiUsageRepository: AiUsageRepository,
    topicPerformance: List<TopicPerformance>,
    flashcardReviewSummary: FlashcardReviewSummary,
    onStartAdaptiveQuiz: () -> Unit,
    onStartSmartReview: () -> Unit,
    onStartDailySession: () -> Unit,
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
        StudyPathProgressCard(
            selectedStudyPath = selectedStudyPath,
            isPremiumActive = isPremiumActive
        )
        ProgressRecommendationSection(
            userProgress = userProgress,
            topicPerformance = topicPerformance,
            onStartAdaptiveQuiz = onStartAdaptiveQuiz
        )
        WeakTopicsSection(
            topicPerformance = topicPerformance,
            isPremiumActive = isPremiumActive,
            onStartAdaptiveQuiz = onStartAdaptiveQuiz
        )

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
        FlashcardReviewSection(
            summary = flashcardReviewSummary,
            onStartSmartReview = onStartSmartReview
        )
        QuizPerformanceCard(userProgress = userProgress)
        StudyPlanAiSection(
            selectedStudyPath = selectedStudyPath,
            aiCoachRepository = aiCoachRepository,
            isPremiumActive = isPremiumActive,
            aiUsageState = aiUsageState,
            aiUsageRepository = aiUsageRepository,
            userProgress = userProgress,
            topicPerformance = topicPerformance,
            flashcardReviewSummary = flashcardReviewSummary,
            onboardingGoal = onboardingGoal,
            dailyStudyMinutes = dailyStudyMinutes,
            onViewPremium = onViewPremium,
            onStartDailySession = onStartDailySession
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
private fun FlashcardReviewSection(
    summary: FlashcardReviewSummary,
    onStartSmartReview: () -> Unit
) {
    SectionHeader(
        title = "Flashcard Review",
        subtitle = "Simple local spaced repetition for cards saved on this device."
    )
    LearnLiftCard {
        Text(
            text = "${summary.dueToday} due today",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "${summary.needsReview} need review - ${summary.known} known - ${summary.newCards} new",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Smart Review brings due and Needs Review cards back first. Regular Flashcards still shows every card.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryActionButton(
            text = "Start Smart Review",
            onClick = onStartSmartReview
        )
    }
}

@Composable
private fun StudyPlanAiSection(
    selectedStudyPath: StudyPath?,
    aiCoachRepository: AiCoachRepository,
    isPremiumActive: Boolean,
    aiUsageState: AiUsageState,
    aiUsageRepository: AiUsageRepository,
    userProgress: UserProgress,
    topicPerformance: List<TopicPerformance>,
    flashcardReviewSummary: FlashcardReviewSummary,
    onboardingGoal: String?,
    dailyStudyMinutes: Int,
    onViewPremium: () -> Unit,
    onStartDailySession: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var studyPlanState by remember(selectedStudyPath?.id) {
        mutableStateOf<AiCoachUiState<StudyPlanResponse>>(AiCoachUiState.Idle)
    }
    var studyPlanLimitReached by remember(selectedStudyPath?.id) {
        mutableStateOf(false)
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
            text = if (isPremiumActive) {
                "Premium AI planning uses your selected path, goal, weak topics, due review count, and recent quiz score."
            } else {
                "Premium helps you plan what to study next."
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        if (!isPremiumActive) {
            Text(
                text = "Create a 7-day AI Study Plan",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Premium helps you plan what to study next while your local study tools stay available.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            PrimaryActionButton(
                text = "View Premium",
                onClick = onViewPremium
            )
            return@LearnLiftCard
        }
        ProgressAiUsageStatusText(
            action = AiUsageAction.StudyPlan,
            usageState = aiUsageState,
            isPremiumActive = isPremiumActive
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        val requestStudyPlan = {
            val path = selectedStudyPath
            if (path != null && studyPlanState !is AiCoachUiState.Loading) {
                coroutineScope.launch {
                    when (
                        val usageDecision = aiUsageRepository.consumeIfAvailable(
                            action = AiUsageAction.StudyPlan,
                            isPremium = isPremiumActive
                        )
                    ) {
                        is AiUsageDecision.Blocked -> {
                            studyPlanLimitReached = !isPremiumActive
                            studyPlanState = AiCoachUiState.Error(usageDecision.message)
                            return@launch
                        }

                        is AiUsageDecision.Allowed -> {
                            studyPlanLimitReached = false
                        }
                    }
                    studyPlanState = AiCoachUiState.Loading
                    studyPlanState = when (
                        val result = aiCoachRepository.studyPlan(
                            StudyPlanRequest(
                                studyPathId = path.id,
                                studyPathTitle = path.title,
                                onboardingGoal = onboardingGoal,
                                dailyStudyMinutes = dailyStudyMinutes,
                                weakTopics = topicPerformance.studyPlanWeakTopics(),
                                dueSmartReviewCount = flashcardReviewSummary.dueToday,
                                recentQuizSummary = userProgress.recentQuizSummary(),
                                planState = if (isPremiumActive) "premium" else "free",
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
                "Building your 7-day study plan..."
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
                ProgressAiLoadingPanel(message = "Building your 7-day study plan...")
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
                    Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                    PrimaryActionButton(
                        text = "Start Daily Session",
                        onClick = onStartDailySession
                    )
                }
            }

            is AiCoachUiState.Error -> {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                ProgressAiPanel(subtitle = "Local recommendation available") {
                    Text(
                        text = "AI Study Plan is temporarily unavailable. Try again later or continue with your daily session.",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
                    if (studyPlanLimitReached && !isPremiumActive) {
                        PrimaryActionButton(
                            text = "View Premium",
                            onClick = onViewPremium
                        )
                    } else {
                        SecondaryActionButton(
                            text = "Retry 7-Day Plan",
                            onClick = requestStudyPlan
                        )
                    }
                }
            }
        }
    }
}

private fun List<TopicPerformance>.studyPlanWeakTopics(): List<String> {
    return sortedWith(
        compareByDescending<TopicPerformance> { it.needsReview }
            .thenByDescending { it.weaknessScore }
            .thenByDescending { it.wrongAnswers }
            .thenBy { it.topic.lowercase() }
    )
        .map { it.topic }
        .distinct()
        .take(5)
}

private fun UserProgress.recentQuizSummary(): String? {
    return if (totalQuizzesCompleted <= 0) {
        null
    } else {
        "Last quiz: $lastQuizPercentage% with $lastQuizScore correct"
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
private fun ProgressAiUsageStatusText(
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
private fun AdvancedInsightsTeaser(
    isPremiumActive: Boolean,
    onViewPremium: () -> Unit
) {
    PremiumTeaserCard(
        title = if (isPremiumActive) {
            "Premium study guidance active"
        } else {
            "Want deeper study guidance?"
        },
        description = if (isPremiumActive) {
            "Use AI Study Plans and Premium review support alongside your local progress tools."
        } else {
            "Premium adds AI Study Plans and richer review support. Basic progress and weak topics stay available."
        },
        label = if (isPremiumActive) "Active" else "Premium",
        actionText = "View Premium",
        onActionClick = onViewPremium
    )
}

@Composable
private fun ProgressRecommendationSection(
    userProgress: UserProgress,
    topicPerformance: List<TopicPerformance>,
    onStartAdaptiveQuiz: () -> Unit
) {
    val recommendation = SmartCoachAdvisor.progressRecommendation(userProgress, topicPerformance)
    SectionHeader(
        title = "Recommended Focus",
        subtitle = "Local guidance based only on progress saved on this device."
    )
    SmartCoachRecommendationCard(
        recommendation = recommendation,
        localGuidanceLabel = "Local rule-based guidance - no live AI or data sharing",
        onActionClick = if (recommendation.actionLabel == "Start Adaptive Quiz") {
            onStartAdaptiveQuiz
        } else {
            null
        }
    )
}

@Composable
private fun WeakTopicsSection(
    topicPerformance: List<TopicPerformance>,
    isPremiumActive: Boolean,
    onStartAdaptiveQuiz: () -> Unit
) {
    SectionHeader(
        title = "Topics to Review",
        subtitle = if (topicPerformance.isEmpty()) {
            "Complete a quiz to unlock topic insights."
        } else {
            "Local topic signals from quizzes and flashcards on this device."
        }
    )

    if (topicPerformance.isEmpty()) {
        LearnLiftCard {
            Text(
                text = "No topic data yet",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Answer quiz questions or mark flashcards to build local topic insights.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            SecondaryActionButton(
                text = "Start Adaptive Quiz",
                onClick = onStartAdaptiveQuiz
            )
        }
        return
    }

    val rankedTopics = topicPerformance
        .sortedWith(
            compareByDescending<TopicPerformance> { it.needsReview }
                .thenByDescending { it.weaknessScore }
                .thenByDescending { it.wrongAnswers }
                .thenBy { it.topic.lowercase() }
        )
        .take(5)

    rankedTopics.forEach { topic ->
        TopicPerformanceCard(
            topicPerformance = topic,
            isPremiumActive = isPremiumActive
        )
    }
    LearnLiftCard {
        Text(
            text = "Practice these topics",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Start an Adaptive Quiz that prioritizes your local review signals.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryActionButton(
            text = "Start Adaptive Quiz",
            onClick = onStartAdaptiveQuiz
        )
    }
}

@Composable
private fun TopicPerformanceCard(
    topicPerformance: TopicPerformance,
    isPremiumActive: Boolean
) {
    LearnLiftCard {
        Text(
            text = topicPerformance.topic,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = topicPerformance.statusLabel,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (topicPerformance.totalAttempts < 2) {
                "${topicPerformance.totalAttempts} local signal - needs more practice data"
            } else {
                "${topicPerformance.accuracyPercent}% accuracy - ${topicPerformance.wrongAnswers} review signal(s)"
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        if (isPremiumActive) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Premium insight: use this topic for your next focused AI review.",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
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
private fun StudyPathProgressCard(
    selectedStudyPath: StudyPath?,
    isPremiumActive: Boolean
) {
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
            if (selectedStudyPath.isPremium && !isPremiumActive && selectedStudyPath.freePreviewCount > 0) {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = "Preview mode: progress uses only the visible preview items for this pack.",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
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
