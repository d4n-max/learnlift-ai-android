package com.learnliftai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun QuizScreen(
    selectedStudyPath: StudyPath?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.Center
    ) {
        EmptyState(
            title = "Quiz",
            description = selectedStudyPath?.let {
                "Quick practice questions for ${it.title} will appear here after the initial content model is ready."
            } ?: "Choose a study path from Home, then quiz mode will offer quick practice questions and feedback after the initial content model is ready."
        )
    }
}
