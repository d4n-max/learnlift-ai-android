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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun StudyPathSelectionScreen(
    studyPaths: List<StudyPath>,
    selectedStudyPath: StudyPath?,
    isPremiumActive: Boolean,
    onStudyPathSelected: (StudyPath) -> Unit,
    onViewPremium: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lockedPath by remember { mutableStateOf<StudyPath?>(null) }
    var comingSoonPath by remember { mutableStateOf<StudyPath?>(null) }
    val freePaths = studyPaths.filter { !it.isPremium }
    val premiumPaths = studyPaths.filter { it.isPremium }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        SectionHeader(
            title = "Choose a study path",
            subtitle = "Pick the goal you want LearnLift AI to support first."
        )
        SectionHeader(
            title = "Free Learning Paths",
            subtitle = "Start here without Premium."
        )
        freePaths.forEach { studyPath ->
            StudyPathOptionCard(
                studyPath = studyPath,
                isSelected = selectedStudyPath?.id == studyPath.id,
                isPremiumActive = isPremiumActive,
                onSelect = { onStudyPathSelected(studyPath) }
            )
        }
        SectionHeader(
            title = "Premium Study Packs",
            subtitle = "Preview a few items for free, or unlock the full pack with Premium."
        )
        premiumPaths.forEach { studyPath ->
            StudyPathOptionCard(
                studyPath = studyPath,
                isSelected = selectedStudyPath?.id == studyPath.id,
                isPremiumActive = isPremiumActive,
                onSelect = {
                    when {
                        studyPath.isComingSoon -> comingSoonPath = studyPath
                        isPremiumActive -> onStudyPathSelected(studyPath)
                        else -> lockedPath = studyPath
                    }
                }
            )
        }
        SecondaryActionButton(
            text = "Back to Home",
            onClick = onBackToHome
        )
    }

    lockedPath?.let { path ->
        PremiumPackLockDialog(
            studyPath = path,
            onPreview = {
                lockedPath = null
                onStudyPathSelected(path)
            },
            onViewPremium = {
                lockedPath = null
                onViewPremium()
            },
            onCancel = { lockedPath = null }
        )
    }

    comingSoonPath?.let { path ->
        ComingSoonPackDialog(
            studyPath = path,
            onViewPremium = {
                comingSoonPath = null
                onViewPremium()
            },
            onDismiss = { comingSoonPath = null }
        )
    }
}

@Composable
private fun StudyPathOptionCard(
    studyPath: StudyPath,
    isSelected: Boolean,
    isPremiumActive: Boolean,
    onSelect: () -> Unit
) {
    val contentSummary = studyPath.contentSummary
    val isLockedPremium = studyPath.isPremium && !isPremiumActive && !studyPath.isComingSoon
    val border = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
    } else {
        BorderStroke(
            1.dp,
            if (studyPath.isPremium) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.28f)
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(LearnLiftCorners.card),
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = if (studyPath.isPremium) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(LearnLiftSpacing.screenContent)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StudyPathPill(text = studyPath.icon)
                if (isSelected) {
                    Text(
                        text = "Selected",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Text(
                text = studyPath.title,
                color = if (studyPath.isPremium) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Column(
                verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
            ) {
                if (studyPath.isPremium) {
                    StudyPathMetaLabel(text = "Premium")
                }
                if (studyPath.isComingSoon) {
                    StudyPathMetaLabel(text = "Coming soon")
                } else if (isLockedPremium && studyPath.freePreviewCount > 0) {
                    StudyPathMetaLabel(text = "Preview available")
                }
                StudyPathMetaLabel(text = studyPath.accentLabel)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = studyPath.subtitle,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.84f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = studyPath.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )
            if (contentSummary != null) {
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
                Text(
                    text = contentSummary.countLabel,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Row(
                horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
            ) {
                StudyPathMetaLabel(text = studyPath.difficultyLabel)
                StudyPathMetaLabel(text = studyPath.estimatedDailyTime)
            }
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Recommended for: ${studyPath.recommendedFor}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            PrimaryActionButton(
                text = when {
                    studyPath.isComingSoon -> "Coming soon"
                    isSelected -> "Keep this path"
                    isLockedPremium -> "Preview or unlock"
                    else -> "Select this path"
                },
                onClick = onSelect,
                enabled = true
            )
        }
    }
}

@Composable
private fun PremiumPackLockDialog(
    studyPath: StudyPath,
    onPreview: () -> Unit,
    onViewPremium: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Unlock Premium Study Packs",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)) {
                Text(
                    text = "This pack is part of LearnLift AI Premium. Preview a few cards for free, or unlock the full pack with Premium.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = studyPath.title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                studyPath.contentSummary?.let { summary ->
                    Text(
                        text = summary.countLabel,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Topics: ${summary.topicExamples}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onPreview) {
                Text(text = "Preview pack")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onViewPremium) {
                    Text(text = "View Premium")
                }
                TextButton(onClick = onCancel) {
                    Text(text = "Cancel")
                }
            }
        }
    )
}

@Composable
private fun ComingSoonPackDialog(
    studyPath: StudyPath,
    onViewPremium: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = studyPath.title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "This Premium Study Pack is coming soon.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onViewPremium) {
                Text(text = "View Premium")
            }
        }
    )
}

@Composable
private fun StudyPathPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                shape = RoundedCornerShape(LearnLiftCorners.highlight)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun StudyPathMetaLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(LearnLiftCorners.highlight)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold
    )
}

private data class StudyPathContentSummary(
    val flashcards: Int,
    val quizQuestions: Int,
    val topicExamples: String
) {
    val countLabel: String = "$flashcards cards • $quizQuestions questions"
}

private val StudyPath.contentSummary: StudyPathContentSummary?
    get() = when (id) {
        "english-vocabulary-speaking" -> StudyPathContentSummary(
            flashcards = 84,
            quizQuestions = 63,
            topicExamples = "workplace English, speaking confidence, interview phrases"
        )
        "job-interview-prep" -> StudyPathContentSummary(
            flashcards = 80,
            quizQuestions = 60,
            topicExamples = "STAR answers, behavioral questions, salary conversations"
        )
        "it-qa-interview-prep" -> StudyPathContentSummary(
            flashcards = 60,
            quizQuestions = 50,
            topicExamples = "test cases, bug reports, API basics"
        )
        "sql-interview-prep" -> StudyPathContentSummary(
            flashcards = 30,
            quizQuestions = 25,
            topicExamples = "joins, grouping, NULLs, indexes, CTEs"
        )
        "qa-advanced" -> StudyPathContentSummary(
            flashcards = 30,
            quizQuestions = 25,
            topicExamples = "risk, coverage, triage, release readiness"
        )
        "automation-testing-basics" -> StudyPathContentSummary(
            flashcards = 30,
            quizQuestions = 25,
            topicExamples = "pyramid, UI/API checks, selectors, flaky tests"
        )
        else -> null
    }
