package com.learnliftai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.learnliftai.app.ui.components.PlaceholderPanel
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun FlashcardsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.Center
    ) {
        PlaceholderPanel(
            title = "Flashcards",
            description = "Review cards will help learners practice key terms, prompts, and concepts once study content is added."
        )
    }
}
