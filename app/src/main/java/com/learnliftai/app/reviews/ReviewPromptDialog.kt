package com.learnliftai.app.reviews

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ReviewPromptDialog(
    onRateLearnLift: () -> Unit,
    onNotNow: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enjoying LearnLift AI?") },
        text = {
            Text(text = "A quick rating helps us improve the app for more learners.")
        },
        confirmButton = {
            TextButton(onClick = onRateLearnLift) {
                Text(text = "Rate LearnLift")
            }
        },
        dismissButton = {
            TextButton(onClick = onNotNow) {
                Text(text = "Not now")
            }
        }
    )
}
