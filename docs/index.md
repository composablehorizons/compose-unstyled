# Composables Core

Unstyled, fully accessible Compose Multiplatform UI components that you can customize to your heart's desire.

Available for Compose Desktop, Compose Web (Js/WASM), Jetpack Compose (Android), iOS, and any other platform Compose can run on.

[Show components]

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.4.0")
}
```

### Install ComposeTheme (recommended)

All code samples on this site use the [`ComposeTheme`](https://github.com/composablehorizons/composetheme) library instead of forcing you to use a specific design system. 

This makes it simpler to copy-paste the code without having to worry about hard coded colors or the design system used. 

It is optional but highly recommended to use in your projects:

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:composetheme:1.2.0-alpha")
}
```