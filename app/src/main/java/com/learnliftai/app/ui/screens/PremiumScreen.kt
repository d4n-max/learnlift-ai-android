package com.learnliftai.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val activity = context.findActivity()
    var selectedPackage by remember(premiumUiState.monthlyPackage.id, premiumUiState.yearlyPackage.id) {
        mutableStateOf(premiumUiState.yearlyPackage)
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
            onPackageSelected = { selectedPackage = it }
        )
        BillingNotice(premiumUiState = premiumUiState)
        PrimaryActionButton(
            text = when {
                premiumUiState.isPremiumActive -> "Premium active"
                premiumUiState.isPurchasing -> "Starting purchase..."
                else -> "Start Premium"
            },
            onClick = {
                if (activity != null) {
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
            text = "Unlock LearnLift AI Premium",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "Get deeper guidance, more practice, and smarter study plans.",
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
                "Premium is active on this device."
            } else {
                "Core study features remain available while Premium is optional."
            },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PremiumBenefits() {
    SectionHeader(
        title = "Premium benefits",
        subtitle = "Planned upgrades for deeper, more guided practice."
    )
    LearnLiftCard {
        PremiumBenefitRow("AI-powered answer explanations")
        PremiumBenefitRow("AI 7-day study plans")
        PremiumBenefitRow("Unlimited quizzes and daily sessions")
        PremiumBenefitRow("Full study packs")
        PremiumBenefitRow("Advanced progress insights")
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
        subtitle = if (premiumUiState.productsUnavailable) {
            "RevenueCat products are not available yet. Placeholder prices are shown."
        } else {
            "Prices are loaded from RevenueCat."
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
            onClick = { onPackageSelected(premiumUiState.yearlyPackage) }
        )
    }
}

@Composable
private fun PricingCard(
    premiumPackage: PremiumPackage,
    isSelected: Boolean,
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
                "Premium products are not available yet"
            },
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = if (premiumUiState.isRevenueCatConfigured) {
                "If products are not configured in RevenueCat and Google Play yet, purchases may fail gracefully during testing."
            } else {
                "Add a RevenueCat public API key for local or Play testing. The app remains usable in Free mode."
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
