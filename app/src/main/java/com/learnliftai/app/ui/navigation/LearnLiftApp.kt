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
import com.learnliftai.app.data.LocalFlashcardReviewRepository
import com.learnliftai.app.data.LocalOnboardingRepository
import com.learnliftai.app.data.LocalProgressRepository
import com.learnliftai.app.data.LocalReminderPreferencesRepository
import com.learnliftai.app.data.LocalTopicPerformanceRepository
import com.learnliftai.app.data.ai.AiUsageRepository
import com.learnliftai.app.data.ai.AiUsageState
import com.learnliftai.app.data.billing.PremiumRepository
import com.learnliftai.app.data.StudyPathRepository
import com.learnliftai.app.domain.QuizMode
import com.learnliftai.app.domain.model.FlashcardMode
import com.learnliftai.app.domain.model.OnboardingGoal
import com.learnliftai.app.domain.model.OnboardingPreferences
import com.learnliftai.app.domain.model.ReminderPreferences
import com.learnliftai.app.domain.model.UserProgress
import com.learnliftai.app.domain.model.flashcardReviewSummaryFor
import com.learnliftai.app.notifications.DailyReminderScheduler
import com.learnliftai.app.ui.screens.DailyStudySessionScreen
import com.learnliftai.app.ui.screens.FlashcardsScreen
import com.learnliftai.app.ui.screens.HomeScreen
import com.learnliftai.app.ui.screens.OnboardingScreen
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
    val onboardingRepository = remember { LocalOnboardingRepository(context.applicationContext) }
    val reminderPreferencesRepository = remember { LocalReminderPreferencesRepository(context.applicationContext) }
    val reminderScheduler = remember { DailyReminderScheduler(context.applicationContext) }
    val topicPerformanceRepository = remember { LocalTopicPerformanceRepository(context.applicationContext) }
    val flashcardReviewRepository = remember { LocalFlashcardReviewRepository(context.applicationContext) }
    val premiumRepository = remember { PremiumRepository(context.applicationContext) }
    val aiUsageRepository = remember { AiUsageRepository(context.applicationContext) }
    val userProgress by progressRepository.progress.collectAsState(initial = UserProgress())
    val onboardingPreferences by onboardingRepository.preferences.collectAsState(initial = OnboardingPreferences())
    val reminderPreferences by reminderPreferencesRepository.preferences.collectAsState(initial = ReminderPreferences())
    val topicPerformance by topicPerformanceRepository.topicPerformance.collectAsState(initial = emptyList())
    val flashcardReviewStates by flashcardReviewRepository.reviewStates.collectAsState(initial = emptyList())
    val premiumUiState by premiumRepository.uiState.collectAsState()
    val aiUsageState by aiUsageRepository.usage.collectAsState(initial = AiUsageState())
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
    var quizModeName by rememberSaveable {
        mutableStateOf(QuizMode.Normal.name)
    }
    var flashcardModeName by rememberSaveable {
        mutableStateOf(FlashcardMode.All.name)
    }
    val selectedDestination = LearnLiftDestination.valueOf(selectedDestinationName)
    val quizMode = QuizMode.valueOf(quizModeName)
    val flashcardMode = FlashcardMode.valueOf(flashcardModeName)
    val studyPaths = StudyPathRepository.studyPaths
    val selectedStudyPath = StudyPathRepository.findById(userProgress.selectedStudyPathId)
    val selectedStudyContent = remember(userProgress.selectedStudyPathId) {
        userProgress.selectedStudyPathId?.let { AssetStudyContentRepository.loadStudyContent(context, it) }
    }
    val selectedPathFlashcards = selectedStudyContent?.flashcards.orEmpty()
    val selectedFlashcardReviewSummary = flashcardReviewSummaryFor(
        pathId = selectedStudyPath?.id,
        flashcards = selectedPathFlashcards,
        reviewStates = flashcardReviewStates
    )
    val shouldShowOnboarding = !onboardingPreferences.hasCompletedOnboarding
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
            if (!shouldShowOnboarding) {
                LearnLiftBottomNavigation(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = {
                        isChoosingStudyPath = false
                        isDailySessionActive = false
                        isSettingsOpen = false
                        isPremiumOpen = false
                        if (it == LearnLiftDestination.Quiz) {
                            quizModeName = QuizMode.Normal.name
                        }
                        if (it == LearnLiftDestination.Flashcards) {
                            flashcardModeName = FlashcardMode.All.name
                        }
                        selectedDestinationName = it.name
                    }
                )
            }
        }
    ) { innerPadding ->
        if (shouldShowOnboarding) {
            OnboardingScreen(
                studyPaths = studyPaths,
                existingSelectedPathId = userProgress.selectedStudyPathId,
                onCompleteOnboarding = { goal, pathId, dailyMinutes ->
                    coroutineScope.launch {
                        onboardingRepository.completeOnboarding(
                            onboardingGoal = goal.label,
                            recommendedStudyPathId = pathId,
                            dailyStudyMinutes = dailyMinutes
                        )
                        progressRepository.setSelectedStudyPathId(pathId)
                    }
                    isSettingsOpen = false
                    isChoosingStudyPath = false
                    isDailySessionActive = false
                    isPremiumOpen = false
                    selectedDestinationName = LearnLiftDestination.Home.name
                },
                onSkipOnboarding = {
                    coroutineScope.launch {
                        onboardingRepository.completeOnboarding(
                            onboardingGoal = OnboardingGoal.PrepareForJobInterviews.label,
                            recommendedStudyPathId = DefaultOnboardingPathId,
                            dailyStudyMinutes = 10
                        )
                        progressRepository.setSelectedStudyPathId(DefaultOnboardingPathId)
                    }
                    isSettingsOpen = false
                    isChoosingStudyPath = false
                    isDailySessionActive = false
                    isPremiumOpen = false
                    selectedDestinationName = LearnLiftDestination.Home.name
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (isPremiumOpen) {
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
                aiUsageState = aiUsageState,
                reminderPreferences = reminderPreferences,
                dailyStudyMinutes = onboardingPreferences.dailyStudyMinutes,
                onChooseStudyPath = {
                    isSettingsOpen = false
                    isChoosingStudyPath = true
                },
                onViewPremium = {
                    isSettingsOpen = false
                    isPremiumOpen = true
                },
                onReminderEnabledChange = { enabled ->
                    coroutineScope.launch {
                        reminderPreferencesRepository.setRemindersEnabled(enabled)
                        if (enabled) {
                            reminderScheduler.scheduleDailyReminder(reminderPreferences.copy(remindersEnabled = true))
                            reminderPreferencesRepository.markReminderScheduled()
                        } else {
                            reminderScheduler.cancelDailyReminder()
                        }
                    }
                },
                onReminderTimeSelected = { hour, minute ->
                    coroutineScope.launch {
                        reminderPreferencesRepository.setReminderTime(hour, minute)
                        val updatedPreferences = reminderPreferences.copy(
                            reminderHour = hour,
                            reminderMinute = minute
                        )
                        if (updatedPreferences.remindersEnabled) {
                            reminderScheduler.scheduleDailyReminder(updatedPreferences)
                            reminderPreferencesRepository.markReminderScheduled()
                        }
                    }
                },
                onResetProgress = {
                    coroutineScope.launch {
                        progressRepository.resetProgressStats()
                        topicPerformanceRepository.resetTopicPerformance()
                        flashcardReviewRepository.resetReviewState()
                    }
                },
                onRestorePurchases = {
                    coroutineScope.launch {
                        premiumRepository.restorePurchases()
                    }
                },
                onResetOnboarding = {
                    coroutineScope.launch {
                        onboardingRepository.resetOnboarding()
                    }
                    isSettingsOpen = false
                    isPremiumOpen = false
                    isChoosingStudyPath = false
                    isDailySessionActive = false
                    selectedDestinationName = LearnLiftDestination.Home.name
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
                onFlashcardTopicReviewed = { flashcard, markedKnown ->
                    coroutineScope.launch {
                        topicPerformanceRepository.recordFlashcardReview(
                            pathId = flashcard.pathId,
                            topic = flashcard.topic,
                            difficulty = flashcard.difficulty,
                            markedKnown = markedKnown
                        )
                        flashcardReviewRepository.recordReview(
                            flashcard = flashcard,
                            markedKnown = markedKnown
                        )
                    }
                },
                onQuizTopicAnswered = { question, isCorrect ->
                    coroutineScope.launch {
                        topicPerformanceRepository.recordQuizAnswer(
                            pathId = question.pathId,
                            topic = question.topic,
                            difficulty = question.difficulty,
                            isCorrect = isCorrect
                        )
                    }
                },
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
                    dailyStudyMinutes = onboardingPreferences.dailyStudyMinutes,
                    topicPerformance = topicPerformance.filter { it.pathId == selectedStudyPath?.id },
                    flashcardReviewSummary = selectedFlashcardReviewSummary,
                    isPremiumActive = premiumUiState.isPremiumActive,
                    onChooseStudyPath = { isChoosingStudyPath = true },
                    onOpenSettings = { isSettingsOpen = true },
                    onViewPremium = { isPremiumOpen = true },
                    onStartDailySession = { isDailySessionActive = true },
                    onStartFlashcards = {
                        isChoosingStudyPath = false
                        flashcardModeName = FlashcardMode.All.name
                        selectedDestinationName = LearnLiftDestination.Flashcards.name
                    },
                    onStartSmartReview = {
                        isChoosingStudyPath = false
                        flashcardModeName = FlashcardMode.SmartReview.name
                        selectedDestinationName = LearnLiftDestination.Flashcards.name
                    },
                    onStartQuiz = {
                        isChoosingStudyPath = false
                        quizModeName = QuizMode.Normal.name
                        selectedDestinationName = LearnLiftDestination.Quiz.name
                    },
                    onStartAdaptiveQuiz = {
                        isChoosingStudyPath = false
                        quizModeName = QuizMode.Adaptive.name
                        selectedDestinationName = LearnLiftDestination.Quiz.name
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                LearnLiftDestination.Flashcards -> FlashcardsScreen(
                    selectedStudyPath = selectedStudyPath,
                    selectedStudyContent = selectedStudyContent,
                    reviewStates = flashcardReviewStates.filter { selectedStudyPath == null || it.pathId == selectedStudyPath.id },
                    flashcardMode = flashcardMode,
                    onFlashcardReviewed = { reviewedDelta, knownDelta, needsReviewDelta ->
                        coroutineScope.launch {
                            progressRepository.recordFlashcardReview(
                                reviewedDelta = reviewedDelta,
                                knownDelta = knownDelta,
                                needsReviewDelta = needsReviewDelta
                            )
                        }
                    },
                    onFlashcardTopicReviewed = { flashcard, markedKnown ->
                        coroutineScope.launch {
                            topicPerformanceRepository.recordFlashcardReview(
                                pathId = flashcard.pathId,
                                topic = flashcard.topic,
                                difficulty = flashcard.difficulty,
                                markedKnown = markedKnown
                            )
                            flashcardReviewRepository.recordReview(
                                flashcard = flashcard,
                                markedKnown = markedKnown
                            )
                        }
                    },
                    onContinueAllFlashcards = {
                        flashcardModeName = FlashcardMode.All.name
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                LearnLiftDestination.Quiz -> QuizScreen(
                    selectedStudyPath = selectedStudyPath,
                    selectedStudyContent = selectedStudyContent,
                    isPremiumActive = premiumUiState.isPremiumActive,
                    aiUsageState = aiUsageState,
                    aiUsageRepository = aiUsageRepository,
                    topicPerformance = topicPerformance.filter { it.pathId == selectedStudyPath?.id },
                    quizMode = quizMode,
                    onViewPremium = { isPremiumOpen = true },
                    onQuizTopicAnswered = { question, isCorrect ->
                        coroutineScope.launch {
                            topicPerformanceRepository.recordQuizAnswer(
                                pathId = question.pathId,
                                topic = question.topic,
                                difficulty = question.difficulty,
                                isCorrect = isCorrect
                            )
                        }
                    },
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
                    aiUsageState = aiUsageState,
                    aiUsageRepository = aiUsageRepository,
                    topicPerformance = topicPerformance.filter { selectedStudyPath == null || it.pathId == selectedStudyPath.id },
                    flashcardReviewSummary = selectedFlashcardReviewSummary,
                    onStartAdaptiveQuiz = {
                        quizModeName = QuizMode.Adaptive.name
                        selectedDestinationName = LearnLiftDestination.Quiz.name
                    },
                    onStartSmartReview = {
                        flashcardModeName = FlashcardMode.SmartReview.name
                        selectedDestinationName = LearnLiftDestination.Flashcards.name
                    },
                    onOpenSettings = { isSettingsOpen = true },
                    onViewPremium = { isPremiumOpen = true },
                    onResetProgress = {
                        coroutineScope.launch {
                            progressRepository.resetProgressStats()
                            topicPerformanceRepository.resetTopicPerformance()
                            flashcardReviewRepository.resetReviewState()
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

private const val DefaultOnboardingPathId = "job-interview-prep"

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
