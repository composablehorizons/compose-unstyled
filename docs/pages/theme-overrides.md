---
title: Theme Overrides
description: Learn how to override your themes for advanced customizations.
---

## What are theme overrides?

Theme overrides allow you to override your theme's default styling for a group of components at once.

This is handy for when:

- You want to style a group of components as a group, instead of styling each one individually
- You are responsible for styling a piece of UI you do not control (i.e., you are building slot-based components)

## Styling a group of components

Every [component](components.md) in Unstyled is themable. They will make use of the current theme's [default content color](custom-themes.md#content-color) and [default text style](custom-themes.md#text-style) to render their content.

Instead of styling each component individually:

```kotlin
Column {
    BasicText("Less than 10 minutes remaining")
    UnstyledIcon(Lucide.AlarmClock, contentDescription = null, tint = Color.Red.copy(alpha = 0.6f))
    BasicText("You are running out of time!", style = TextStyle(color = Color.Red.copy(alpha = 0.6f)))
}
```

use the `ProvideContentColor` to override the content color used:

```kotlin
Column {
    BasicText("Less than 10 minutes remaining")
    ProvideContentColor(Color.Red.copy(alpha = 0.6f)) {
        UnstyledIcon(Lucide.AlarmClock, contentDescription = null) // automatically picks up the red color
        BasicText("You are running out of time!") // automatically picks up the red color
    }
}
```

Both the Icon and Text components automatically inherit the provided color without needing explicit styling parameters.

`ProvideTextStyle` is also available for overriding the text style used.

If you need to override an entire theme use `ThemeOverride` which allows to override specific theme token values.

## Styling slot-based components

You can use theme overrides to provide a specific styling to the content of your slots:

```kotlin
@Composable
fun Card(
    contentColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier.background(backgroundColor)) {
        ProvideContentColor(contentColor) {
            content()
        }
    }
}
```

This way the `Card` component is responsible for styling itself and its content. Any [component](components.md) passed to the `content` slot will be styled accordingly.
