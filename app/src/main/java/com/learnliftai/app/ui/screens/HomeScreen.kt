package com.learnliftai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
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
import com.learnliftai.app.ui.theme.LearnLiftTypographySizes

@Composable
fun HomeScreen(
    selectedStudyPath: StudyPath?,
    selectedStudyContent: StudyContent?,
    onChooseStudyPath: () -> Unit,
    onStartFlashcards: () -> Unit,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        HomeBrandHeader()

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
            DashboardStats(
                selectedStudyPath = selectedStudyPath,
                selectedStudyContent = selectedStudyContent
            )
            QuickActions(
                onStartFlashcards = onStartFlashcards,
                onStartQuiz = onStartQuiz,
                onChangeStudyPath = onChooseStudyPath
            )
        }
    }
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
    selectedStudyContent: StudyContent?
) {
    SectionHeader(title = "Content stats")
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
    onStartFlashcards: () -> Unit,
    onStartQuiz: () -> Unit,
    onChangeStudyPath: () -> Unit
) {
    SectionHeader(
        title = "Quick actions",
        subtitle = "Jump into the study mode you want right now."
    )
    LearnLiftCard {
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
            text = "Change Study Path",
            onClick = onChangeStudyPath
        )
    }
}

@Composable
private fun HomeBrandHeader() {
    Column {
        Box(
            modifier = Modifier
                .size(LearnLiftSpacing.homeLogoSize)
                .clip(RoundedCornerShape(LearnLiftCorners.logo))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LA",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = LearnLiftTypographySizes.homeLogoText,
                fontWeight = FontWeight.Bold
            )
        }
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
}
