package com.learnliftai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun PlaceholderPanel(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    LearnLiftCard(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .width(LearnLiftSpacing.highlightWidth)
                .height(LearnLiftSpacing.highlightHeight)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.extraLarge
                )
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
