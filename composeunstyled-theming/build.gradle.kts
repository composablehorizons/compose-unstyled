@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    jvm()

    wasmJs {
        browser()
    }

    js {
        browser()
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeUnstyledTheming"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                api(projects.internalShared)
            }
        }

        androidMain.dependencies {
            implementation(libs.androidx.activitycompose)
        }

        applyDefaultHierarchyTemplate {
            common {
                group("cmp") {
                    withJvm()
                    withIos()
                    withWasmJs()
                    withJs()
                }

                group("web") {
                    withWasmJs()
                    withJs()
                }
            }
        }
    }
}

android {
    namespace = "com.composeunstyled.theme"
    compileSdk = libs.versions.android.compileSDK.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSDK.get().toInt()
    }
}
