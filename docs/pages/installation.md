---
title: Installation
description: Learn how to use Compose Unstyled in a new or existing projects.
---

## Add to an existing project

### Add Maven Central

We distribute Compose Unstyled via Maven Central. Maven Central is the most popular repository for Kotlin packages and
it should be included in your list of repositories
out of the box.

To make sure you have it, check your `settings.gradle.kts`:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral() // <- Add this
    }
}

```

### Add to Jetpack Compose project

Add the dependency in your project, and set JVM version to 17:

```kotlin title="app/build.gradle.kts"
// app/build.gradle.kts
android {
    kotlinOptions {
        jvmTarget = "17" // <- Update this
    }
    compileOptions {
        // make sure these are set to VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // keep the rest the same
}

dependencies {
    // adds theming APIs
    implementation("com.composables:composeunstyled-theming")

    // add the component modules you use
    implementation("com.composables:composeunstyled-button")
    implementation("com.composables:composeunstyled-text-field")

    // adds themes for native look and feel
    implementation("com.composables:composeunstyled-platformtheme")
}
```

### Add to Compose Multiplatform project

For Compose Multiplatform apps:

```kotlin title="composeApp/build.gradle.kts"
// composeApp/build.gradle.kts
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17) // <- Update this
        }
    }
    sourceSets {
        commonMain.dependencies {
            // adds theming APIs
            implementation("com.composables:composeunstyled-theming")

            // add the component modules you use
            implementation("com.composables:composeunstyled-button")
            implementation("com.composables:composeunstyled-text-field")

            // adds themes for native look and feel
            implementation("com.composables:composeunstyled-platformtheme")
        }
    }
}
```
