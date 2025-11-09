@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
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

val detektSourceDirs = listOf(
    "src/commonMain/kotlin",
    "src/cmpMain/kotlin",
    "src/androidMain/kotlin",
    "src/jvmMain/kotlin",
    "src/iosMain/kotlin",
    "src/webMain/kotlin"
).map(::file).filter(File::exists)

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files(rootProject.file("detekt.yml")))
    parallel = true
    source.setFrom(detektSourceDirs)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_17.target
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(false)
    }
}
