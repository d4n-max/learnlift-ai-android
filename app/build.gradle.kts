import org.gradle.api.provider.ProviderFactory
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.isFile) {
        localPropertiesFile.inputStream().use(::load)
    }
}

data class ConfigValue(
    val value: String,
    val source: String
) {
    val isPlaceholder: Boolean
        get() = value in PlaceholderRevenueCatValues || value.endsWith("_HERE")

    val startsWithTest: Boolean
        get() = value.startsWith("test_")
}

fun String?.cleanConfigValue(): String? = this?.trim()?.takeIf { it.isNotBlank() }

fun ProviderFactory.gradlePropertyOrEnvOrLocal(name: String, fallback: String): ConfigValue {
    val gradleValue = gradleProperty(name).orNull.cleanConfigValue()
    if (gradleValue != null) return ConfigValue(gradleValue, "gradle property")

    val envValue = environmentVariable(name).orNull.cleanConfigValue()
    if (envValue != null) return ConfigValue(envValue, "environment variable")

    val localValue = localProperties.getProperty(name).cleanConfigValue()
    if (localValue != null) return ConfigValue(localValue, "local.properties")

    return ConfigValue(fallback, "debug placeholder")
}

fun requireReleaseRevenueCatKey(config: ConfigValue) {
    if (config.value.isBlank() || config.isPlaceholder) {
        throw GradleException(
            "Release builds require REVENUECAT_ANDROID_PUBLIC_API_KEY from a Gradle property, " +
                "environment variable, or local.properties."
        )
    }
    if (config.startsWithTest) {
        throw GradleException(
            "Release builds must use REVENUECAT_ANDROID_PUBLIC_API_KEY, not a RevenueCat Test Store key."
        )
    }
}

fun logRevenueCatConfig(label: String, config: ConfigValue) {
    logger.lifecycle(
        "$label RevenueCat key source=${config.source}, " +
            "startsWithTest=${config.startsWithTest}, " +
            "isPlaceholder=${config.isPlaceholder}"
    )
}

val PlaceholderRevenueCatValues = setOf(
    "REVENUECAT_PUBLIC_API_KEY_HERE",
    "REVENUECAT_ANDROID_PUBLIC_API_KEY_HERE",
    "REVENUECAT_TEST_STORE_API_KEY_HERE"
)

android {
    namespace = "com.learnliftai.app"
    compileSdk = 35
    val aiCoachUrl = providers
        .gradleProperty("SUPABASE_AI_COACH_URL")
        .orElse("https://hfeyfsqfggtajowlaeap.supabase.co/functions/v1/ai-coach")
        .get()
    val revenueCatAndroidPublicApiKey = providers
        .gradlePropertyOrEnvOrLocal(
            name = "REVENUECAT_ANDROID_PUBLIC_API_KEY",
            fallback = "REVENUECAT_ANDROID_PUBLIC_API_KEY_HERE"
        )
    val revenueCatTestStoreApiKey = providers
        .gradlePropertyOrEnvOrLocal(
            name = "REVENUECAT_TEST_STORE_API_KEY",
            fallback = "REVENUECAT_TEST_STORE_API_KEY_HERE"
        )
    val useRevenueCatTestStore = providers
        .gradlePropertyOrEnvOrLocal(
            name = "USE_REVENUECAT_TEST_STORE",
            fallback = "false"
        )
    val useRevenueCatTestStoreValue = useRevenueCatTestStore.value.equals("true", ignoreCase = true)

    logRevenueCatConfig("Android public", revenueCatAndroidPublicApiKey)
    logRevenueCatConfig("Test Store", revenueCatTestStoreApiKey)
    logger.lifecycle(
        "USE_REVENUECAT_TEST_STORE source=${useRevenueCatTestStore.source}, " +
            "enabled=$useRevenueCatTestStoreValue"
    )

    gradle.taskGraph.whenReady {
        val releaseTaskRequested = allTasks.any { task ->
            task.path.contains("Release") || task.name.contains("Release")
        }
        if (releaseTaskRequested) {
            requireReleaseRevenueCatKey(revenueCatAndroidPublicApiKey)
            logRevenueCatConfig("Release selected", revenueCatAndroidPublicApiKey)
        }
    }

    defaultConfig {
        applicationId = "com.learnliftai.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 11
        versionName = "0.3.2"
        buildConfigField("String", "SUPABASE_AI_COACH_URL", "\"$aiCoachUrl\"")
        buildConfigField("String", "REVENUECAT_ANDROID_PUBLIC_API_KEY", "\"${revenueCatAndroidPublicApiKey.value}\"")
        buildConfigField("String", "REVENUECAT_TEST_STORE_API_KEY", "\"${revenueCatTestStoreApiKey.value}\"")
        buildConfigField("String", "REVENUECAT_ANDROID_PUBLIC_API_KEY_SOURCE", "\"${revenueCatAndroidPublicApiKey.source}\"")
        buildConfigField("String", "REVENUECAT_TEST_STORE_API_KEY_SOURCE", "\"${revenueCatTestStoreApiKey.source}\"")
    }

    buildTypes {
        debug {
            val screenshotDemoStudyPlan = providers
                .gradlePropertyOrEnvOrLocal(
                    name = "LEARNLIFT_SCREENSHOT_DEMO_STUDY_PLAN",
                    fallback = "false"
                )
                .value
                .equals("true", ignoreCase = true)
            val selectedRevenueCatKey = if (useRevenueCatTestStoreValue) {
                revenueCatTestStoreApiKey
            } else {
                revenueCatAndroidPublicApiKey
            }
            logRevenueCatConfig("Debug selected", selectedRevenueCatKey)

            buildConfigField("Boolean", "SCREENSHOT_DEMO_STUDY_PLAN", screenshotDemoStudyPlan.toString())
            buildConfigField("Boolean", "USE_REVENUECAT_TEST_STORE", useRevenueCatTestStoreValue.toString())
            buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY", "\"${selectedRevenueCatKey.value}\"")
            buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY_SOURCE", "\"${selectedRevenueCatKey.source}\"")
        }

        release {
            buildConfigField("Boolean", "SCREENSHOT_DEMO_STUDY_PLAN", "false")
            buildConfigField("Boolean", "USE_REVENUECAT_TEST_STORE", "false")
            buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY", "\"${revenueCatAndroidPublicApiKey.value}\"")
            buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY_SOURCE", "\"${revenueCatAndroidPublicApiKey.source}\"")
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
    implementation("com.google.android.play:review-ktx:2.0.2")
    implementation(platform("com.google.firebase:firebase-bom:34.14.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.revenuecat.purchases:purchases:10.6.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.6")
    testImplementation("junit:junit:4.13.2")
}
