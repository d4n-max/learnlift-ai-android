package com.learnliftai.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.learnliftai.app.data.AssetStudyContentRepository
import com.learnliftai.app.data.LocalProgressRepository
import com.learnliftai.app.data.StudyPathRepository
import com.learnliftai.app.domain.model.UserProgress
import com.learnliftai.app.ui.screens.FlashcardsScreen
import com.learnliftai.app.ui.screens.HomeScreen
import com.learnliftai.app.ui.screens.ProgressScreen
import com.learnliftai.app.ui.screens.QuizScreen
import com.learnliftai.app.ui.screens.StudyPathSelectionScreen
import kotlinx.coroutines.launch

@Composable
fun LearnLiftApp() {
    val context = LocalContext.current
    val progressRepository = remember { LocalProgressRepository(context.applicationContext) }
    val userProgress by progressRepository.progress.collectAsState(initial = UserProgress())
    val coroutineScope = rememberCoroutineScope()
    var selectedDestinationName by rememberSaveable {
        mutableStateOf(LearnLiftDestination.Home.name)
    }
    var isChoosingStudyPath by rememberSaveable {
        mutableStateOf(false)
    }
    val selectedDestination = LearnLiftDestination.valueOf(selectedDestinationName)
    val studyPaths = StudyPathRepository.studyPaths
    val selectedStudyPath = StudyPathRepository.findById(userProgress.selectedStudyPathId)
    val selectedStudyContent = remember(userProgress.selectedStudyPathId) {
        userProgress.selectedStudyPathId?.let { AssetStudyContentRepository.loadStudyContent(context, it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            LearnLiftBottomNavigation(
                selectedDestination = selectedDestination,
                onDestinationSelected = {
                    isChoosingStudyPath = false
                    selectedDestinationName = it.name
                }
            )
        }
    ) { innerPadding ->
        if (isChoosingStudyPath) {
            StudyPathSelectionScreen(
                studyPaths = studyPaths,
                selectedStudyPath = selectedStudyPath,
                onStudyPathSelected = {
                    coroutineScope.launch {
                        progressRepository.setSelectedStudyPathId(it.id)
                    }
                    isChoosingStudyPath = false
                    selectedDestinationName = LearnLiftDestination.Home.name
                },
                onBackToHome = {
                    isChoosingStudyPath = false
                    selectedDestinationName = LearnLiftDestination.Home.name
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            when (selectedDestination) {
                LearnLiftDestination.Home -> HomeScreen(
                    selectedStudyPath = selectedStudyPath,
                    selectedStudyContent = selectedStudyContent,
                    userProgress = userProgress,
                    onChooseStudyPath = { isChoosingStudyPath = true },
                    onStartFlashcards = {
                        isChoosingStudyPath = false
                        selectedDestinationName = LearnLiftDestination.Flashcards.name
                    },
                    onStartQuiz = {
                        isChoosingStudyPath = false
                        selectedDestinationName = LearnLiftDestination.Quiz.name
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                LearnLiftDestination.Flashcards -> FlashcardsScreen(
                    selectedStudyPath = selectedStudyPath,
                    selectedStudyContent = selectedStudyContent,
                    onFlashcardReviewed = { reviewedDelta, knownDelta, needsReviewDelta ->
                        coroutineScope.launch {
                            progressRepository.recordFlashcardReview(
                                reviewedDelta = reviewedDelta,
                                knownDelta = knownDelta,
                                needsReviewDelta = needsReviewDelta
                            )
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                LearnLiftDestination.Quiz -> QuizScreen(
                    selectedStudyPath = selectedStudyPath,
                    selectedStudyContent = selectedStudyContent,
                    onQuizCompleted = { score, percentage ->
                        coroutineScope.launch {
                            progressRepository.recordQuizCompleted(
                                score = score,
                                percentage = percentage
                            )
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                LearnLiftDestination.Progress -> ProgressScreen(
                    selectedStudyPath = selectedStudyPath,
                    userProgress = userProgress,
                    onResetProgress = {
                        coroutineScope.launch {
                            progressRepository.resetProgressStats()
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun LearnLiftBottomNavigation(
    selectedDestination: LearnLiftDestination,
    onDestinationSelected: (LearnLiftDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LearnLiftDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Text(
                        text = destination.marker,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
                )
            )
        }
    }
}
