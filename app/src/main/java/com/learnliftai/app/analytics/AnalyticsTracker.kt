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

    fun purchaseCancelled(
        plan: String,
        errorType: String = "user_cancelled",
        source: AnalyticsSource = AnalyticsSource.Paywall
    ) {
        logEvent(
            name = EventPurchaseCancelled,
            params = mapOf(
                ParamScreen to AnalyticsScreen.Paywall.analyticsName,
                ParamSource to source.value,
                ParamPlan to plan,
                ParamResult to AnalyticsResult.Cancelled.value,
                ParamErrorType to errorType
            )
        )
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

    fun premiumEntitlementActive(plan: String? = null, source: AnalyticsSource = AnalyticsSource.Paywall) {
        logEvent(
            name = EventPremiumEntitlementActive,
            params = buildMap {
                put(ParamScreen, AnalyticsScreen.Paywall.analyticsName)
                put(ParamSource, source.value)
                put(ParamEntitlementId, PremiumEntitlementIdValue)
                put(ParamPremiumStatus, PremiumStatusPremium)
                if (!plan.isNullOrBlank()) {
                    put(ParamPlan, plan)
                }
            }
        )
    }

    fun dailySessionCompleted(
        studyPathId: String,
        studyPathTitle: String,
        questionsCount: Int,
        correctCount: Int,
        scorePercent: Int,
        isPremiumActive: Boolean
    ) {
        logEvent(
            name = EventDailySessionCompleted,
            params = mapOf(
                ParamStudyPathId to studyPathId,
                ParamStudyPathTitle to studyPathTitle,
                ParamQuestionsCount to questionsCount,
                ParamCorrectCount to correctCount,
                ParamScorePercent to scorePercent,
                ParamPremiumStatus to if (isPremiumActive) PremiumStatusPremium else PremiumStatusFree,
                ParamSourceScreen to AnalyticsScreen.DailySession.analyticsName
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

    private fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putDouble(key, value.toDouble())
                    is Boolean -> putString(key, value.toString())
                    else -> putString(key, value.toString().take(MaxParamValueLength))
                }
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
        if (BuildConfig.DEBUG) {
            Log.d(LogTag, "firebase_event name=$name params=$params")
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
        const val EventPurchaseCancelled = "purchase_cancelled"
        const val EventPremiumEntitlementActive = "premium_entitlement_active"
        const val EventDailySessionCompleted = "daily_session_completed"
        const val EventSettingsOpened = "settings_opened"

        const val ParamScreen = "screen"
        const val ParamSource = "source"
        const val ParamSourceScreen = "source_screen"
        const val ParamPlan = "plan"
        const val ParamResult = "result"
        const val ParamErrorType = "error_type"
        const val ParamEntitlementId = "entitlement_id"
        const val ParamPremiumStatus = "premium_status"
        const val ParamStudyPathId = "study_path_id"
        const val ParamStudyPathTitle = "study_path_title"
        const val ParamQuestionsCount = "questions_count"
        const val ParamCorrectCount = "correct_count"
        const val ParamScorePercent = "score_percent"

        const val PremiumEntitlementIdValue = "premium"
        const val PremiumStatusFree = "free"
        const val PremiumStatusPremium = "premium"
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
