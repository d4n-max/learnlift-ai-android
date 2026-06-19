package com.learnliftai.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learnliftai.app.BuildConfig
import com.learnliftai.app.data.billing.PremiumEntitlement
import com.learnliftai.app.data.billing.PremiumPackage
import com.learnliftai.app.data.billing.PremiumUiState
import com.learnliftai.app.ui.components.GradientHeroCard
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.TopicChip
import com.learnliftai.app.ui.theme.LearnLiftCorners
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun PremiumScreen(
    premiumUiState: PremiumUiState,
    onRefreshPremium: () -> Unit,
    onPurchasePackage: (PremiumPackage, Activity) -> Unit,
    onRestorePurchases: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val activity = context.findActivity()
    val managementUrl = premiumUiState.managementUrl
    var selectedPackageId by remember {
        mutableStateOf(premiumUiState.yearlyPackage.id)
    }
    val selectedPackage = if (selectedPackageId == premiumUiState.monthlyPackage.id) {
        premiumUiState.monthlyPackage
    } else {
        premiumUiState.yearlyPackage
    }

    LaunchedEffect(Unit) {
        onRefreshPremium()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        PremiumHero(isPremiumActive = premiumUiState.isPremiumActive)
        PremiumStatus(premiumUiState = premiumUiState)
        PremiumBenefits()
        PricingOptions(
            premiumUiState = premiumUiState,
            selectedPackage = selectedPackage,
            onPackageSelected = { selectedPackageId = it.id }
        )
        PremiumTrustSupport(premiumUiState = premiumUiState)
        if (premiumUiState.productsUnavailable || !premiumUiState.isRevenueCatConfigured) {
            BillingNotice(premiumUiState = premiumUiState)
        }
        PrimaryActionButton(
            text = when {
                premiumUiState.isPremiumActive -> "Premium active"
                premiumUiState.isPurchasing -> "Starting purchase..."
                selectedPackage.revenueCatPackage == null -> "Plans unavailable"
                else -> "Start Premium"
            },
            onClick = {
                if (activity != null) {
                    logStartPremiumTap(selectedPackage)
                    onPurchasePackage(selectedPackage, activity)
                }
            },
            enabled = !premiumUiState.isPremiumActive &&
                !premiumUiState.isPurchasing &&
                selectedPackage.revenueCatPackage != null &&
                activity != null
        )
        SecondaryActionButton(
            text = if (premiumUiState.isRestoring) "Restoring..." else "Restore purchases",
            onClick = onRestorePurchases,
            enabled = !premiumUiState.isRestoring
        )
        if (premiumUiState.isPremiumActive && !managementUrl.isNullOrBlank()) {
            SecondaryActionButton(
                text = "Manage subscription",
                onClick = { uriHandler.openUri(managementUrl) }
            )
        }
        SecondaryActionButton(
            text = "Maybe later",
            onClick = onBack
        )
    }
}

@Composable
private fun PremiumHero(isPremiumActive: Boolean) {
    GradientHeroCard {
        TopicChip(
            text = if (isPremiumActive) "Premium active" else "Premium",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "Study with more AI help every day",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Unlock more explanations, AI quiz feedback, 7-day plans, and full Premium Study Packs when you want deeper practice.",
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PremiumStatus(premiumUiState: PremiumUiState) {
    LearnLiftCard {
        Text(
            text = "Current plan",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (premiumUiState.entitlement == PremiumEntitlement.Premium) "Premium" else "Free",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = premiumUiState.message ?: if (premiumUiState.isPremiumActive) {
                "Premium active. More AI help, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked."
            } else {
                "Core study features remain available while Premium is optional."
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        if (
            BuildConfig.DEBUG &&
            premiumUiState.productsUnavailable &&
            premiumUiState.debugUnavailableReason != null
        ) {
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = "Debug billing reason: ${premiumUiState.debugUnavailableReason}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PremiumBenefits() {
    SectionHeader(
        title = "Premium benefits",
        subtitle = "Unlock more AI support, study plans, and Premium Study Packs."
    )
    LearnLiftCard {
        Text(
            text = "Available with Premium",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        PremiumBenefitRow("More AI Coach explanations for wrong answers")
        PremiumBenefitRow("Higher daily AI limits")
        PremiumBenefitRow("AI Study Review after quizzes")
        PremiumBenefitRow("7-day AI Study Plans")
        PremiumBenefitRow("Full Premium Study Packs")
        PremiumBenefitRow("Smarter support for what to practice next")
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "Premium Study Packs include",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        PremiumBenefitRow("SQL Interview Prep")
        PremiumBenefitRow("QA Advanced")
        PremiumBenefitRow("Automation Testing Basics")
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "Coming soon",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        PremiumBenefitRow("Python Basics")
        PremiumBenefitRow("JavaScript Basics")
        PremiumBenefitRow("Business English")
        PremiumBenefitRow("Technical Interview Prep")
        Spacer(modifier = Modifier.height(LearnLiftSpacing.contentGap))
        Text(
            text = "Expanding later",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        PremiumBenefitRow("Advanced progress insights")
        PremiumBenefitRow("More premium study paths")
        PremiumBenefitRow("Deeper weak-topic coaching")
    }
}

@Composable
private fun PricingOptions(
    premiumUiState: PremiumUiState,
    selectedPackage: PremiumPackage,
    onPackageSelected: (PremiumPackage) -> Unit
) {
    SectionHeader(
        title = "Choose a plan",
        subtitle = if (
            BuildConfig.DEBUG &&
            premiumUiState.productsUnavailable &&
            premiumUiState.debugUnavailableReason != null
        ) {
            "Debug: ${premiumUiState.debugUnavailableReason}"
        } else if (premiumUiState.productsUnavailable) {
            "Premium plans are temporarily unavailable. Please try again later."
        } else {
            "Localized prices are loaded from RevenueCat when available."
        }
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
    ) {
        PricingCard(
            premiumPackage = premiumUiState.monthlyPackage,
            isSelected = selectedPackage.id == premiumUiState.monthlyPackage.id,
            onClick = { onPackageSelected(premiumUiState.monthlyPackage) }
        )
        PricingCard(
            premiumPackage = premiumUiState.yearlyPackage,
            isSelected = selectedPackage.id == premiumUiState.yearlyPackage.id,
            badgeText = "Best value",
            onClick = { onPackageSelected(premiumUiState.yearlyPackage) }
        )
    }
}

@Composable
private fun PremiumTrustSupport(premiumUiState: PremiumUiState) {
    LearnLiftCard {
        Text(
            text = "Trust and support",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Purchases are handled securely by Google Play through RevenueCat. Cancel anytime in Google Play. Your free study tools stay available.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (premiumUiState.isPremiumActive) {
                "Premium active. More AI help, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked."
            } else {
                "Your free learning tools stay available."
            },
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PricingCard(
    premiumPackage: PremiumPackage,
    isSelected: Boolean,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    val accentColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(LearnLiftCorners.card),
        border = BorderStroke(1.dp, accentColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(LearnLiftSpacing.cardPadding)) {
            if (badgeText != null) {
                TopicChip(
                    text = badgeText,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            }
            Text(
                text = premiumPackage.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = premiumPackage.price,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
            Text(
                text = premiumPackage.helperText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Save compared to monthly",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BillingNotice(premiumUiState: PremiumUiState) {
    LearnLiftCard(
        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.26f),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = if (premiumUiState.isRevenueCatConfigured) {
                "Purchases are handled by Google Play through RevenueCat"
            } else {
                "Premium plans are temporarily unavailable"
            },
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (premiumUiState.isRevenueCatConfigured) {
                "Real Google Play prices appear when products are mapped in RevenueCat. If packages are unavailable, purchases stay disabled."
            } else {
                "Premium status could not be refreshed right now. The app remains usable in Free mode."
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PremiumBenefitRow(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private fun logStartPremiumTap(selectedPackage: PremiumPackage) {
    if (!BuildConfig.DEBUG) return

    val revenueCatPackage = selectedPackage.revenueCatPackage
    Log.d(
        PremiumLogTag,
        "start_premium_tap selectedPlan=${selectedPackage.title}, " +
            "packageId=${revenueCatPackage?.identifier}, " +
            "productId=${revenueCatPackage?.product?.id}"
    )
}

private const val PremiumLogTag = "LearnLiftPremium"
