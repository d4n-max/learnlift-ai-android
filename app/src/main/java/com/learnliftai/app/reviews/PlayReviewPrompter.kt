package com.learnliftai.app.reviews

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory

class PlayReviewPrompter {
    fun launchReviewFlow(context: Context) {
        val activity = context.findActivity()
        if (activity == null) {
            Log.d(LogTag, "Play review skipped: no Activity context.")
            return
        }

        runCatching {
            val reviewManager = ReviewManagerFactory.create(activity)
            reviewManager.requestReviewFlow()
                .addOnCompleteListener { request ->
                    if (!request.isSuccessful) {
                        Log.d(LogTag, "Play review info request failed.")
                        return@addOnCompleteListener
                    }
                    reviewManager.launchReviewFlow(activity, request.result)
                        .addOnCompleteListener {
                            Log.d(LogTag, "Play review flow completed or was skipped by Play.")
                        }
                }
        }.onFailure {
            Log.d(LogTag, "Play review flow failed safely.")
        }
    }

    private fun Context.findActivity(): Activity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    private companion object {
        const val LogTag = "PlayReviewPrompter"
    }
}
