package com.learnliftai.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.learnliftai.app.R
import com.learnliftai.app.domain.model.SmartCoachRecommendation
import com.learnliftai.app.domain.model.SmartCoachRecommendationType
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftElevation
import com.learnliftai.app.ui.theme.LearnLiftGradients
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun LearnLiftLogo(
    modifier: Modifier = Modifier,
    size: Dp = LearnLiftSpacing.homeLogoSize
) {
    Image(
        painter = painterResource(id = R.drawable.learnlift_ai_app_icon),
        contentDescription = "LearnLift AI logo",
        modifier = modifier
            .width(size)
            .height(size)
            .clip(RoundedCornerShape(LearnLiftCorners.logo)),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun LearnLiftCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(LearnLiftCorners.card),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = LearnLiftElevation.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor)
                .padding(LearnLiftSpacing.cardPadding),
            content = content
        )
    }
}

@Composable
fun GradientHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(LearnLiftCorners.hero),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = LearnLiftElevation.hero)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LearnLiftGradients.hero())
                .padding(LearnLiftSpacing.screenContent)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingContent: (@Composable RowScope.() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(LearnLiftCorners.button),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(modifier = Modifier.width(LearnLiftSpacing.smallGap))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(LearnLiftCorners.button),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
        ) {
            Spacer(
                modifier = Modifier
                    .width(6.dp)
                    .height(28.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(LearnLiftCorners.highlight)
                    )
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (action != null) {
                action()
            }
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    helperText: String? = null,
    accentColor: Color = MaterialTheme.colorScheme.secondary
) {
    LearnLiftCard(modifier = modifier) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        if (helperText != null) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = helperText,
                color = accentColor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PremiumTeaserCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    label: String = "Coming soon",
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    LearnLiftCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f)
    ) {
        TopicChip(
            text = label,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            SecondaryActionButton(
                text = actionText,
                onClick = onActionClick
            )
        }
    }
}

@Composable
fun TopicChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    LearnLiftChip(
        text = text,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Composable
fun DifficultyChip(
    text: String,
    modifier: Modifier = Modifier
) {
    val normalized = text.lowercase()
    val containerColor = when {
        "easy" in normalized || "beginner" in normalized -> MaterialTheme.colorScheme.tertiaryContainer
        "medium" in normalized || "intermediate" in normalized -> MaterialTheme.colorScheme.secondaryContainer
        "hard" in normalized || "advanced" in normalized -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        "easy" in normalized || "beginner" in normalized -> MaterialTheme.colorScheme.onTertiaryContainer
        "medium" in normalized || "intermediate" in normalized -> MaterialTheme.colorScheme.onSecondaryContainer
        "hard" in normalized || "advanced" in normalized -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    LearnLiftChip(
        text = text,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Composable
fun ProgressPill(
    label: String,
    progress: Float,
    modifier: Modifier = Modifier,
    valueText: String? = null
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    LearnLiftCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (valueText != null) {
                Text(
                    text = valueText,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(LearnLiftSpacing.mediumGap))
        LinearProgressIndicator(
            progress = { safeProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(LearnLiftCorners.chip)),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    LearnLiftCard(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .width(LearnLiftSpacing.highlightWidth)
                .height(LearnLiftSpacing.highlightHeight)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(LearnLiftCorners.highlight)
                )
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            SecondaryActionButton(
                text = actionText,
                onClick = onActionClick
            )
        }
    }
}

@Composable
fun SmartCoachRecommendationCard(
    recommendation: SmartCoachRecommendation,
    modifier: Modifier = Modifier,
    localGuidanceLabel: String? = "Local rule-based guidance"
) {
    val accentColor = when (recommendation.type) {
        SmartCoachRecommendationType.Encouragement -> MaterialTheme.colorScheme.primary
        SmartCoachRecommendationType.Review -> MaterialTheme.colorScheme.secondary
        SmartCoachRecommendationType.Warning -> MaterialTheme.colorScheme.error
        SmartCoachRecommendationType.NextStep -> MaterialTheme.colorScheme.secondary
    }

    LearnLiftCard(
        modifier = modifier,
        borderColor = accentColor.copy(alpha = 0.28f),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .width(6.dp)
                    .height(36.dp)
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(LearnLiftCorners.highlight)
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (recommendation.actionLabel != null) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = recommendation.actionLabel,
                        color = accentColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = recommendation.message,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodyMedium
        )
        if (recommendation.focusTopics.isNotEmpty()) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Text(
                text = "Topics to review",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = recommendation.focusTopics.joinToString(separator = ", "),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (localGuidanceLabel != null) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Text(
                text = localGuidanceLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun LearnLiftChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color
) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(LearnLiftCorners.chip))
            .background(containerColor)
            .border(
                width = 1.dp,
                color = contentColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(LearnLiftCorners.chip)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp),
        color = contentColor,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold
    )
}
