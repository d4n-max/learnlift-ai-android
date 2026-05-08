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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onStudyPathSelected: (StudyPath) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        studyPaths.forEach { studyPath ->
            StudyPathOptionCard(
                studyPath = studyPath,
                isSelected = selectedStudyPath?.id == studyPath.id,
                onSelect = { onStudyPathSelected(studyPath) }
            )
        }
        SecondaryActionButton(
            text = "Back to Home",
            onClick = onBackToHome
        )
    }
}

@Composable
private fun StudyPathOptionCard(
    studyPath: StudyPath,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val border = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
    } else {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(LearnLiftCorners.card),
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                StudyPathPill(text = studyPath.accentLabel)
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
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
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
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Row(
                horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
            ) {
                StudyPathMetaLabel(text = studyPath.difficultyLabel)
                StudyPathMetaLabel(text = studyPath.estimatedDailyTime)
            }
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            PrimaryActionButton(
                text = if (isSelected) "Keep this path" else "Select this path",
                onClick = onSelect
            )
        }
    }
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
