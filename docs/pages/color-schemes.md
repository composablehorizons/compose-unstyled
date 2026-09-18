---
title: Color Schemes
description: Define light, dark, and custom theme variations for your Jetpack Compose app.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-theming:{{compose_unstyled_version}}")
```

## Define light and dark schemes

Use `buildThemeV2` to define values for each color scheme. When you invoke the theme without choosing a scheme, it
automatically selects `ColorScheme.Light` while the system is in light mode and `ColorScheme.Dark` while it is in dark
mode.

```kotlin
val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("on_background")
val primary = ThemeToken<Color>("primary")

val AppTheme = buildThemeV2 {
    properties[colors] = mapOf(
        primary to Color(0xFF155DFC),
    )

    colorScheme(ColorScheme.Light) {
        properties[colors] = mapOf(
            background to Color(0xFFFAFAFA),
            onBackground to Color(0xFF0C0A09),
        )
    }
    colorScheme(ColorScheme.Dark) {
        properties[colors] = mapOf(
            background to Color(0xFF020617),
            onBackground to Color(0xFFF1F5F9),
        )
    }
}
```

When the system mode changes, the theme recomposes and applies the matching scheme.

Values defined outside a `colorScheme` block are used as fallbacks. In this example, both schemes use the shared
`primary` value and only override the background values that change.

## Choose a scheme explicitly

Pass a `ColorScheme` when applying the theme to choose a scheme yourself. For example, a theme switcher can keep the
selected scheme in state and pass it to the theme:

```kotlin
@Composable
fun App() {
    var colorScheme by remember { mutableStateOf(ColorScheme.Light) }

    AppTheme(colorScheme = colorScheme) {
        Column {
            UnstyledButton(onClick = { colorScheme = ColorScheme.Light }) {
                BasicText("Use light theme")
            }
            UnstyledButton(onClick = { colorScheme = ColorScheme.Dark }) {
                BasicText("Use dark theme")
            }

            AppContent()
        }
    }
}
```

You can also define named schemes beyond light and dark:

```kotlin
val Sepia = ColorScheme("sepia")

val ReaderTheme = buildThemeV2 {
    colorScheme(Sepia) {
        properties[colors] = mapOf(
            background to Color(0xFFF4ECD8),
            onBackground to Color(0xFF433422),
        )
    }
}
```

## Animate scheme changes

By default, scheme changes apply immediately. Set `colorSchemeTransitionSpec` to animate color theme values and the
default content color when the active scheme changes.

```kotlin
val AppTheme = buildThemeV2 {
    colorSchemeTransitionSpec = tween(200)
    // Define color schemes.
}
```

## API Reference

<ApiReference declaration="com.composeunstyled.theme.ColorScheme" />
<ApiReference declaration="com.composeunstyled.theme.buildThemeV2" />
