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

<UnstyledDemo id="typography-default-text-style" />

## Define and apply typography tokens

Use named tokens for styles that appear in more than one place. Tokens give each role in your type scale one source of truth.

```kotlin
val typography = ThemeProperty<TextStyle>("typography")
val title = ThemeToken<TextStyle>("title")
val body = ThemeToken<TextStyle>("body")

val AppTheme = buildThemeV2 {
    properties[typography] = mapOf(
        title to TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        ),
        body to TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
    )
}

@Composable
fun Article() {
    AppTheme {
        Column {
            Text(
                text = "Page title",
                style = Theme[typography][title],
            )
            Text(
                text = "The body text uses its own token.",
                style = Theme[typography][body],
            )
        }
    }
}
```

## Override typography locally

Pass styling properties to `Text()` when only one element changes. Use `ProvideTextStyle` to update the inherited style for a subtree. Text outside the subtree keeps its current style.

```kotlin
Column {
    Text("Standard content")

    Text(
        text = "A bold status message",
        fontWeight = FontWeight.Bold,
    )

    ProvideTextStyle(
        LocalTextStyle.current.copy(
            fontWeight = FontWeight.Bold,
        ),
    ) {
        Text("Important content")
        Text("This also uses the local style")
    }
}
```

For other theme defaults and local overrides, see [Theme Values](theme-values.md).

## API Reference

<ApiReference declaration="com.composeunstyled.Text" />
