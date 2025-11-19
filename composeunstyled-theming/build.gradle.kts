@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
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
    namespace = "com.composeunstyled.theme"
    compileSdk = libs.versions.android.compileSDK.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSDK.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

group = publishGroupId
version = publishVersion

mavenPublishing {
    publishToMavenCentral(automaticRelease = true, validateDeployment = false)
    signAllPublications()

    coordinates(publishGroupId, "composeunstyled-theming", publishVersion)

    pom {
        name.set("Compose Unstyled Theming")
        description.set("Theming system for Compose Unstyled - foundational components for building high-quality, accessible design systems in Compose Multiplatform.")
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
