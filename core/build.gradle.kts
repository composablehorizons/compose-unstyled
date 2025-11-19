@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

val publishGroupId = "com.composables"
val publishVersion = libs.versions.unstyled.get()
val githubUrl = "github.com/composablehorizons/compose-unstyled"
val projectUrl = "https://composeunstyled.com"

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
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
            baseName = "ComposablesCore"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Core module is now just an alias for composeunstyled
                api(project(":composeunstyled"))
            }
        }
    }
}

android {
    namespace = "com.composables.core"
    compileSdk = libs.versions.android.compileSDK.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSDK.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

group = publishGroupId
version = publishVersion

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(groupId = publishGroupId, artifactId = "core", version = publishVersion)

    pom {
        name.set("Compose Unstyled")
        description.set("Compose Unstyled is a set of foundational components for building high-quality, accessible design systems in Compose Multiplatform.")
        url.set(projectUrl)

        licenses {
            license {
                name.set("MIT License")
                url.set("https://${githubUrl}/blob/main/LICENSE")
            }
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://${githubUrl}/issues")
        }

        developers {
            developer {
                id.set("composablehorizons")
                name.set("Composable Horizons")
                email.set("alex@composablesui.com")
            }
        }

        scm {
            connection.set("scm:git:${githubUrl}.git")
            developerConnection.set("scm:git:ssh://${githubUrl}.git")
            url.set("https://${githubUrl}/tree/main")
        }
    }
}
