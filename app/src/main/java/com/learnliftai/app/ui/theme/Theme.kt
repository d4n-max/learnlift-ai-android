package com.learnliftai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape

private val LightColorScheme = lightColorScheme(
    primary = LearnLiftPurple,
    secondary = LearnLiftPink,
    tertiary = LearnLiftSuccess,
    background = LearnLiftBackground,
    surface = LearnLiftSurface,
    surfaceTint = LearnLiftPurple,
    surfaceVariant = LearnLiftSurfaceVariant,
    primaryContainer = LearnLiftSurfaceVariant,
    secondaryContainer = LearnLiftPink.copy(alpha = 0.14f),
    tertiaryContainer = LearnLiftSuccessContainer,
    outline = LearnLiftOutline,
    outlineVariant = LearnLiftOutline.copy(alpha = 0.58f),
    error = LearnLiftError,
    errorContainer = LearnLiftErrorContainer,
    onPrimary = LearnLiftWhite,
    onSecondary = LearnLiftWhite,
    onTertiary = LearnLiftWhite,
    onBackground = LearnLiftText,
    onSurface = LearnLiftText,
    onSurfaceVariant = LearnLiftTextMuted,
    onPrimaryContainer = LearnLiftPurple,
    onSecondaryContainer = LearnLiftPink,
    onTertiaryContainer = LearnLiftSuccess,
    onError = LearnLiftWhite,
    onErrorContainer = LearnLiftError
)

private val DarkColorScheme = darkColorScheme(
    primary = LearnLiftDarkPrimary,
    secondary = LearnLiftDarkPink,
    tertiary = LearnLiftDarkSuccess,
    background = LearnLiftDarkBackground,
    surface = LearnLiftDarkSurface,
    surfaceTint = LearnLiftDarkPrimary,
    surfaceVariant = LearnLiftDarkSurfaceVariant,
    primaryContainer = LearnLiftPurple,
    secondaryContainer = LearnLiftPink.copy(alpha = 0.24f),
    tertiaryContainer = LearnLiftDarkSuccessContainer,
    outline = LearnLiftDarkOutline,
    outlineVariant = LearnLiftDarkOutline.copy(alpha = 0.68f),
    error = LearnLiftDarkError,
    errorContainer = LearnLiftDarkErrorContainer,
    onPrimary = LearnLiftWhite,
    onSecondary = LearnLiftDarkBackground,
    onTertiary = LearnLiftDarkBackground,
    onBackground = LearnLiftDarkText,
    onSurface = LearnLiftDarkText,
    onSurfaceVariant = LearnLiftDarkTextMuted,
    onPrimaryContainer = LearnLiftWhite,
    onSecondaryContainer = LearnLiftDarkPink,
    onTertiaryContainer = LearnLiftDarkSuccess,
    onError = LearnLiftDarkBackground,
    onErrorContainer = LearnLiftDarkError
)

private val LearnLiftShapes = Shapes(
    extraSmall = RoundedCornerShape(LearnLiftCorners.extraSmall),
    small = RoundedCornerShape(LearnLiftCorners.small),
    medium = RoundedCornerShape(LearnLiftCorners.medium),
    large = RoundedCornerShape(LearnLiftCorners.card),
    extraLarge = RoundedCornerShape(LearnLiftCorners.hero)
)

@Composable
fun LearnLiftAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> if (darkTheme) DarkColorScheme else LightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        shapes = LearnLiftShapes,
        content = content
    )
}
