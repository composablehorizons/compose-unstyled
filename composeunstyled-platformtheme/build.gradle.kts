@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)

    id("maven-publish")
    id("signing")
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
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
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

    js {
        browser()
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeUnstyledPlatformTheme"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate {
        common {
            group("nonAndroid") {
                withIos()
                withJvm()
                withJs()
                withWasmJs()
            }

            group("nonWeb") {
                withIos()
                withAndroidTarget()
                withJvm()
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.foundation)
            api(projects.composeunstyledTheming)
            implementation(compose.components.resources)
        }
        androidMain.dependencies {
            implementation(libs.composables.ripple)
        }
    }
}

android {
    namespace = "com.composeunstyled.platformtheme"
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

val javadocJar = tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

group = publishGroupId
version = publishVersion


afterEvaluate {
    publishing {
        publications {
            withType<MavenPublication> {
                artifact(javadocJar)

                pom {
                    name.set("Compose Unstyled Platform Theme")
                    description.set("Theme with native look and feel for Compose Multiplatform.")
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
        }
        // TODO: remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
        project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
            dependsOn(project.tasks.withType(Sign::class.java))
        }
    }
}

signing {
    useInMemoryPgpKeys(
        getLocalProperty("signing.keyId") ?: System.getenv("SIGNING_KEY_ID"),
        getLocalProperty("signing.key") ?: System.getenv("SIGNING_KEY"),
        getLocalProperty("signing.password") ?: System.getenv("SIGNING_PASSWORD"),
    )
    sign(publishing.publications)
}
