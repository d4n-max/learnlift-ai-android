package com.learnliftai.app.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.learnliftai.app.BuildConfig

class AnalyticsTracker(context: Context) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)

    fun trackScreenView(screen: AnalyticsScreen) {
        logEvent(
            name = FirebaseAnalytics.Event.SCREEN_VIEW,
            params = mapOf(
                ParamScreen to screen.analyticsName,
                FirebaseAnalytics.Param.SCREEN_NAME to screen.analyticsName,
                FirebaseAnalytics.Param.SCREEN_CLASS to screen.analyticsName
            )
        )
    }

    fun onboardingStarted(source: AnalyticsSource = AnalyticsSource.AppStart) {
        logEvent(EventOnboardingStarted, mapOf(ParamScreen to AnalyticsScreen.Onboarding.analyticsName, ParamSource to source.value))
    }

    fun onboardingCompleted(result: AnalyticsResult, source: AnalyticsSource = AnalyticsSource.Onboarding) {
        logEvent(
            name = EventOnboardingCompleted,
            params = mapOf(
                ParamScreen to AnalyticsScreen.Onboarding.analyticsName,
                ParamSource to source.value,
                ParamResult to result.value
            )
        )
    }

    fun paywallViewed(source: AnalyticsSource) {
        logEvent(
            name = EventPaywallViewed,
            params = mapOf(
                ParamScreen to AnalyticsScreen.Paywall.analyticsName,
                ParamSource to source.value
            )
        )
    }

    fun premiumCtaClicked(source: AnalyticsSource, plan: String) {
        logEvent(
            name = EventPremiumCtaClicked,
            params = mapOf(
                ParamScreen to AnalyticsScreen.Paywall.analyticsName,
                ParamSource to source.value,
                ParamPlan to plan
            )
        )
    }

    fun purchaseStarted(plan: String, source: AnalyticsSource = AnalyticsSource.Paywall) {
        logPurchaseEvent(EventPurchaseStarted, plan, AnalyticsResult.Started, source)
    }

    fun purchaseSuccess(plan: String, source: AnalyticsSource = AnalyticsSource.Paywall) {
        logPurchaseEvent(EventPurchaseSuccess, plan, AnalyticsResult.Success, source)
    }

    fun purchaseFailed(
        plan: String,
        result: AnalyticsResult,
        errorType: String,
        source: AnalyticsSource = AnalyticsSource.Paywall
    ) {
        logEvent(
            name = EventPurchaseFailed,
            params = mapOf(
                ParamScreen to AnalyticsScreen.Paywall.analyticsName,
                ParamSource to source.value,
                ParamPlan to plan,
                ParamResult to result.value,
                ParamErrorType to errorType
            )
        )
    }

    fun settingsOpened(source: AnalyticsSource) {
        logEvent(
            name = EventSettingsOpened,
            params = mapOf(
                ParamScreen to AnalyticsScreen.Settings.analyticsName,
                ParamSource to source.value
            )
        )
    }

    private fun logPurchaseEvent(
        name: String,
        plan: String,
        result: AnalyticsResult,
        source: AnalyticsSource = AnalyticsSource.Paywall
    ) {
        logEvent(
            name = name,
            params = mapOf(
                ParamScreen to AnalyticsScreen.Paywall.analyticsName,
                ParamSource to source.value,
                ParamPlan to plan,
                ParamResult to result.value
            )
        )
    }

    private fun logEvent(name: String, params: Map<String, String>) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                putString(key, value.take(MaxParamValueLength))
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
        if (BuildConfig.DEBUG) {
            Log.d(LogTag, "event=$name params=$params")
        }
    }

    private companion object {
        const val LogTag = "LearnLiftAnalytics"
        const val MaxParamValueLength = 100

        const val EventOnboardingStarted = "onboarding_started"
        const val EventOnboardingCompleted = "onboarding_completed"
        const val EventPaywallViewed = "paywall_viewed"
        const val EventPremiumCtaClicked = "premium_cta_clicked"
        const val EventPurchaseStarted = "purchase_started"
        const val EventPurchaseSuccess = "purchase_success"
        const val EventPurchaseFailed = "purchase_failed"
        const val EventSettingsOpened = "settings_opened"

        const val ParamScreen = "screen"
        const val ParamSource = "source"
        const val ParamPlan = "plan"
        const val ParamResult = "result"
        const val ParamErrorType = "error_type"
    }
}

enum class AnalyticsScreen(val analyticsName: String) {
    Onboarding("onboarding"),
    Home("home"),
    Flashcards("flashcards"),
    Quiz("quiz"),
    Progress("progress"),
    DailySession("daily_session"),
    StudyPaths("study_paths"),
    Settings("settings"),
    Paywall("paywall")
}

enum class AnalyticsSource(val value: String) {
    AppStart("app_start"),
    Onboarding("onboarding"),
    Home("home"),
    Flashcards("flashcards"),
    Quiz("quiz"),
    Progress("progress"),
    StudyPaths("study_paths"),
    Settings("settings"),
    Paywall("paywall")
}

enum class AnalyticsResult(val value: String) {
    Started("started"),
    Completed("completed"),
    Skipped("skipped"),
    Success("success"),
    Failure("failure"),
    Cancelled("cancelled")
}
