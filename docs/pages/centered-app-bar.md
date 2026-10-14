---
title: Centered App Bar
description: A centered app bar with title, navigation, and action slots.
---

<UnstyledDemo id="centered-app-bar" />

## Features

- Compose a title, navigation control, and trailing actions without prescribed colors or dimensions.
- Apply window insets and content padding at the bar boundary.
- Keep the title at the bar midpoint even when the side slots have different widths.

## Installation

```kotlin
implementation("com.composables:composeunstyled-centered-app-bar:{{compose_unstyled_version}}")
```

## Composition

```kotlin
UnstyledCenteredAppBar(
  title = { BasicText("Inbox") },
  navigation = { BasicText("Back") },
  actions = { BasicText("Search") },
)
```

## Accessibility

The bar is a layout container. Give interactive navigation and action content accessible labels and semantics at the call site.

## Code Examples

### Add a surface and spacing

```kotlin
UnstyledCenteredAppBar(
  title = { BasicText("Inbox") },
  modifier = Modifier
    .background(Color.White)
    .border(1.dp, Color.Black),
  contentPadding = PaddingValues(16.dp),
  navigation = { BasicText("Back") },
  actions = { BasicText("Search") },
)
```

### Respect system bars

```kotlin
UnstyledCenteredAppBar(
  title = { BasicText("Inbox") },
  modifier = Modifier
    .background(Color.White)
    .border(1.dp, Color.Black),
  windowInsets = WindowInsets.statusBars,
  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
)
```

## API Reference

<ApiReference declaration="com.composeunstyled.UnstyledCenteredAppBar" />
