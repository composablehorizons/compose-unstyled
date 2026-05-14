---
title: Scrim
description: A modal scrim that follows the modal transition state.
---

<UnstyledDemo id="scrim" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-scrim")
```

## Anatomy

```kotlin
Modal(state = state) {
  Scrim()
}
```

## Concepts

- `Scrim` renders a modal overlay inside `Modal`.
- `Scrim` follows the modal transition state.

## Code Examples

### Adding a scrim to a modal

Use the `Scrim` component inside `Modal` content:

```kotlin
Modal(state = state) {
  Scrim()
}
```

### Changing scrim color

Use the `scrimColor` parameter to change the overlay color:

```kotlin
Modal(state = state) {
  Scrim(scrimColor = Color.Black.copy(alpha = 0.4f))
}
```

### Animating a scrim

Use the `enter` and `exit` parameters to animate the scrim with the modal state:

```kotlin
Modal(state = state) {
  Scrim(
    enter = fadeIn(),
    exit = fadeOut(),
  )
}
```

<ApiReference id="scrim" />
