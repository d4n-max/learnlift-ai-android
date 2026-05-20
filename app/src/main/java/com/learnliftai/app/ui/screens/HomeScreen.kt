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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.learnliftai.app.domain.SmartCoachAdvisor
import com.learnliftai.app.domain.model.StudyContent
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.domain.model.TopicPerformance
import com.learnliftai.app.domain.model.UserProgress
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.LearnLiftLogo
import com.learnliftai.app.ui.components.PremiumTeaserCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.SmartCoachRecommendationCard
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun HomeScreen(
    selectedStudyPath: StudyPath?,
    selectedStudyContent: StudyContent?,
    userProgress: UserProgress,
    topicPerformance: List<TopicPerformance>,
    isPremiumActive: Boolean,
    onChooseStudyPath: () -> Unit,
    onOpenSettings: () -> Unit,
    onViewPremium: () -> Unit,
    onStartDailySession: () -> Unit,
    onStartFlashcards: () -> Unit,
    onStartQuiz: () -> Unit,
    onStartAdaptiveQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        HomeBrandHeader(onOpenSettings = onOpenSettings)

        if (selectedStudyPath == null) {
            EmptyState(
                title = "Choose your first study path",
                description = "Pick one goal to unlock flashcards, quiz questions, and a focused dashboard for your practice.",
                actionText = "Choose Study Path",
                onActionClick = onChooseStudyPath
            )
        } else {
            SelectedPathOverview(
                selectedStudyPath = selectedStudyPath,
                selectedStudyContent = selectedStudyContent,
                onChangeStudyPath = onChooseStudyPath
            )
            QuickActions(
                onStartDailySession = onStartDailySession,
                onStartFlashcards = onStartFlashcards,
                onStartQuiz = onStartQuiz,
                onStartAdaptiveQuiz = onStartAdaptiveQuiz,
                onChangeStudyPath = onChooseStudyPath
            )
            HomeRecommendation(
                userProgress = userProgress,
                topicPerformance = topicPerformance
            )
            HomePremiumTeaser(
                isPremiumActive = isPremiumActive,
                onViewPremium = onViewPremium
            )
            DashboardStats(
                selectedStudyPath = selectedStudyPath,
                selectedStudyContent = selectedStudyContent,
                userProgress = userProgress
            )
        }
    }
}

@Composable
private fun HomePremiumTeaser(
    isPremiumActive: Boolean,
    onViewPremium: () -> Unit
) {
    PremiumTeaserCard(
        title = if (isPremiumActive) "Premium active" else "Premium",
        description = if (isPremiumActive) {
            "Thanks for supporting LearnLift AI. Premium benefits will expand as new features roll out."
        } else {
            "AI explanations, study plans, and unlimited practice are planned for Premium."
        },
        actionText = if (isPremiumActive) "View Premium" else "View benefits",
        onActionClick = onViewPremium
    )
}

@Composable
private fun HomeRecommendation(
    userProgress: UserProgress,
    topicPerformance: List<TopicPerformance>
) {
    SmartCoachRecommendationCard(
        recommendation = SmartCoachAdvisor.homeRecommendation(userProgress, topicPerformance),
        localGuidanceLabel = "Local guidance"
    )
}

@Composable
private fun SelectedPathOverview(
    selectedStudyPath: StudyPath,
    selectedStudyContent: StudyContent?,
    onChangeStudyPath: () -> Unit
) {
    SectionHeader(
        title = "Today's focus",
        subtitle = "Start with one focused step for ${selectedStudyPath.accentLabel.lowercase()} practice."
    )
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
            style = MaterialTheme.typography.headlineSmall,
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
        if (selectedStudyContent != null) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Text(
                text = "${selectedStudyContent.flashcards.size} flashcards and ${selectedStudyContent.quizQuestions.size} quiz questions ready",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        SecondaryActionButton(
            text = "Change Study Path",
            onClick = onChangeStudyPath
        )
    }
}

@Composable
private fun DashboardStats(
    selectedStudyPath: StudyPath,
    selectedStudyContent: StudyContent?,
    userProgress: UserProgress
) {
    SectionHeader(
        title = "Overall progress on this device",
        subtitle = "These totals are saved locally and are not separated by study path yet."
    )
    StatCard(
        label = "Current streak",
        value = "${userProgress.currentStudyStreak} days",
        helperText = userProgress.lastStudyDate?.let { "Last studied $it" } ?: "Complete a quiz or review a card to start"
    )
    StatCard(
        label = "Total flashcards reviewed",
        value = userProgress.totalFlashcardsReviewed.toString(),
        helperText = "${userProgress.totalKnownCards} known - ${userProgress.totalNeedsReviewCards} need review"
    )
    StatCard(
        label = "Last quiz score",
        value = if (userProgress.totalQuizzesCompleted == 0) "Not yet" else "${userProgress.lastQuizPercentage}%",
        helperText = if (userProgress.totalQuizzesCompleted == 0) {
            "Take a quiz to save your first score"
        } else {
            "${userProgress.lastQuizScore} correct on the last completed quiz"
        }
    )
    StatCard(
        label = "Flashcards available",
        value = (selectedStudyContent?.flashcards?.size ?: 0).toString(),
        helperText = "Loaded from local study content"
    )
    StatCard(
        label = "Quiz questions available",
        value = (selectedStudyContent?.quizQuestions?.size ?: 0).toString(),
        helperText = "Ready for Quiz Mode"
    )
    StatCard(
        label = "Daily time",
        value = selectedStudyPath.estimatedDailyTime,
        helperText = "Suggested study rhythm"
    )
    StatCard(
        label = "Difficulty",
        value = selectedStudyPath.difficultyLabel,
        helperText = selectedStudyPath.accentLabel
    )
}

@Composable
private fun QuickActions(
    onStartDailySession: () -> Unit,
    onStartFlashcards: () -> Unit,
    onStartQuiz: () -> Unit,
    onStartAdaptiveQuiz: () -> Unit,
    onChangeStudyPath: () -> Unit
) {
    SectionHeader(
        title = "Quick actions",
        subtitle = "Jump into the study mode you want right now."
    )
    LearnLiftCard {
        PrimaryActionButton(
            text = "Start Daily Session",
            onClick = onStartDailySession
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        PrimaryActionButton(
            text = "Start Flashcards",
            onClick = onStartFlashcards
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        PrimaryActionButton(
            text = "Start Quiz",
            onClick = onStartQuiz
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        SecondaryActionButton(
            text = "Adaptive Quiz",
            onClick = onStartAdaptiveQuiz
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        SecondaryActionButton(
            text = "Change Study Path",
            onClick = onChangeStudyPath
        )
    }
}

@Composable
private fun HomeBrandHeader(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            LearnLiftLogo()
            Spacer(modifier = Modifier.height(LearnLiftSpacing.logoToTitle))
            Text(
                text = "LearnLift AI",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.titleToTagline))
            Text(
                text = "Elevate Your Skills, Effortlessly.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.76f),
                style = MaterialTheme.typography.titleMedium
            )
        }
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
