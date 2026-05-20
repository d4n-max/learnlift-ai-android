package com.learnliftai.app.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.learnliftai.app.data.LocalOnboardingRepository
import com.learnliftai.app.data.LocalProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (!canPostNotifications(context)) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val progress = LocalProgressRepository(context.applicationContext).progress.first()
                val onboarding = LocalOnboardingRepository(context.applicationContext).preferences.first()
                DailyReminderScheduler.showReminderNotification(
                    context = context.applicationContext,
                    userProgress = progress,
                    dailyStudyMinutes = onboarding.dailyStudyMinutes
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }
}
