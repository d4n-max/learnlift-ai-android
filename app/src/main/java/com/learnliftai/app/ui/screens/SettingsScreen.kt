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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing
import com.learnliftai.app.ui.theme.LearnLiftTypographySizes

@Composable
fun SettingsScreen(
    selectedStudyPath: StudyPath?,
    onChooseStudyPath: () -> Unit,
    onResetProgress: () -> Unit,
    onBackToHome: () -> Unit,
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
        SettingsBrandHeader()
        CurrentStudyPathSettingsCard(
            selectedStudyPath = selectedStudyPath,
            onChooseStudyPath = onChooseStudyPath
        )
        AppInfoSection()
        FutureFeaturesSection()
        LearnLiftCard {
            Text(
                text = "Progress data",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Progress is stored locally on this device. Resetting clears saved stats and streaks, but keeps your selected study path.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            SecondaryActionButton(
                text = "Reset Progress Stats",
                onClick = { showResetConfirmation = true }
            )
        }
        SecondaryActionButton(
            text = "Back to Home",
            onClick = onBackToHome
        )
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
private fun SettingsBrandHeader() {
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

@Composable
private fun CurrentStudyPathSettingsCard(
    selectedStudyPath: StudyPath?,
    onChooseStudyPath: () -> Unit
) {
    SectionHeader(title = "Current study path")
    if (selectedStudyPath == null) {
        EmptyState(
            title = "No study path selected",
            description = "Choose a study path to personalize Home, Flashcards, Quiz, Daily Session, and Progress.",
            actionText = "Choose Study Path",
            onActionClick = onChooseStudyPath
        )
    } else {
        LearnLiftCard {
            Text(
                text = selectedStudyPath.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = selectedStudyPath.subtitle,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = selectedStudyPath.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AppInfoSection() {
    SectionHeader(title = "App info")
    StatCard(
        label = "Version",
        value = "0.1.0 MVP",
        helperText = "Early local learning build"
    )
    StatCard(
        label = "Current mode",
        value = "Local MVP",
        helperText = "No account required"
    )
    StatCard(
        label = "Data storage",
        value = "Local device",
        helperText = "Stored locally on this device"
    )
}

@Composable
private fun FutureFeaturesSection() {
    SectionHeader(title = "Future features")
    LearnLiftCard {
        FutureFeatureRow(title = "AI Coach", status = "Coming soon")
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        FutureFeatureRow(title = "Cloud sync", status = "Coming later")
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        FutureFeatureRow(title = "Premium study packs", status = "Future idea")
    }
}

@Composable
private fun FutureFeatureRow(title: String, status: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = status,
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
}
