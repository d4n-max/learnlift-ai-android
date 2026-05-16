package com.learnliftai.app.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
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
        return fetchPremiumState(customerInfo)
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

        logPurchasePackage("purchase_completed", packageToPurchase)
        logCustomerInfo("purchase_result", purchaseResult.customerInfo)

        val refreshedCustomerInfo = Purchases.sharedInstance.awaitCustomerInfo()
        logCustomerInfo("purchase_refreshed", refreshedCustomerInfo)

        val customerInfoForState = if (hasPremiumEntitlement(purchaseResult.customerInfo)) {
            purchaseResult.customerInfo
        } else {
            refreshedCustomerInfo
        }
        val latestState = fetchPremiumState(customerInfoForState)
        val entitlement = entitlementFrom(customerInfoForState)

        return latestState.copy(
            entitlement = entitlement,
            message = if (entitlement == PremiumEntitlement.Premium) {
                "Premium is active. Thank you for supporting LearnLift AI."
            } else {
                "Purchase completed, but Premium entitlement is not active yet. Check RevenueCat product-entitlement setup."
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
        logCustomerInfo("restore_result", customerInfo)

        val refreshedCustomerInfo = Purchases.sharedInstance.awaitCustomerInfo()
        logCustomerInfo("restore_refreshed", refreshedCustomerInfo)

        val customerInfoForState = if (hasPremiumEntitlement(customerInfo)) {
            customerInfo
        } else {
            refreshedCustomerInfo
        }
        val latestState = fetchPremiumState(customerInfoForState)
        val entitlement = entitlementFrom(customerInfoForState)

        return latestState.copy(
            entitlement = entitlement,
            message = if (entitlement == PremiumEntitlement.Premium) {
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

    private suspend fun fetchPremiumState(customerInfo: CustomerInfo): PremiumUiState {
        val offerings = Purchases.sharedInstance.awaitOfferings()
        val offering = offerings.getOffering(DefaultOfferingId) ?: offerings.current
        logCustomerInfo("state_update", customerInfo)
        return buildPremiumState(customerInfo, offering)
    }

    private fun buildPremiumState(
        customerInfo: CustomerInfo,
        offering: Offering?
    ): PremiumUiState {
        val monthly = offering?.monthly
            ?: offering?.availablePackages?.firstOrNull {
                it.identifier == MonthlyPackageId ||
                    it.packageType == PackageType.MONTHLY ||
                    MonthlyProductId in it.product.id
            }
        val yearly = offering?.annual
            ?: offering?.availablePackages?.firstOrNull {
                it.identifier == YearlyPackageId ||
                    it.packageType == PackageType.ANNUAL ||
                    YearlyProductId in it.product.id
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
        return if (hasPremiumEntitlement(customerInfo)) {
            PremiumEntitlement.Premium
        } else {
            PremiumEntitlement.Free
        }
    }

    private fun hasPremiumEntitlement(customerInfo: CustomerInfo): Boolean {
        return customerInfo.entitlements[PremiumEntitlementId]?.isActive == true
    }

    private fun RevenueCatPackage?.toPremiumPackage(fallback: PremiumPackage): PremiumPackage {
        return this?.let { revenueCatPackage ->
            fallback.copy(
                id = revenueCatPackage.product.id,
                title = revenueCatPackage.displayTitle(fallback.title),
                price = revenueCatPackage.product.price.formatted,
                revenueCatPackage = revenueCatPackage
            )
        } ?: fallback
    }

    private fun RevenueCatPackage.displayTitle(fallbackTitle: String): String {
        val packageId = identifier.lowercase()
        val productId = product.id.lowercase()
        return when {
            packageId == MonthlyPackageId ||
                productId == MonthlyProductId ||
                productId.endsWith("monthly") -> "Monthly"
            packageId == YearlyPackageId ||
                packageId == AnnualPackageId ||
                productId == YearlyProductId ||
                productId.endsWith("yearly") ||
                productId.endsWith("annual") -> "Yearly"
            else -> fallbackTitle
        }
    }

    private fun logPurchasePackage(event: String, revenueCatPackage: RevenueCatPackage) {
        if (!BuildConfig.DEBUG) return

        Log.d(
            LogTag,
            "$event productId=${revenueCatPackage.product.id}, " +
                "offeringId=${revenueCatPackage.presentedOfferingContext.offeringIdentifier}, " +
                "packageId=${revenueCatPackage.identifier}"
        )
    }

    private fun logCustomerInfo(event: String, customerInfo: CustomerInfo) {
        if (!BuildConfig.DEBUG) return

        val activeEntitlements = customerInfo.entitlements.active.keys.sorted()
        val allEntitlements = customerInfo.entitlements.all.keys.sorted()
        val activeSubscriptions = customerInfo.activeSubscriptions.sorted()
        val allPurchasedProducts = customerInfo.allPurchasedProductIds.sorted()

        Log.d(
            LogTag,
            "$event activeEntitlements=$activeEntitlements, " +
                "allEntitlements=$allEntitlements, " +
                "activeSubscriptions=$activeSubscriptions, " +
                "allPurchasedProductIds=$allPurchasedProducts"
        )
    }

    private fun hasConfiguredKey(): Boolean {
        return publicApiKey.isNotBlank() && publicApiKey != PlaceholderRevenueCatKey
    }

    private companion object {
        const val LogTag = "LearnLiftPremium"
        var isConfigured = false
        const val PlaceholderRevenueCatKey = "REVENUECAT_PUBLIC_API_KEY_HERE"
    }
}
