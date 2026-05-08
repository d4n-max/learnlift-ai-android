package com.learnliftai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.learnliftai.app.domain.model.StudyContent
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun FlashcardsScreen(
    selectedStudyPath: StudyPath?,
    selectedStudyContent: StudyContent?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.Center
    ) {
        EmptyState(
            title = "Flashcards",
            description = if (selectedStudyPath != null && selectedStudyContent != null) {
                "${selectedStudyPath.title} has ${selectedStudyContent.flashcards.size} flashcards available. Full flashcard review will be added next."
            } else {
                "Choose a study path from Home, then review cards will help learners practice key terms, prompts, and concepts once study content is added."
            }
        )
    }
}
