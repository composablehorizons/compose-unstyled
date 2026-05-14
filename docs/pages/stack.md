---
title: Stack
description: A single layout component for horizontal and vertical stacks.
---

<UnstyledDemo id="stack" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-stack")
```

## Anatomy

```kotlin
Stack {
}
```

## Concepts

- `Stack` renders children in one horizontal or vertical layout.
- The `weight()` modifier distributes remaining space in the current stack orientation.

## Code Examples

### Creating a vertical stack

Use the `orientation` parameter to arrange content vertically:

```kotlin
Stack(orientation = StackOrientation.Vertical) {
  BasicText("One")
  BasicText("Two")
}
```

### Adding space between items

Use the `spacing` parameter to add equal space between children:

```kotlin
Stack(spacing = 12.dp) {
  BasicText("One")
  BasicText("Two")
}
```

### Aligning stack content

Use the `mainAxisArrangement` and `crossAxisAlignment` parameters to align children:

```kotlin
Stack(
  mainAxisArrangement = MainAxisArrangement.Center,
  crossAxisAlignment = CrossAxisAlignment.Center,
) {
  BasicText("One")
  BasicText("Two")
}
```

### Weighting stack children

Use the `weight()` modifier inside `Stack` content to distribute remaining space:

```kotlin
Stack {
  BasicText(
    text = "Primary",
    modifier = Modifier.weight(1f),
  )
  BasicText("Secondary")
}
```

<ApiReference id="stack" />
