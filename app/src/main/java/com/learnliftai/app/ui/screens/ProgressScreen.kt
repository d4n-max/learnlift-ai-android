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
fun ProgressScreen(
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
            title = "Progress",
            description = selectedStudyPath?.let {
                "Progress for ${it.title} will show streaks, completion, and learning momentum after local progress storage is introduced."
            } ?: "Choose a study path from Home, then progress tracking will show streaks, completion, and learning momentum after local progress storage is introduced."
        )
    }
}
