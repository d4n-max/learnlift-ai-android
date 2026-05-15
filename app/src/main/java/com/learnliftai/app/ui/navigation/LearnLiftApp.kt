package com.learnliftai.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.learnliftai.app.data.billing.PremiumRepository
import com.learnliftai.app.data.StudyPathRepository
import com.learnliftai.app.domain.model.UserProgress
import com.learnliftai.app.ui.screens.DailyStudySessionScreen
import com.learnliftai.app.ui.screens.FlashcardsScreen
import com.learnliftai.app.ui.screens.HomeScreen
import com.learnliftai.app.ui.screens.PremiumScreen
import com.learnliftai.app.ui.screens.ProgressScreen
import com.learnliftai.app.ui.screens.QuizScreen
import com.learnliftai.app.ui.screens.SettingsScreen
import com.learnliftai.app.ui.screens.StudyPathSelectionScreen
import kotlinx.coroutines.launch

@Composable
fun LearnLiftApp() {
    val context = LocalContext.current
    val progressRepository = remember { LocalProgressRepository(context.applicationContext) }
    val premiumRepository = remember { PremiumRepository(context.applicationContext) }
    val userProgress by progressRepository.progress.collectAsState(initial = UserProgress())
    val premiumUiState by premiumRepository.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedDestinationName by rememberSaveable {
        mutableStateOf(LearnLiftDestination.Home.name)
    }
    var isChoosingStudyPath by rememberSaveable {
        mutableStateOf(false)
    }
    var isDailySessionActive by rememberSaveable {
        mutableStateOf(false)
    }
    var isSettingsOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isPremiumOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val selectedDestination = LearnLiftDestination.valueOf(selectedDestinationName)
    val studyPaths = StudyPathRepository.studyPaths
    val selectedStudyPath = StudyPathRepository.findById(userProgress.selectedStudyPathId)
    val selectedStudyContent = remember(userProgress.selectedStudyPathId) {
        userProgress.selectedStudyPathId?.let { AssetStudyContentRepository.loadStudyContent(context, it) }
    }
    val isSubFlowOpen = isSettingsOpen || isPremiumOpen || isDailySessionActive || isChoosingStudyPath

    LaunchedEffect(Unit) {
        premiumRepository.refreshPremiumState()
    }

    BackHandler(enabled = isSubFlowOpen) {
        isSettingsOpen = false
        isPremiumOpen = false
        isDailySessionActive = false
        isChoosingStudyPath = false
        selectedDestinationName = LearnLiftDestination.Home.name
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            LearnLiftBottomNavigation(
                selectedDestination = selectedDestination,
                onDestinationSelected = {
                    isChoosingStudyPath = false
                    isDailySessionActive = false
                    isSettingsOpen = false
                    isPremiumOpen = false
                    selectedDestinationName = it.name
                }
            )
        }
    ) { innerPadding ->
        if (isPremiumOpen) {
            PremiumScreen(
                premiumUiState = premiumUiState,
                onRefreshPremium = {
                    coroutineScope.launch {
                        premiumRepository.refreshPremiumState()
                    }
                },
                onPurchasePackage = { premiumPackage, activity ->
                    coroutineScope.launch {
                        premiumRepository.purchase(activity, premiumPackage)
                    }
                },
                onRestorePurchases = {
                    coroutineScope.launch {
                        premiumRepository.restorePurchases()
                    }
                },
                onBack = {
                    isPremiumOpen = false
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (isSettingsOpen) {
            SettingsScreen(
                selectedStudyPath = selectedStudyPath,
                premiumUiState = premiumUiState,
                onChooseStudyPath = {
                    isSettingsOpen = false
                    isChoosingStudyPath = true
                },
                onViewPremium = {
                    isSettingsOpen = false
                    isPremiumOpen = true
                },
                onResetProgress = {
                    coroutineScope.launch {
                        progressRepository.resetProgressStats()
                    }
                },
                onRestorePurchases = {
                    coroutineScope.launch {
                        premiumRepository.restorePurchases()
                    }
                },
                onBackToHome = {
                    isSettingsOpen = false
                    isPremiumOpen = false
                    isChoosingStudyPath = false
                    isDailySessionActive = false
                    selectedDestinationName = LearnLiftDestination.Home.name
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (isDailySessionActive) {
            DailyStudySessionScreen(
                selectedStudyPath = selectedStudyPath,
                selectedStudyContent = selectedStudyContent,
                onFinishSession = { reviewedCards, knownCards, needsReviewCards, quizAnswered, quizCorrect, quizPercentage ->
                    coroutineScope.launch {
                        progressRepository.recordDailySessionCompleted(
                            reviewedCards = reviewedCards,
                            knownCards = knownCards,
                            needsReviewCards = needsReviewCards,
                            quizAnswered = quizAnswered,
                            quizCorrect = quizCorrect,
                            quizPercentage = quizPercentage
                        )
                    }
                },
                onReturnHome = {
                    isDailySessionActive = false
                    isChoosingStudyPath = false
                    isPremiumOpen = false
                    selectedDestinationName = LearnLiftDestination.Home.name
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (isChoosingStudyPath) {
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
                    isPremiumOpen = false
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
                    isPremiumActive = premiumUiState.isPremiumActive,
                    onChooseStudyPath = { isChoosingStudyPath = true },
                    onOpenSettings = { isSettingsOpen = true },
                    onViewPremium = { isPremiumOpen = true },
                    onStartDailySession = { isDailySessionActive = true },
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
                    isPremiumActive = premiumUiState.isPremiumActive,
                    onOpenSettings = { isSettingsOpen = true },
                    onViewPremium = { isPremiumOpen = true },
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
