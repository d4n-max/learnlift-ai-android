package com.learnliftai.app.data.billing

import com.revenuecat.purchases.Package

enum class PremiumEntitlement {
    Free,
    Premium
}

data class PremiumPackage(
    val id: String,
    val title: String,
    val price: String,
    val helperText: String,
    internal val revenueCatPackage: Package? = null
)

data class PremiumUiState(
    val entitlement: PremiumEntitlement = PremiumEntitlement.Free,
    val isLoading: Boolean = false,
    val isPurchasing: Boolean = false,
    val isRestoring: Boolean = false,
    val isRevenueCatConfigured: Boolean = false,
    val productsUnavailable: Boolean = false,
    val monthlyPackage: PremiumPackage = PremiumPackage(
        id = MonthlyProductId,
        title = "Monthly",
        price = "€3.99 / month",
        helperText = "Flexible access to more AI help and full Premium Study Packs."
    ),
    val yearlyPackage: PremiumPackage = PremiumPackage(
        id = YearlyProductId,
        title = "Yearly",
        price = "€24.99 / year",
        helperText = "Best value for steady weekly practice."
    ),
    val message: String? = null,
    val debugUnavailableReason: String? = null,
    val managementUrl: String? = null
) {
    val isPremiumActive: Boolean = entitlement == PremiumEntitlement.Premium
    val canPurchaseMonthly: Boolean = monthlyPackage.revenueCatPackage != null && !isPurchasing
    val canPurchaseYearly: Boolean = yearlyPackage.revenueCatPackage != null && !isPurchasing
}

const val PremiumEntitlementId = "premium"
const val DefaultOfferingId = "default"
const val MonthlyPackageId = "monthly"
const val YearlyPackageId = "yearly"
const val AnnualPackageId = "annual"
const val MonthlyProductId = "learnlift_premium_monthly"
const val YearlyProductId = "learnlift_premium_yearly"
