package com.learnliftai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = LearnLiftPurple,
    secondary = LearnLiftPink,
    background = LearnLiftBackground,
    surface = LearnLiftSurface,
    surfaceVariant = LearnLiftSurfaceVariant,
    onPrimary = LearnLiftWhite,
    onSecondary = LearnLiftWhite,
    onBackground = LearnLiftText,
    onSurface = LearnLiftText
)

private val DarkColorScheme = darkColorScheme(
    primary = LearnLiftPurple,
    secondary = LearnLiftPink,
    background = LearnLiftDarkBackground,
    surface = LearnLiftDarkSurface,
    onPrimary = LearnLiftWhite,
    onSecondary = LearnLiftWhite,
    onBackground = LearnLiftDarkText,
    onSurface = LearnLiftDarkText
)

@Composable
fun LearnLiftAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
