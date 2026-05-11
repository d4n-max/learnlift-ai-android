package com.learnliftai.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object LearnLiftSpacing {
    val extraSmall = 4.dp
    val smallGap = 8.dp
    val mediumGap = 12.dp
    val largeGap = 16.dp
    val titleToTagline = 10.dp
    val contentGap = 18.dp
    val cardPadding = 20.dp
    val logoToTitle = 28.dp
    val sectionGap = 32.dp
    val screenPadding = 24.dp
    val screenHorizontalMargin = 24.dp
    val screenVerticalMargin = 24.dp
    val screenContent = 22.dp
    val logoSize = 96.dp
    val homeLogoSize = 64.dp
    val highlightWidth = 48.dp
    val highlightHeight = 5.dp
}

object LearnLiftCorners {
    val extraSmall = 8.dp
    val small = 12.dp
    val medium = 16.dp
    val logo = 28.dp
    val card = 24.dp
    val hero = 28.dp
    val button = 18.dp
    val chip = 999.dp
    val highlight = 100.dp
}

object LearnLiftElevation {
    val none = 0.dp
    val subtle = 1.dp
    val card = 3.dp
    val hero = 6.dp
}

object LearnLiftTypographySizes {
    val logoText = 32.sp
    val homeLogoText = 24.sp
    val welcomeTitle = 34.sp
    val welcomeTagline = 18.sp
}

object LearnLiftGradients {
    @Composable
    fun hero(): Brush {
        return Brush.linearGradient(
            colors = listOf(
                LearnLiftBrandPurple,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)
            )
        )
    }

    @Composable
    fun premium(): Brush {
        return Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondaryContainer
            )
        )
    }

    @Composable
    fun surfaceGlow(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.background
            )
        )
    }
}
