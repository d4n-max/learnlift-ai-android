package com.learnliftai.app.data.billing

import android.app.Activity
import android.content.Context
import com.learnliftai.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PremiumRepository(
    context: Context
) {
    private val billingService = RevenueCatBillingService(context.applicationContext)
    private val _uiState = MutableStateFlow(PremiumUiState())
    val uiState: StateFlow<PremiumUiState> = _uiState.asStateFlow()

    suspend fun refreshPremiumState() {
        _uiState.value = _uiState.value.copy(isLoading = true, message = null)
        _uiState.value = runCatching {
            billingService.fetchPremiumState()
        }.getOrElse {
            PremiumUiState(
                isRevenueCatConfigured = false,
                productsUnavailable = true,
                message = "Premium status could not be refreshed right now.",
                debugUnavailableReason = debugReason("premium_state_refresh_failure:${it.javaClass.simpleName}")
            )
        }
    }

    suspend fun purchase(activity: Activity, premiumPackage: PremiumPackage) {
        _uiState.value = _uiState.value.copy(isPurchasing = true, message = null)
        _uiState.value = runCatching {
            billingService.purchase(activity, premiumPackage)
        }.getOrElse { error ->
            _uiState.value.copy(
                isPurchasing = false,
                message = billingService.userCancelledMessage(error)
                    ?: "Premium purchase could not be completed. Please try again later.",
                debugUnavailableReason = debugReason("premium_purchase_failure:${error.javaClass.simpleName}")
            )
        }
    }

    suspend fun restorePurchases() {
        _uiState.value = _uiState.value.copy(isRestoring = true, message = null)
        _uiState.value = runCatching {
            billingService.restore()
        }.getOrElse {
            _uiState.value.copy(
                isRestoring = false,
                message = "Purchases could not be restored right now.",
                debugUnavailableReason = debugReason("premium_restore_failure:${it.javaClass.simpleName}")
            )
        }
    }

    private fun debugReason(reason: String): String? {
        return reason.takeIf { BuildConfig.DEBUG }
    }
}
