plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.composeunstyled.demo.android"
    compileSdk = libs.versions.android.compileSDK.get().toInt()

    defaultConfig {
        applicationId = "com.composeunstyled.demo.android"
        minSdk = 23
        targetSdk = libs.versions.android.compileSDK.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":composeunstyled-platformtheme"))
    implementation(libs.androidx.activitycompose)

    implementation("androidx.compose.ui:ui:1.11.0-alpha05")
    implementation("androidx.compose.foundation:foundation:1.11.0-alpha05")
    implementation("androidx.compose.animation:animation:1.11.0-alpha05")
    implementation("androidx.compose.ui:ui-graphics:1.11.0-alpha05")
    implementation("androidx.compose.ui:ui-unit:1.11.0-alpha05")
    debugImplementation("androidx.compose.ui:ui-tooling:1.11.0-alpha05")
}
