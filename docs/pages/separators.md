---
title: Separators
description: Horizontal and vertical separators with caller-defined color and thickness.
---

<UnstyledDemo id="separators" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-separators")
```

## Anatomy

```kotlin
UnstyledHorizontalSeparator(color = Color.Black)

UnstyledVerticalSeparator(color = Color.Black)
```

## Concepts

- `UnstyledHorizontalSeparator` renders a horizontal line.
- `UnstyledVerticalSeparator` renders a vertical line.

## Code Examples

### Changing separator thickness

Use the `thickness` parameter to change the line thickness:

```kotlin
UnstyledHorizontalSeparator(
  color = Color.Black,
  thickness = 2.dp,
)
```

<ApiReference id="separators" />
