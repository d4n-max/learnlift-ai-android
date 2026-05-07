package com.learnliftai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun PlaceholderPanel(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(LearnLiftCorners.card),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(LearnLiftSpacing.screenContent)
        ) {
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
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
