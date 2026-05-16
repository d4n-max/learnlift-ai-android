package com.learnliftai.app

import android.app.Application
import com.learnliftai.app.data.billing.RevenueCatBillingService

class LearnLiftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RevenueCatBillingService(this).configureIfAvailable()
    }
}
