---
title: Button
description: A button component for custom button styles.
---

<UnstyledDemo id="button" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-button")
```

## Anatomy

```kotlin
UnstyledButton(onClick = onClick) {
}
```

## Concepts

- `UnstyledButton` represents the clickable button surface.

## Accessibility

`UnstyledButton` uses `Role.Button` by default. Enter and Space activate it.

## Code Examples

### Disabling a button

Use the `enabled` parameter to prevent button activation:

```kotlin
UnstyledButton(
  enabled = false,
  onClick = { submit() },
) {
  BasicText("Submit")
}
```

<ApiReference id="button" />
