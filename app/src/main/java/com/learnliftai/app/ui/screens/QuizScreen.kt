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
fun QuizScreen(
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
            title = "Quiz",
            description = if (selectedStudyPath != null && selectedStudyContent != null) {
                "${selectedStudyPath.title} has ${selectedStudyContent.quizQuestions.size} quiz questions available. Full quiz mode will be added next."
            } else {
                "Choose a study path from Home, then quiz mode will offer quick practice questions and feedback after the initial content model is ready."
            }
        )
    }
}
