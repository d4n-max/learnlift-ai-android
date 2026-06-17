package com.learnliftai.app.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.learnliftai.app.MainActivity
import com.learnliftai.app.R
import com.learnliftai.app.domain.model.ReminderPreferences
import java.util.Calendar

class DailyReminderScheduler(
    private val context: Context
) {
    fun scheduleDailyReminder(preferences: ReminderPreferences) {
        if (!preferences.remindersEnabled) {
            cancelDailyReminder()
            return
        }

        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextReminderMillis(preferences.reminderHour, preferences.reminderMinute),
            AlarmManager.INTERVAL_DAY,
            reminderPendingIntent(context)
        )
    }

    fun cancelDailyReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(reminderPendingIntent(context))
    }

    private fun nextReminderMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    companion object {
        const val ChannelId = "daily_study_reminders"
        private const val ReminderRequestCode = 4901
        private const val NotificationId = 4902

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                val channel = NotificationChannel(
                    ChannelId,
                    "Daily study reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders to continue your LearnLift AI study habit"
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showReminderNotification(
            context: Context
        ) {
            createNotificationChannel(context)
            val title = "Time for a quick study session"
            val body = "Open LearnLift AI for a short focused practice session."
            val notification = NotificationCompat.Builder(context, ChannelId)
                .setSmallIcon(R.drawable.ic_notification_learnlift)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(openAppPendingIntent(context))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(NotificationId, notification)
        }

        private fun reminderPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DailyReminderReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                ReminderRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun openAppPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(
                context,
                ReminderRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
