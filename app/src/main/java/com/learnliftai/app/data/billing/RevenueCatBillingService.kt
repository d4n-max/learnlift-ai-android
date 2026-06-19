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
import com.revenuecat.purchases.models.googleProduct
import com.revenuecat.purchases.Package as RevenueCatPackage

class RevenueCatBillingService(
    private val context: Context,
    private val publicApiKey: String = BuildConfig.REVENUECAT_PUBLIC_API_KEY
) {
    fun configureIfAvailable(): Boolean {
        logKeyDiagnostics("configure")
        if (!hasConfiguredKey()) {
            debugLog("configure skipped reason=revenuecat_key_missing_or_placeholder")
            return false
        }
        if (!BuildConfig.DEBUG && publicApiKey.startsWith(TestStoreKeyPrefix)) {
            Log.e(LogTag, "Release build cannot use a RevenueCat Test Store public SDK key.")
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
                message = PremiumPlansUnavailableMessage,
                debugUnavailableReason = unavailableReason("revenuecat_key_missing_or_placeholder")
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
            message = PremiumPlansUnavailableMessage,
            debugUnavailableReason = unavailableReason("selected_revenuecat_package_missing")
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
                "Premium active. More AI help, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked."
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
                message = PremiumPlansUnavailableMessage,
                debugUnavailableReason = unavailableReason("revenuecat_key_missing_or_placeholder")
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
                "Premium active. More AI help, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked."
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
        logCustomerInfo("state_update", customerInfo)
        val offerings = runCatching {
            Purchases.sharedInstance.awaitOfferings()
        }.getOrElse { error ->
            debugLog("offerings_fetch_failure error=${error.javaClass.simpleName}")
            return PremiumUiState(
                entitlement = entitlementFrom(customerInfo),
                isRevenueCatConfigured = true,
                productsUnavailable = true,
                message = PremiumPlansUnavailableMessage,
                debugUnavailableReason = unavailableReason("offerings_fetch_failure:${error.javaClass.simpleName}"),
                managementUrl = customerInfo.managementURL?.toString()
            )
        }
        logOfferings(offerings)
        val currentOffering = offerings.current
        val defaultOffering = offerings.getOffering(DefaultOfferingId)
        val offering = currentOffering ?: defaultOffering
        when {
            currentOffering == null && defaultOffering == null -> {
                debugLog("offering_selection reason=current_and_default_offering_missing")
            }
            currentOffering == null -> {
                debugLog("offering_selection reason=current_offering_missing using=$DefaultOfferingId")
            }
            defaultOffering == null -> {
                debugLog("offering_selection reason=default_offering_missing using_current=${currentOffering.identifier}")
            }
            else -> {
                debugLog("offering_selection using_current=${currentOffering.identifier}")
            }
        }
        return buildPremiumState(customerInfo, offering)
    }

    private fun buildPremiumState(
        customerInfo: CustomerInfo,
        offering: Offering?
    ): PremiumUiState {
        val availablePackages = offering?.availablePackages.orEmpty()
        val monthly = availablePackages.selectMonthlyPackage()
        val yearly = availablePackages.selectYearlyPackage()
        val productsUnavailable = monthly == null && yearly == null
        val debugUnavailableReason = unavailableReason(
            buildUnavailableReason(
                offering = offering,
                monthly = monthly,
                yearly = yearly
            )
        )
        logPackageResolution(offering, monthly, yearly, debugUnavailableReason)

        return PremiumUiState(
            entitlement = entitlementFrom(customerInfo),
            isRevenueCatConfigured = true,
            productsUnavailable = productsUnavailable,
            monthlyPackage = monthly.toPremiumPackage(
                fallback = PremiumPackage(
                    id = MonthlyProductId,
                    title = "Monthly",
                    price = "€3.99 / month",
                    helperText = "Flexible access to more AI help and full Premium Study Packs."
                )
            ),
            yearlyPackage = yearly.toPremiumPackage(
                fallback = PremiumPackage(
                    id = YearlyProductId,
                    title = "Yearly",
                    price = "€24.99 / year",
                    helperText = "Best value for steady weekly practice."
                )
            ),
            message = if (productsUnavailable) PremiumPlansUnavailableMessage else null,
            debugUnavailableReason = debugUnavailableReason,
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

    private fun List<RevenueCatPackage>.selectMonthlyPackage(): RevenueCatPackage? {
        return selectExpectedPackage(
            expectedProductId = MonthlyProductId,
            expectedBasePlanId = MonthlyPackageId,
            fallbackPackageIds = setOf(MonthlyPackageId),
            expectedPackageType = PackageType.MONTHLY
        )
    }

    private fun List<RevenueCatPackage>.selectYearlyPackage(): RevenueCatPackage? {
        return selectExpectedPackage(
            expectedProductId = YearlyProductId,
            expectedBasePlanId = YearlyPackageId,
            fallbackPackageIds = setOf(YearlyPackageId, AnnualPackageId),
            expectedPackageType = PackageType.ANNUAL
        )
    }

    private fun List<RevenueCatPackage>.selectExpectedPackage(
        expectedProductId: String,
        expectedBasePlanId: String,
        fallbackPackageIds: Set<String>,
        expectedPackageType: PackageType
    ): RevenueCatPackage? {
        return firstOrNull {
            it.matchesExpectedPlaySubscription(
                expectedProductId = expectedProductId,
                expectedBasePlanId = expectedBasePlanId
            )
        }
            ?: takeIf { BuildConfig.DEBUG && BuildConfig.USE_REVENUECAT_TEST_STORE }
                ?.firstOrNull { revenueCatPackage ->
                    val packageId = revenueCatPackage.identifier.lowercase()
                    val productId = revenueCatPackage.product.id.lowercase()
                    fallbackPackageIds.any { expectedId ->
                        packageId == expectedId ||
                            expectedId in packageId ||
                            expectedId in productId
                    } || revenueCatPackage.packageType == expectedPackageType
                }
    }

    private fun RevenueCatPackage.matchesExpectedPlaySubscription(
        expectedProductId: String,
        expectedBasePlanId: String
    ): Boolean {
        val googleProduct = product.googleProduct
        if (googleProduct != null) {
            return googleProduct.productId.equals(expectedProductId, ignoreCase = true) &&
                googleProduct.basePlanId.equals(expectedBasePlanId, ignoreCase = true)
        }

        return product.id.matchesExpectedPlayProductId(
            expectedProductId = expectedProductId,
            expectedBasePlanId = expectedBasePlanId
        )
    }

    private fun String.matchesExpectedPlayProductId(
        expectedProductId: String,
        expectedBasePlanId: String
    ): Boolean {
        return equals(expectedProductId, ignoreCase = true) ||
            equals("$expectedProductId:$expectedBasePlanId", ignoreCase = true)
    }

    private fun RevenueCatPackage.displayTitle(fallbackTitle: String): String {
        val packageId = identifier.lowercase()
        val productId = product.id.lowercase()
        return when {
            packageId == MonthlyPackageId ||
                MonthlyPackageId in packageId ||
                productId == MonthlyProductId ||
                MonthlyPackageId in productId -> "Monthly"
            packageId == YearlyPackageId ||
                packageId == AnnualPackageId ||
                YearlyPackageId in packageId ||
                AnnualPackageId in packageId ||
                productId == YearlyProductId ||
                YearlyPackageId in productId ||
                AnnualPackageId in productId -> "Yearly"
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

    private fun logKeyDiagnostics(event: String) {
        if (!BuildConfig.DEBUG) return

        Log.d(
            LogTag,
            "$event keyPresent=${publicApiKey.isNotBlank()}, " +
                "keyPlaceholder=${isPlaceholderKey(publicApiKey)}, " +
                "keyStartsWithTest=${publicApiKey.startsWith(TestStoreKeyPrefix)}, " +
                "keySource=${BuildConfig.REVENUECAT_PUBLIC_API_KEY_SOURCE}, " +
                "useRevenueCatTestStore=${BuildConfig.USE_REVENUECAT_TEST_STORE}"
        )
    }

    private fun logOfferings(offerings: com.revenuecat.purchases.Offerings) {
        if (!BuildConfig.DEBUG) return

        val offeringIds = offerings.all.keys.sorted()
        Log.d(
            LogTag,
            "offerings_fetch_success currentOfferingId=${offerings.current?.identifier}, " +
                "defaultOfferingPresent=${offerings.getOffering(DefaultOfferingId) != null}, " +
                "offeringIds=$offeringIds"
        )
        offerings.all.values
            .sortedBy { it.identifier }
            .forEach { offering ->
                offering.availablePackages.forEach { revenueCatPackage ->
                    Log.d(
                        LogTag,
                        "offering_package offeringId=${offering.identifier}, " +
                            "packageId=${revenueCatPackage.identifier}, " +
                            "packageType=${revenueCatPackage.packageType}, " +
                            "productId=${revenueCatPackage.product.id}"
                    )
                }
            }
    }

    private fun logPackageResolution(
        offering: Offering?,
        monthly: RevenueCatPackage?,
        yearly: RevenueCatPackage?,
        debugUnavailableReason: String?
    ) {
        if (!BuildConfig.DEBUG) return

        debugLog(
            "package_resolution selectedOfferingId=${offering?.identifier}, " +
                "monthlyProductId=${monthly?.product?.id}, " +
                "yearlyProductId=${yearly?.product?.id}, " +
                "reason=${debugUnavailableReason ?: "available"}"
        )
    }

    private fun buildUnavailableReason(
        offering: Offering?,
        monthly: RevenueCatPackage?,
        yearly: RevenueCatPackage?
    ): String? {
        if (offering == null) return "current_and_default_offering_missing"

        val productIds = offering.availablePackages.map { it.product.id }.sorted()
        val hasExpectedMonthlyProduct = offering.availablePackages.any {
            it.matchesExpectedPlaySubscription(
                expectedProductId = MonthlyProductId,
                expectedBasePlanId = MonthlyPackageId
            )
        }
        val hasExpectedYearlyProduct = offering.availablePackages.any {
            it.matchesExpectedPlaySubscription(
                expectedProductId = YearlyProductId,
                expectedBasePlanId = YearlyPackageId
            )
        }

        val reasons = buildList {
            if (monthly == null) add("monthly_package_missing_or_product_id_mismatch")
            if (yearly == null) add("yearly_package_missing_or_product_id_mismatch")
            if (offering.availablePackages.isNotEmpty() && (!hasExpectedMonthlyProduct || !hasExpectedYearlyProduct)) {
                add("packages_present_product_ids=${productIds.joinToString("|")}")
            }
        }

        return reasons.takeIf { it.isNotEmpty() }?.joinToString(";")
    }

    private fun unavailableReason(reason: String?): String? {
        return reason?.takeIf { BuildConfig.DEBUG }
    }

    private fun debugLog(message: String) {
        if (!BuildConfig.DEBUG) return

        Log.d(LogTag, message)
    }

    private fun hasConfiguredKey(): Boolean {
        return publicApiKey.isNotBlank() && !isPlaceholderKey(publicApiKey)
    }

    private fun isPlaceholderKey(key: String): Boolean {
        return key in PlaceholderRevenueCatKeys || key.endsWith("_HERE")
    }

    private companion object {
        const val LogTag = "LearnLiftPremium"
        var isConfigured = false
        const val TestStoreKeyPrefix = "test_"
        const val PremiumPlansUnavailableMessage =
            "Premium plans are temporarily unavailable. Please try again later."
        val PlaceholderRevenueCatKeys = setOf(
            "REVENUECAT_PUBLIC_API_KEY_HERE",
            "REVENUECAT_ANDROID_PUBLIC_API_KEY_HERE",
            "REVENUECAT_TEST_STORE_API_KEY_HERE"
        )
    }
}
