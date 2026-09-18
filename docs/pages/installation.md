---
title: Installation
description: Learn how to use Compose Unstyled in a new or existing projects.
---

## Quick start

Compose Unstyled is distributed via Maven Central, the most trusted source of sharing Kotlin packages. Ensure you have it enabled in your repository sources first:

```kotlin title="settings.gradle.kts"
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

```kotlin tabbed
// tab: Jetpack Compose
android {
    kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("com.composables:composeunstyled:{{compose_unstyled_version}}")
}

// tab: Compose Multiplatform
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation("com.composables:composeunstyled:{{compose_unstyled_version}}")
        }
    }
}
```

## Modules

Compose Unstyled is modular. Add focused modules when you do not need the full dependency.

| If you need | Add |
| --- | --- |
| All components and theming APIs | `composeunstyled` |
| Every unstyled component | `composeunstyled-primitives` |
| Theming APIs | `composeunstyled-theming` |
| Specific components, such as Button | `composeunstyled-button` |
| Opinionated themes per platform | `composeunstyled-platformtheme` |

Replace `composeunstyled` in Quick Start with the dependencies that fit your app.

```kotlin
implementation("com.composables:composeunstyled-button:{{compose_unstyled_version}}")
implementation("com.composables:composeunstyled-text-field:{{compose_unstyled_version}}")
implementation("com.composables:composeunstyled-theming:{{compose_unstyled_version}}")
```
