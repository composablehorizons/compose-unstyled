import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotreload)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
}

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(17)
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                val rootDirPath = project.rootDir.path
                val projectDirPath = project.projectDir.path
                outputPath = File("$rootDirPath/docs/demo")
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }

            }
        }
        binaries.executable()
    }

    jvm("desktop")

    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
        }
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(project(":core"))
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta01")
            implementation(libs.composables.icons.lucide)

        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material")
                    exclude("org.jetbrains.compose.material3")
                }
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activitycompose)
                // Material 3 XML themes
                implementation("com.google.android.material:material:1.12.0")
                // Material 3 compose used for the ripple effect
                implementation(compose.material3)
            }
        }
    }
}

compose.experimental {
    web.application {}
}

compose.desktop {
    application {
        mainClass = "com.composeunstyled.demo.MainKt"
    }
}

android {
    namespace = "com.composeunstyled.demo"
    compileSdk = libs.versions.android.compileSDK.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSDK.get().toInt()
        targetSdk = libs.versions.android.compileSDK.get().toInt()
        applicationId = "com.composeunstyled.demo"
        versionCode = 1
        versionName = "1.0.0"
    }
}