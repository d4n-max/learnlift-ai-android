import org.gradle.api.provider.ProviderFactory

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

fun ProviderFactory.gradlePropertyOrEnv(name: String, fallback: String) =
    gradleProperty(name)
        .orElse(environmentVariable(name))
        .orElse(fallback)

android {
    namespace = "com.learnliftai.app"
    compileSdk = 35
    val aiCoachUrl = providers
        .gradleProperty("SUPABASE_AI_COACH_URL")
        .orElse("https://hfeyfsqfggtajowlaeap.supabase.co/functions/v1/ai-coach")
        .get()
    val revenueCatAndroidPublicApiKey = providers
        .gradlePropertyOrEnv(
            name = "REVENUECAT_ANDROID_PUBLIC_API_KEY",
            fallback = "REVENUECAT_ANDROID_PUBLIC_API_KEY_HERE"
        )
        .get()
    val revenueCatTestStoreApiKey = providers
        .gradlePropertyOrEnv(
            name = "REVENUECAT_TEST_STORE_API_KEY",
            fallback = "REVENUECAT_TEST_STORE_API_KEY_HERE"
        )
        .get()
    val useRevenueCatTestStore = providers
        .gradlePropertyOrEnv(
            name = "USE_REVENUECAT_TEST_STORE",
            fallback = "false"
        )
        .map { it.equals("true", ignoreCase = true) }
        .get()

    defaultConfig {
        applicationId = "com.learnliftai.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "0.2.0"
        buildConfigField("String", "SUPABASE_AI_COACH_URL", "\"$aiCoachUrl\"")
        buildConfigField("String", "REVENUECAT_ANDROID_PUBLIC_API_KEY", "\"$revenueCatAndroidPublicApiKey\"")
        buildConfigField("String", "REVENUECAT_TEST_STORE_API_KEY", "\"$revenueCatTestStoreApiKey\"")
    }

    buildTypes {
        debug {
            val selectedRevenueCatKey = if (useRevenueCatTestStore) {
                revenueCatTestStoreApiKey
            } else {
                revenueCatAndroidPublicApiKey
            }

            buildConfigField("Boolean", "USE_REVENUECAT_TEST_STORE", useRevenueCatTestStore.toString())
            buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY", "\"$selectedRevenueCatKey\"")
        }

        release {
            if (revenueCatAndroidPublicApiKey.startsWith("test_")) {
                throw GradleException(
                    "Release builds must use REVENUECAT_ANDROID_PUBLIC_API_KEY, not a RevenueCat Test Store key."
                )
            }

            buildConfigField("Boolean", "USE_REVENUECAT_TEST_STORE", "false")
            buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY", "\"$revenueCatAndroidPublicApiKey\"")
        }
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
    implementation("com.revenuecat.purchases:purchases:10.6.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.6")
}
