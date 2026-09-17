---
title: Dynamic Themes
description: Create themes that can change over time.
---

## Overview

Themes can react to changing state and loaded resources. For light and dark variants, see [Color Schemes](color-schemes.md).

Themes built with Unstyled are composable functions and can recompose when one of its properties is updated.

### The `buildThemeV2` function

Compose Unstyled's `buildThemeV2` function itself is not a `@Composable` function, but the scope it provides for defining your properties is.

This means you can call composable functions, read composition locals, and use effects to load resources without
blocking the UI thread.

This enables dynamic themes, which will recompose once their values change.

## Loading resources asynchronously

Since the `buildThemeV2` scope is composable, you can load resources that might not be immediately available. For example, loading custom fonts from disk or fetching theme data from a network source:

```kotlin expandable
val typography = ThemeProperty<FontFamily>("typography")
val body = ThemeToken<FontFamily>("body")

val AsyncTheme = buildThemeV2 {
    var fontFamily by remember { mutableStateOf<FontFamily?>(null) }

    LaunchedEffect(Unit) {
        fontFamily = withContext(Dispatchers.IO) {
            loadCustomFontFromDisk()
        }
    }

    properties[typography] = mapOf(
        body to (fontFamily ?: FontFamily.Default)
    )
}
```

The theme will initially use the default font, then automatically update once the custom font loads.
