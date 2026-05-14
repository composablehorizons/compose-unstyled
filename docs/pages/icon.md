---
title: Icon
description: An icon component for tinted painter, bitmap, and vector assets.
---

<UnstyledDemo id="icon" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-icon")
```

## Anatomy

```kotlin
UnstyledIcon(
  imageVector = icon,
  contentDescription = "Favorite",
)
```

## Concepts

- `UnstyledIcon` renders an icon from an `ImageVector`, `Painter`, or `ImageBitmap`.

## Accessibility

Pass a short `contentDescription` for icons that communicate meaning. Use `null` for decorative icons.

## Code Examples

### Tinting an icon

Use the `tint` parameter to apply one color to the icon:

```kotlin
UnstyledIcon(
  imageVector = favoriteIcon,
  contentDescription = "Favorite",
  tint = Color.Red,
)
```

<ApiReference id="icon" />
