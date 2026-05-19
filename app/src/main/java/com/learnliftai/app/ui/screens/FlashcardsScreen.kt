package com.learnliftai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.domain.model.Flashcard
import com.learnliftai.app.domain.model.StudyContent
import com.learnliftai.app.domain.model.StudyPath
import com.learnliftai.app.ui.components.EmptyState
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.StatCard
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun FlashcardsScreen(
    selectedStudyPath: StudyPath?,
    selectedStudyContent: StudyContent?,
    onFlashcardReviewed: (reviewedDelta: Int, knownDelta: Int, needsReviewDelta: Int) -> Unit,
    onFlashcardTopicReviewed: (flashcard: Flashcard, markedKnown: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val flashcards = selectedStudyContent?.flashcards.orEmpty()

    if (selectedStudyPath == null) {
        FlashcardsEmptyState(
            title = "Choose a study path first",
            description = "Pick a study path from Home, then your flashcards will appear here for focused review.",
            modifier = modifier
        )
        return
    }

    if (flashcards.isEmpty()) {
        FlashcardsEmptyState(
            title = "No flashcards yet",
            description = "${selectedStudyPath.title} does not have flashcards available yet.",
            modifier = modifier
        )
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isAnswerRevealed by remember { mutableStateOf(false) }
    var knownCardIds by remember { mutableStateOf(emptySet<String>()) }
    var needsReviewCardIds by remember { mutableStateOf(emptySet<String>()) }
    val persistedSessionRatings = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(selectedStudyPath.id) {
        currentIndex = 0
        isAnswerRevealed = false
        knownCardIds = emptySet()
        needsReviewCardIds = emptySet()
        persistedSessionRatings.clear()
    }

    val safeIndex = currentIndex.coerceIn(0, flashcards.lastIndex)
    val currentFlashcard = flashcards[safeIndex]
    val reviewedCount = (knownCardIds + needsReviewCardIds).size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        SectionHeader(
            title = "Flashcards",
            subtitle = selectedStudyPath.title
        )

        FlashcardProgress(
            currentIndex = safeIndex,
            totalCards = flashcards.size
        )

        FlashcardStudyCard(
            flashcard = currentFlashcard,
            isAnswerRevealed = isAnswerRevealed,
            onRevealAnswer = { isAnswerRevealed = true }
        )

        FlashcardReviewActions(
            isAnswerRevealed = isAnswerRevealed,
            onKnown = {
                val previousRating = persistedSessionRatings[currentFlashcard.id]
                if (previousRating != FlashcardRatingKnown) {
                    onFlashcardReviewed(
                        if (previousRating == null) 1 else 0,
                        1,
                        if (previousRating == FlashcardRatingNeedsReview) -1 else 0
                    )
                    persistedSessionRatings[currentFlashcard.id] = FlashcardRatingKnown
                    onFlashcardTopicReviewed(currentFlashcard, true)
                }
                knownCardIds = knownCardIds + currentFlashcard.id
                needsReviewCardIds = needsReviewCardIds - currentFlashcard.id
            },
            onNeedsReview = {
                val previousRating = persistedSessionRatings[currentFlashcard.id]
                if (previousRating != FlashcardRatingNeedsReview) {
                    onFlashcardReviewed(
                        if (previousRating == null) 1 else 0,
                        if (previousRating == FlashcardRatingKnown) -1 else 0,
                        1
                    )
                    persistedSessionRatings[currentFlashcard.id] = FlashcardRatingNeedsReview
                    onFlashcardTopicReviewed(currentFlashcard, false)
                }
                needsReviewCardIds = needsReviewCardIds + currentFlashcard.id
                knownCardIds = knownCardIds - currentFlashcard.id
            }
        )

        FlashcardNavigationActions(
            canGoPrevious = safeIndex > 0,
            canGoNext = safeIndex < flashcards.lastIndex,
            onPrevious = {
                currentIndex = (safeIndex - 1).coerceAtLeast(0)
                isAnswerRevealed = false
            },
            onNext = {
                currentIndex = (safeIndex + 1).coerceAtMost(flashcards.lastIndex)
                isAnswerRevealed = false
            }
        )

        FlashcardSessionStats(
            reviewedCount = reviewedCount,
            knownCount = knownCardIds.size,
            needsReviewCount = needsReviewCardIds.size
        )
    }
}

private const val FlashcardRatingKnown = "known"
private const val FlashcardRatingNeedsReview = "needsReview"

@Composable
private fun FlashcardsEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.Center
    ) {
        EmptyState(
            title = title,
            description = description
        )
    }
}

@Composable
private fun FlashcardProgress(
    currentIndex: Int,
    totalCards: Int
) {
    LearnLiftCard {
        Text(
            text = "Card ${currentIndex + 1} of $totalCards",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalCards.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    }
}

@Composable
private fun FlashcardStudyCard(
    flashcard: Flashcard,
    isAnswerRevealed: Boolean,
    onRevealAnswer: () -> Unit
) {
    LearnLiftCard {
        Text(
            text = "Question",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = flashcard.question,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (isAnswerRevealed) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.sectionGap))
            Text(
                text = "Answer",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = flashcard.answer,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.84f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
            Row(
                horizontalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
            ) {
                FlashcardMetaPill(text = flashcard.topic)
                FlashcardMetaPill(text = flashcard.difficulty)
            }
        } else {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.sectionGap))
            PrimaryActionButton(
                text = "Reveal answer",
                onClick = onRevealAnswer
            )
        }
    }
}

@Composable
private fun FlashcardReviewActions(
    isAnswerRevealed: Boolean,
    onKnown: () -> Unit,
    onNeedsReview: () -> Unit
) {
    LearnLiftCard {
        Text(
            text = "Session review",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Reveal the answer, then mark how this card felt.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        PrimaryActionButton(
            text = "Known",
            onClick = onKnown,
            enabled = isAnswerRevealed
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        SecondaryActionButton(
            text = "Needs review",
            onClick = onNeedsReview,
            enabled = isAnswerRevealed
        )
    }
}

@Composable
private fun FlashcardNavigationActions(
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    LearnLiftCard {
        SecondaryActionButton(
            text = "Previous card",
            onClick = onPrevious,
            enabled = canGoPrevious
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        PrimaryActionButton(
            text = "Next card",
            onClick = onNext,
            enabled = canGoNext
        )
    }
}

@Composable
private fun FlashcardSessionStats(
    reviewedCount: Int,
    knownCount: Int,
    needsReviewCount: Int
) {
    StatCard(
        label = "Reviewed this session",
        value = reviewedCount.toString(),
        helperText = "$knownCount known - $needsReviewCount need review"
    )
}

@Composable
private fun FlashcardMetaPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                shape = RoundedCornerShape(LearnLiftCorners.highlight)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold
    )
}
