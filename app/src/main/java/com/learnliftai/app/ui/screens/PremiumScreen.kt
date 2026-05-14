package com.learnliftai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.learnliftai.app.ui.components.GradientHeroCard
import com.learnliftai.app.ui.components.LearnLiftCard
import com.learnliftai.app.ui.components.PrimaryActionButton
import com.learnliftai.app.ui.components.SecondaryActionButton
import com.learnliftai.app.ui.components.SectionHeader
import com.learnliftai.app.ui.components.TopicChip
import com.learnliftai.app.ui.theme.LearnLiftSpacing

@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LearnLiftSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.contentGap)
    ) {
        PremiumHero()
        PremiumBenefits()
        PricingPreview()
        BillingNotice()
        PrimaryActionButton(
            text = "Coming soon",
            onClick = onBack,
            enabled = false
        )
        SecondaryActionButton(
            text = "Maybe later",
            onClick = onBack
        )
    }
}

@Composable
private fun PremiumHero() {
    GradientHeroCard {
        TopicChip(
            text = "Premium coming soon",
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
private fun PremiumBenefits() {
    SectionHeader(
        title = "Premium benefits",
        subtitle = "Planned upgrades for deeper, more guided practice."
    )
    LearnLiftCard {
        PremiumBenefitRow("AI-powered answer explanations")
        PremiumBenefitRow("Personalized 7-day study plans")
        PremiumBenefitRow("Unlimited quizzes and daily sessions")
        PremiumBenefitRow("Full study packs")
        PremiumBenefitRow("Advanced progress insights")
    }
}

@Composable
private fun PricingPreview() {
    SectionHeader(
        title = "Pricing preview",
        subtitle = "Placeholder pricing for future planning."
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(LearnLiftSpacing.smallGap)
    ) {
        PricingCard(
            plan = "Monthly",
            price = "€3.99",
            helperText = "Flexible access"
        )
        PricingCard(
            plan = "Yearly",
            price = "€24.99",
            helperText = "Best planned value"
        )
    }
}

@Composable
private fun PricingCard(
    plan: String,
    price: String,
    helperText: String,
    modifier: Modifier = Modifier
) {
    LearnLiftCard(
        modifier = modifier,
        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f)
    ) {
        Text(
            text = plan,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = price,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = helperText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun BillingNotice() {
    LearnLiftCard(
        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.26f),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = "Billing not enabled in this build",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LearnLiftSpacing.smallGap))
        Text(
            text = "This screen is a preview only. It does not start purchases, save subscription status, or unlock and lock features.",
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
