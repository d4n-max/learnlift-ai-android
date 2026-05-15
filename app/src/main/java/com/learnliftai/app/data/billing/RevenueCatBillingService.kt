package com.learnliftai.app.data.billing

import android.app.Activity
import android.content.Context
import com.learnliftai.app.BuildConfig
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesTransactionException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.Package as RevenueCatPackage

class RevenueCatBillingService(
    private val context: Context,
    private val publicApiKey: String = BuildConfig.REVENUECAT_PUBLIC_API_KEY
) {
    fun configureIfAvailable(): Boolean {
        if (!hasConfiguredKey()) {
            return false
        }

        if (!isConfigured) {
            Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
            Purchases.configure(
                PurchasesConfiguration.Builder(context.applicationContext, publicApiKey).build()
            )
            isConfigured = true
        }

        return true
    }

    suspend fun fetchPremiumState(): PremiumUiState {
        if (!configureIfAvailable()) {
            return PremiumUiState(
                isRevenueCatConfigured = false,
                productsUnavailable = true,
                message = "Premium products are not available yet."
            )
        }

        val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
        val offerings = Purchases.sharedInstance.awaitOfferings()
        val offering = offerings.getOffering(DefaultOfferingId) ?: offerings.current
        return buildPremiumState(customerInfo, offering)
    }

    suspend fun purchase(
        activity: Activity,
        premiumPackage: PremiumPackage
    ): PremiumUiState {
        val packageToPurchase = premiumPackage.revenueCatPackage ?: return PremiumUiState(
            isRevenueCatConfigured = hasConfiguredKey(),
            productsUnavailable = true,
            message = "Premium products are not available yet."
        )

        val purchaseResult = Purchases.sharedInstance.awaitPurchase(
            PurchaseParams.Builder(activity, packageToPurchase).build()
        )
        val latestState = fetchPremiumState()
        return latestState.copy(
            entitlement = entitlementFrom(purchaseResult.customerInfo),
            message = if (entitlementFrom(purchaseResult.customerInfo) == PremiumEntitlement.Premium) {
                "Premium is active. Thank you for supporting LearnLift AI."
            } else {
                "Purchase completed, but Premium is not active yet. Try restoring purchases."
            }
        )
    }

    suspend fun restore(): PremiumUiState {
        if (!configureIfAvailable()) {
            return PremiumUiState(
                isRevenueCatConfigured = false,
                productsUnavailable = true,
                message = "Premium products are not available yet."
            )
        }

        val customerInfo = Purchases.sharedInstance.awaitRestore()
        val latestState = fetchPremiumState()
        return latestState.copy(
            entitlement = entitlementFrom(customerInfo),
            message = if (entitlementFrom(customerInfo) == PremiumEntitlement.Premium) {
                "Premium restored."
            } else {
                "No active Premium subscription was found."
            }
        )
    }

    fun userCancelledMessage(error: Throwable): String? {
        return if (error is PurchasesTransactionException && error.userCancelled) {
            "Purchase cancelled. You can start Premium any time."
        } else {
            null
        }
    }

    private fun buildPremiumState(
        customerInfo: CustomerInfo,
        offering: Offering?
    ): PremiumUiState {
        val monthly = offering?.monthly
            ?: offering?.availablePackages?.firstOrNull {
                it.packageType == PackageType.MONTHLY || MonthlyProductId in it.product.id
            }
        val yearly = offering?.annual
            ?: offering?.availablePackages?.firstOrNull {
                it.packageType == PackageType.ANNUAL || YearlyProductId in it.product.id
            }
        val productsUnavailable = monthly == null && yearly == null

        return PremiumUiState(
            entitlement = entitlementFrom(customerInfo),
            isRevenueCatConfigured = true,
            productsUnavailable = productsUnavailable,
            monthlyPackage = monthly.toPremiumPackage(
                fallback = PremiumPackage(
                    id = MonthlyProductId,
                    title = "Monthly",
                    price = "€3.99",
                    helperText = "Flexible access"
                )
            ),
            yearlyPackage = yearly.toPremiumPackage(
                fallback = PremiumPackage(
                    id = YearlyProductId,
                    title = "Yearly",
                    price = "€24.99",
                    helperText = "Best planned value"
                )
            ),
            message = if (productsUnavailable) "Premium products are not available yet." else null,
            managementUrl = customerInfo.managementURL?.toString()
        )
    }

    private fun entitlementFrom(customerInfo: CustomerInfo): PremiumEntitlement {
        return if (customerInfo.entitlements.active.containsKey(PremiumEntitlementId)) {
            PremiumEntitlement.Premium
        } else {
            PremiumEntitlement.Free
        }
    }

    private fun RevenueCatPackage?.toPremiumPackage(fallback: PremiumPackage): PremiumPackage {
        return this?.let { revenueCatPackage ->
            fallback.copy(
                id = revenueCatPackage.product.id,
                price = revenueCatPackage.product.price.formatted,
                revenueCatPackage = revenueCatPackage
            )
        } ?: fallback
    }

    private fun hasConfiguredKey(): Boolean {
        return publicApiKey.isNotBlank() && publicApiKey != PlaceholderRevenueCatKey
    }

    private companion object {
        var isConfigured = false
        const val PlaceholderRevenueCatKey = "REVENUECAT_PUBLIC_API_KEY_HERE"
    }
}
