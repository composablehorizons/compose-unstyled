import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(project(":composeunstyled-primitives"))
                implementation(libs.androidx.activitycompose)
                implementation(libs.composables.ripple)
            }
        }
    }
}

android {
    namespace = "com.composeunstyled.demo.systemui"
    compileSdk = libs.versions.android.compileSDK.get().toInt()

    defaultConfig {
        applicationId = "com.composeunstyled.demo.systemui"
        minSdk = 23
        targetSdk = libs.versions.android.compileSDK.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
}
