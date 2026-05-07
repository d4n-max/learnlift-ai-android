package com.learnliftai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.learnliftai.app.ui.navigation.LearnLiftApp
import com.learnliftai.app.ui.theme.LearnLiftAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LearnLiftAITheme {
                LearnLiftApp()
            }
        }
    }
}
