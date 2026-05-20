package com.learnliftai.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.learnliftai.app.data.LocalReminderPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = LocalReminderPreferencesRepository(context.applicationContext)
                val preferences = repository.preferences.first()
                if (preferences.remindersEnabled) {
                    DailyReminderScheduler(context.applicationContext).scheduleDailyReminder(preferences)
                    repository.markReminderScheduled()
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
