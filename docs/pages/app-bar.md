---
title: App Bar
description: A leading-aligned app bar with title, navigation, and action slots.
---

<UnstyledDemo id="app-bar" />

## Features

- Compose a title, navigation control, and trailing actions without prescribed colors or dimensions.
- Apply window insets and content padding at the bar boundary.

## Installation

```kotlin
implementation("com.composables:composeunstyled-app-bar:{{compose_unstyled_version}}")
```

## Composition

```kotlin
UnstyledAppBar(
  title = { BasicText("Messages") },
  navigation = {
    UnstyledButton(onClick = onBack) {
      UnstyledIcon(Lucide.ArrowLeft, contentDescription = "Back")
    }
  },
  actions = {
    UnstyledButton(onClick = onFavorite) {
      UnstyledIcon(Lucide.Heart, contentDescription = "Favorite")
    }
  },
)
```

## Accessibility

The bar is a layout container. Give interactive navigation and action content accessible labels and semantics at the call site.

## Code Examples

### Add a surface and spacing

```kotlin
UnstyledAppBar(
  title = { BasicText("Messages") },
  modifier = Modifier
    .background(Color.White)
    .border(1.dp, Color.Black),
  contentPadding = PaddingValues(16.dp),
  navigation = {
    UnstyledButton(onClick = onBack) {
      UnstyledIcon(Lucide.ArrowLeft, contentDescription = "Back")
    }
  },
  actions = {
    UnstyledButton(onClick = onFavorite) {
      UnstyledIcon(Lucide.Heart, contentDescription = "Favorite")
    }
  },
)
```

### Respect system bars

```kotlin
UnstyledAppBar(
  title = { BasicText("Messages") },
  modifier = Modifier
    .background(Color.White)
    .border(1.dp, Color.Black),
  windowInsets = WindowInsets.statusBars,
  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
)
```

## API Reference

<ApiReference declaration="com.composeunstyled.UnstyledAppBar" />
