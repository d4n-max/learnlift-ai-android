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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.learnliftai.app.ui.components.PlaceholderPanel
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing
import com.learnliftai.app.ui.theme.LearnLiftTypographySizes

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.Center
    ) {
        HomeBrandHeader()
        Spacer(modifier = Modifier.height(LearnLiftSpacing.sectionGap))
        PlaceholderPanel(
            title = "Welcome to your study coach",
            description = "Soon you will be able to choose study paths, practice flashcards, take quizzes, and track your progress in one focused place."
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
