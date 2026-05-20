package com.learnliftai.app

import android.app.Application
import com.learnliftai.app.data.billing.RevenueCatBillingService
import com.learnliftai.app.notifications.DailyReminderScheduler

class LearnLiftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DailyReminderScheduler.createNotificationChannel(this)
        RevenueCatBillingService(this).configureIfAvailable()
    }
}
