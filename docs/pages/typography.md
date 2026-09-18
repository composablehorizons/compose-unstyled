---
title: Typography
description: Set default text styles in your theme and override them where your Compose UI needs them.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-theming:2.9.2")
```

## Set default typography in your theme

Set `defaultTextStyle` when you create your theme. Every `Text()` inside the theme uses this style by default when you do not pass a `style`.

```kotlin
import com.composeunstyled.Text

val AppTheme = buildThemeV2 {
    defaultTextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
fun App() {
    AppTheme {
        Column {
            Text("This text uses the theme style")
            Text("So does this text")
        }
    }
}
```

<UnstyledDemo id="typography-default-text-style" />

## Override typography locally

Use `ProvideTextStyle` to replace the theme's `defaultTextStyle` for a part of your UI. `Text()` calls inside its content use the provided style. Content outside keeps the theme default.

```kotlin
Column {
    Text("Standard content")

    ProvideTextStyle(
        TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        ),
    ) {
        Text("Important content")
        Text("This also uses the local style")
    }
}
```

For other theme defaults and local overrides, see [Theme Values](theme-values.md).

## Override one Text

Pass a style or individual properties to change one `Text()` without changing nearby text.

```kotlin
Text(
    text = "Section title",
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
    textDecoration = TextDecoration.Underline,
)

Text(
    text = "Status message",
    style = TextStyle(color = Color.Red),
)
```

## Handle text overflow

Set `maxLines` and `overflow` when text must fit in a limited space.

```kotlin
Text(
    text = "This is a very long text that might overflow",
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
)
```

## API Reference

<ApiReference declaration="com.composeunstyled.Text" />
