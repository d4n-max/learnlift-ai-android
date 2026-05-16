plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.learnliftai.app"
    compileSdk = 35
    val aiCoachUrl = providers
        .gradleProperty("SUPABASE_AI_COACH_URL")
        .orElse("https://YOUR_PROJECT_REF.supabase.co/functions/v1/ai-coach")
        .get()
    val revenueCatPublicApiKey = providers
        .gradleProperty("REVENUECAT_PUBLIC_API_KEY")
        .orElse("REVENUECAT_PUBLIC_API_KEY_HERE")
        .get()

    defaultConfig {
        applicationId = "com.learnliftai.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "0.1.2"
        buildConfigField("String", "SUPABASE_AI_COACH_URL", "\"$aiCoachUrl\"")
        buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY", "\"$revenueCatPublicApiKey\"")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui:1.6.6")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.6")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.revenuecat.purchases:purchases:9.23.1")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.6")
}
