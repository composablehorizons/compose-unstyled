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
            baseName = "ComposeUnstyledPrimitives"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                api(projects.internalShared)
                api(projects.composeunstyledTheming)
            }
        }

        androidMain.dependencies {
            implementation(libs.androidx.activitycompose)
            implementation(libs.androidx.window)
        }

        jvmMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
        }

        webMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.compose.test)
            implementation(libs.androidx.compose.test.manifest)
            implementation(libs.androidx.espresso)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        val jvmTest by getting

        jvmTest.dependencies {
            implementation(compose.desktop.uiTestJUnit4)
            implementation(libs.assertj.core)
            implementation(compose.desktop.currentOs) {
                exclude(compose.material)
                exclude(compose.material)
            }
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
    namespace = "com.composeunstyled.primitives"
    compileSdk = libs.versions.android.compileSDK.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSDK.get().toInt()
    }
}
