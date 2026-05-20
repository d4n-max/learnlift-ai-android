package com.learnliftai.app.domain.model

data class ReminderPreferences(
    val remindersEnabled: Boolean = false,
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0,
    val lastReminderScheduledAt: Long? = null
) {
    val reminderTimeLabel: String
        get() = "%02d:%02d".format(reminderHour, reminderMinute)
}
