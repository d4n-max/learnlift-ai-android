package com.learnliftai.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.learnliftai.app.ui.screens.FlashcardsScreen
import com.learnliftai.app.ui.screens.HomeScreen
import com.learnliftai.app.ui.screens.ProgressScreen
import com.learnliftai.app.ui.screens.QuizScreen

@Composable
fun LearnLiftApp() {
    var selectedDestinationName by rememberSaveable {
        mutableStateOf(LearnLiftDestination.Home.name)
    }
    val selectedDestination = LearnLiftDestination.valueOf(selectedDestinationName)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            LearnLiftBottomNavigation(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestinationName = it.name }
            )
        }
    ) { innerPadding ->
        when (selectedDestination) {
            LearnLiftDestination.Home -> HomeScreen(modifier = Modifier.padding(innerPadding))
            LearnLiftDestination.Flashcards -> FlashcardsScreen(modifier = Modifier.padding(innerPadding))
            LearnLiftDestination.Quiz -> QuizScreen(modifier = Modifier.padding(innerPadding))
            LearnLiftDestination.Progress -> ProgressScreen(modifier = Modifier.padding(innerPadding))
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
