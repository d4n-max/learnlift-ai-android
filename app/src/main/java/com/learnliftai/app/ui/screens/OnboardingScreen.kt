package com.learnliftai.app.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.domain.model.OnboardingGoal
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.domain.model.recommendedPathForGoal
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.LearnLiftLogo
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun OnboardingScreen(
    studyPaths: List<StudyPath>,
    existingSelectedPathId: String?,
    onCompleteOnboarding: (goal: OnboardingGoal, pathId: String, dailyMinutes: Int) -> Unit,
    onSkipOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedGoal by remember { mutableStateOf(OnboardingGoal.PrepareForJobInterviews) }
    var selectedDailyMinutes by remember { mutableIntStateOf(10) }
    var manualPathId by remember { mutableStateOf<String?>(null) }
    var isChoosingPath by remember { mutableStateOf(false) }
    val recommendedPathId = manualPathId ?: recommendedPathForGoal(selectedGoal, existingSelectedPathId)
    val recommendedPath = studyPaths.firstOrNull { it.id == recommendedPathId } ?: studyPaths.firstOrNull()

    BackHandler(enabled = step > 0 || isChoosingPath) {
        if (isChoosingPath) {
            isChoosingPath = false
        } else {
            step = (step - 1).coerceAtLeast(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        when (step) {
            0 -> OnboardingWelcomeStep(
                onGetStarted = { step = 1 },
                onSkip = onSkipOnboarding
            )

            1 -> OnboardingGoalStep(
                selectedGoal = selectedGoal,
                onGoalSelected = {
                    selectedGoal = it
                    manualPathId = null
                },
                onContinue = { step = 2 },
                onSkip = onSkipOnboarding
            )

            2 -> OnboardingDailyTimeStep(
                selectedMinutes = selectedDailyMinutes,
                onMinutesSelected = { selectedDailyMinutes = it },
                onContinue = { step = 3 },
                onSkip = onSkipOnboarding
            )

            else -> OnboardingRecommendedPathStep(
                recommendedPath = recommendedPath,
                studyPaths = studyPaths,
                selectedDailyMinutes = selectedDailyMinutes,
                isChoosingPath = isChoosingPath,
                selectedPathId = recommendedPath?.id,
                onChoosePath = { isChoosingPath = true },
                onPathSelected = {
                    manualPathId = it.id
                    isChoosingPath = false
                },
                onStartLearning = {
                    val pathId = recommendedPath?.id ?: "job-interview-prep"
                    onCompleteOnboarding(selectedGoal, pathId, selectedDailyMinutes)
                },
                onSkip = onSkipOnboarding
            )
        }
    }
}

@Composable
private fun OnboardingWelcomeStep(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit
) {
    LearnLiftLogo()
    SectionHeader(
        title = "LearnLift AI",
        subtitle = "Elevate Your Skills, Effortlessly."
    )
    LearnLiftCard(
        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f),
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f)
    ) {
        Text(
            text = "Build better answers, stronger study habits, and smarter practice sessions.",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Set up your goal in under a minute. Everything stays local on this device.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryActionButton(
            text = "Get started",
            onClick = onGetStarted
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        SecondaryActionButton(
            text = "Skip for now",
            onClick = onSkip
        )
    }
}

@Composable
private fun OnboardingGoalStep(
    selectedGoal: OnboardingGoal,
    onGoalSelected: (OnboardingGoal) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    SectionHeader(
        title = "Choose your goal",
        subtitle = "What are you preparing for?"
    )
    OnboardingGoal.entries.forEach { goal ->
        SelectableOnboardingCard(
            title = goal.label,
            subtitle = goalSubtitle(goal),
            isSelected = selectedGoal == goal,
            onClick = { onGoalSelected(goal) }
        )
    }
    PrimaryActionButton(
        text = "Continue",
        onClick = onContinue
    )
    SecondaryActionButton(
        text = "Skip for now",
        onClick = onSkip
    )
}

@Composable
private fun OnboardingDailyTimeStep(
    selectedMinutes: Int,
    onMinutesSelected: (Int) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    SectionHeader(
        title = "Choose daily time",
        subtitle = "How much time do you want to study per day?"
    )
    listOf(5, 10, 15, 20).forEach { minutes ->
        SelectableOnboardingCard(
            title = "$minutes minutes",
            subtitle = if (minutes == 10) "Recommended default" else "A focused daily rhythm",
            isSelected = selectedMinutes == minutes,
            onClick = { onMinutesSelected(minutes) }
        )
    }
    PrimaryActionButton(
        text = "Continue",
        onClick = onContinue
    )
    SecondaryActionButton(
        text = "Skip for now",
        onClick = onSkip
    )
}

@Composable
private fun OnboardingRecommendedPathStep(
    recommendedPath: StudyPath?,
    studyPaths: List<StudyPath>,
    selectedDailyMinutes: Int,
    isChoosingPath: Boolean,
    selectedPathId: String?,
    onChoosePath: () -> Unit,
    onPathSelected: (StudyPath) -> Unit,
    onStartLearning: () -> Unit,
    onSkip: () -> Unit
) {
    SectionHeader(
        title = if (isChoosingPath) "Change path" else "Recommended path",
        subtitle = if (isChoosingPath) {
            "Pick the path you want to start with."
        } else {
            "Recommended for you"
        }
    )

    if (isChoosingPath) {
        studyPaths.forEach { path ->
            OnboardingStudyPathCard(
                studyPath = path,
                isSelected = selectedPathId == path.id,
                onClick = { onPathSelected(path) }
            )
        }
    } else if (recommendedPath != null) {
        OnboardingStudyPathCard(
            studyPath = recommendedPath,
            isSelected = true,
            onClick = onChoosePath
        )
        LearnLiftCard {
            Text(
                text = "Recommended daily goal",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Start with a $selectedDailyMinutes-minute session. You can change your path later from Settings.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    PrimaryActionButton(
        text = "Start learning",
        onClick = onStartLearning
    )
    SecondaryActionButton(
        text = "Change path",
        onClick = onChoosePath
    )
    SecondaryActionButton(
        text = "Skip for now",
        onClick = onSkip
    )
}

@Composable
private fun SelectableOnboardingCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(LearnLiftCorners.card),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.46f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LearnLiftSpacing.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .height(38.dp)
                    .weight(0.05f)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(LearnLiftCorners.highlight)
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.74f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun OnboardingStudyPathCard(
    studyPath: StudyPath,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    SelectableOnboardingCard(
        title = studyPath.title,
        subtitle = "${studyPath.subtitle} - ${studyPath.estimatedDailyTime}",
        isSelected = isSelected,
        onClick = onClick
    )
}

private fun goalSubtitle(goal: OnboardingGoal): String {
    return when (goal) {
        OnboardingGoal.ImproveEnglishForWork -> "Practice clearer vocabulary and everyday speaking."
        OnboardingGoal.PrepareForJobInterviews -> "Build stronger, more confident interview answers."
        OnboardingGoal.PrepareForItQaInterviews -> "Review QA concepts and technical interview basics."
        OnboardingGoal.BuildDailyLearningHabit -> "Start with a simple daily rhythm and grow from there."
    }
}
