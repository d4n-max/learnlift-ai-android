package com.learnliftai.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.learnliftai.app.BuildConfig
import com.learnliftai.app.data.ai.AiUsageAction
import com.learnliftai.app.data.ai.AiUsageState
import com.learnliftai.app.data.billing.PremiumEntitlement
import com.learnliftai.app.data.billing.PremiumUiState
import com.learnliftai.app.domain.model.PremiumPlanStatus
import com.learnliftai.app.domain.model.ReminderPreferences
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.LearnLiftLogo
import com.learnliftai.app.ui.components.PremiumTeaserCard
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun SettingsScreen(
    selectedStudyPath: StudyPath?,
    premiumUiState: PremiumUiState,
    aiUsageState: AiUsageState,
    reminderPreferences: ReminderPreferences,
    dailyStudyMinutes: Int,
    onChooseStudyPath: () -> Unit,
    onViewPremium: () -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderTimeSelected: (hour: Int, minute: Int) -> Unit,
    onResetProgress: () -> Unit,
    onRestorePurchases: () -> Unit,
    onResetOnboarding: () -> Unit,
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
        PremiumSettingsSection(
            premiumUiState = premiumUiState,
            aiUsageState = aiUsageState,
            onViewPremium = onViewPremium,
            onRestorePurchases = onRestorePurchases
        )
        DailyReminderSettingsSection(
            reminderPreferences = reminderPreferences,
            dailyStudyMinutes = dailyStudyMinutes,
            onReminderEnabledChange = onReminderEnabledChange,
            onReminderTimeSelected = onReminderTimeSelected
        )
        AppInfoSection()
        FutureFeaturesSection()
        OnboardingSettingsSection(onResetOnboarding = onResetOnboarding)
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
private fun DailyReminderSettingsSection(
    reminderPreferences: ReminderPreferences,
    dailyStudyMinutes: Int,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    val context = LocalContext.current
    var permissionDenied by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionDenied = !granted
        onReminderEnabledChange(granted)
    }

    SectionHeader(title = "Daily reminder")
    LearnLiftCard {
        Text(
            text = "Local study reminder",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "We'll remind you locally on this device. No account or cloud sync required.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Daily goal: $dailyStudyMinutes minutes",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryReminderToggle(
            reminderPreferences = reminderPreferences,
            permissionDenied = permissionDenied,
            onEnable = {
                permissionDenied = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onReminderEnabledChange(true)
                }
            },
            onDisable = { onReminderEnabledChange(false) }
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "Reminder time: ${reminderPreferences.reminderTimeLabel}",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        ReminderTimePresetButton("08:00", 8, 0, reminderPreferences, onReminderTimeSelected)
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        ReminderTimePresetButton("12:00", 12, 0, reminderPreferences, onReminderTimeSelected)
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        ReminderTimePresetButton("19:00", 19, 0, reminderPreferences, onReminderTimeSelected)
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        ReminderTimePresetButton("21:00", 21, 0, reminderPreferences, onReminderTimeSelected)
    }
}

@Composable
private fun PrimaryReminderToggle(
    reminderPreferences: ReminderPreferences,
    permissionDenied: Boolean,
    onEnable: () -> Unit,
    onDisable: () -> Unit
) {
    val status = when {
        permissionDenied -> "Notification permission is disabled."
        reminderPreferences.remindersEnabled -> "Reminder enabled for ${reminderPreferences.reminderTimeLabel}."
        else -> "Reminder is off."
    }
    Text(
        text = status,
        color = if (permissionDenied) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (permissionDenied) FontWeight.SemiBold else FontWeight.Normal
    )
    Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
    if (reminderPreferences.remindersEnabled) {
        SecondaryActionButton(
            text = "Disable daily study reminder",
            onClick = onDisable
        )
    } else {
        SecondaryActionButton(
            text = "Enable daily study reminder",
            onClick = onEnable
        )
    }
}

@Composable
private fun ReminderTimePresetButton(
    label: String,
    hour: Int,
    minute: Int,
    reminderPreferences: ReminderPreferences,
    onReminderTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    SecondaryActionButton(
        text = if (reminderPreferences.reminderHour == hour && reminderPreferences.reminderMinute == minute) {
            "$label selected"
        } else {
            label
        },
        onClick = { onReminderTimeSelected(hour, minute) }
    )
}

@Composable
private fun OnboardingSettingsSection(
    onResetOnboarding: () -> Unit
) {
    SectionHeader(title = "Onboarding")
    LearnLiftCard {
        Text(
            text = "Learning setup",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Restart onboarding to choose your goal, recommended path, and daily study time again. This does not reset progress.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        SecondaryActionButton(
            text = "Restart onboarding",
            onClick = onResetOnboarding
        )
    }
}

@Composable
private fun PremiumSettingsSection(
    premiumUiState: PremiumUiState,
    aiUsageState: AiUsageState,
    onViewPremium: () -> Unit,
    onRestorePurchases: () -> Unit
) {
    SectionHeader(title = "Premium")
    LearnLiftCard {
        Text(
            text = "Current plan",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (premiumUiState.entitlement == PremiumEntitlement.Premium) {
                "Premium"
            } else {
                PremiumPlanStatus.Free.label
            },
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (premiumUiState.entitlement == PremiumEntitlement.Premium) {
                "Premium is active. You have higher daily AI Coach limits and access to Premium-ready study tools."
            } else {
                "Free plan includes flashcards, quizzes, daily sessions, progress, Smart Coach, and limited AI previews."
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "AI access",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (premiumUiState.isPremiumActive) {
                "Premium AI access active"
            } else {
                "Free previews: ${aiUsageState.remainingFor(AiUsageAction.ExplainAnswer, isPremium = false)} explanations and ${aiUsageState.remainingFor(AiUsageAction.QuizSummary, isPremium = false)} study review left today"
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        if (premiumUiState.message != null) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = premiumUiState.message,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    PremiumTeaserCard(
        title = if (premiumUiState.entitlement == PremiumEntitlement.Premium) {
            "Premium active"
        } else {
            PremiumPlanStatus.PremiumComingSoon.label
        },
        description = if (premiumUiState.isPremiumActive) {
            "More AI help is active now. Advanced insights and premium study packs are coming soon."
        } else {
            "Upgrade for more AI help each day. Advanced insights and premium study packs are coming soon."
        },
        label = if (premiumUiState.isPremiumActive) "Active" else "Premium",
        actionText = "View Premium benefits",
        onActionClick = onViewPremium
    )
    SecondaryActionButton(
        text = if (premiumUiState.isRestoring) "Restoring purchases..." else "Restore purchases",
        onClick = onRestorePurchases,
        enabled = !premiumUiState.isRestoring
    )
}

@Composable
private fun SettingsBrandHeader() {
    Column {
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
        value = BuildConfig.VERSION_NAME,
        helperText = "Closed testing candidate"
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
        FutureFeatureRow(title = "AI Coach", status = "Available with fallback")
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        FutureFeatureRow(title = "Cloud sync", status = "Coming later")
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        FutureFeatureRow(title = "Premium study packs", status = "Available")
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
