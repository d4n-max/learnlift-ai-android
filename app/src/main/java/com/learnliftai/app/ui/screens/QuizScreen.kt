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
fun QuizScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.Center
    ) {
        PlaceholderPanel(
            title = "Quiz",
            description = "Quiz mode will offer quick practice questions and feedback after the initial content model is ready."
        )
    }
}
