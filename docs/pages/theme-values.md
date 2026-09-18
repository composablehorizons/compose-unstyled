---
title: Theme Values
description: Set theme defaults and override values for a subtree of your Compose UI.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-theming:{{compose_unstyled_version}}")
```

## Theme defaults

A theme provides default values to its content through composition locals. Content chooses whether to read those values.

`defaultContentColor` provides `LocalContentColor`. [Text](typography.md) uses it when no other text color is set.
`defaultTextStyle` provides `LocalTextStyle`. `defaultIndication` provides `LocalIndication`. `defaultTextSelectionColors`
provides `LocalTextSelectionColors`.

```kotlin
val AppTheme = buildThemeV2 {
    defaultContentColor = Color(0xFF0C0A09)
    defaultTextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    )
    defaultIndication = rememberColoredIndication(
        hoveredColor = Color.White.copy(alpha = 0.3f),
        pressedColor = Color.White.copy(alpha = 0.5f),
        focusedColor = Color.Black.copy(alpha = 0.1f),
    )
    defaultTextSelectionColors = TextSelectionColors(
        handleColor = Color.Blue,
        backgroundColor = Color.Blue.copy(alpha = 0.4f),
    )
}
```

## Override values locally

Use `ProvideContentColor` and `ProvideTextStyle` to replace the theme defaults for a subtree. Content outside that subtree
keeps the theme defaults.

```kotlin
Column {
    Text("Standard content")

    ProvideContentColor(Color.Red.copy(alpha = 0.6f)) {
        ProvideTextStyle(TextStyle(fontWeight = FontWeight.Bold)) {
            Text("Important content")
        }
    }
}
```

## API Reference

<ApiReference declaration="com.composeunstyled.theme.buildThemeV2" />
<ApiReference declaration="com.composeunstyled.ProvideContentColor" />
<ApiReference declaration="com.composeunstyled.ProvideTextStyle" />
