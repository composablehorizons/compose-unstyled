---
title: Dynamic Themes
description: Create themes that can change over time.
---

## Overview

There are scenarios where you need your themes to automatically update.

One common scenario is styling your app according to system context, such as implementing light and dark modes. Another scenario is when you do not have all available resources ahead of time, and you need to load them from disk (such as loading custom fonts).

Themes built with Unstyled are composable functions and can recompose when one of its properties is updated.

### The `buildTheme` function

Compose Unstyled's `buildTheme` function itself is not a `@Composable` function, but the scope it provides for defining your properties is.

This means that you can call any `@Composable` function, composition locals, or even launch effects in order for using coroutines and load resources that might be blocking.

This enables dynamic themes, which will recompose once their values change.

## Implementing Dark Mode

Compose Foundation comes with a `isSystemInDarkTheme()` function that emits whether the system is in dark mode.

You can use this information to provide a light and dark color scheme respectively when defining your theme:

```kotlin
import androidx.compose.foundation.isSystemInDarkTheme

val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("on_background")

val LightDarkTheme = buildTheme {
    val isDark = isSystemInDarkTheme()
    properties[colors] = if (isDark) {
        // dark palette
        mapOf(
            background to Color(0xFF020617),
            onBackground to Color(0xFFf1f5f9),
        )
    } else {
        // light palette
        mapOf(
            background to Color(0xFFFAFAFA),
            onBackground to Color(0xFF0C0A09),
        )
    }
}
```

That's it. When the system option changes, the scope of the `buildTheme` will recompose, updating the `colors` of your app.

## Loading Resources Asynchronously

Since the `buildTheme` scope is composable, you can load resources that might not be immediately available. For example, loading custom fonts from disk or fetching theme data from a network source:

```kotlin
val typography = ThemeProperty<FontFamily>("typography")
val body = ThemeToken<FontFamily>("body")

val AsyncTheme = buildTheme {
    val scope = rememberCoroutineScope()
    var fontFamily by remember { mutableStateOf<FontFamily?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            fontFamily = loadCustomFontFromDisk()
        }
    }

    properties[typography] = mapOf(
        body to (fontFamily ?: FontFamily.Default)
    )
}
```

The theme will initially use the default font, then automatically update once the custom font loads.
