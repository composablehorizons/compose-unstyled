---
title: Modal
description: A low-level modal layer for blocking background interaction.
---

<UnstyledDemo id="modal" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-modal")
```

## Anatomy

```kotlin
Modal(state = state) {
  Scrim()
}
```

## Concepts

- `ModalState` controls whether the modal is visible.
- `Modal` renders content in a modal layer.
- `Scrim` renders a modal overlay that follows the modal transition state.
- `modalFragment()` marks content that should keep the modal mounted during transitions.

## Accessibility

Use higher-level components such as `Dialog` or `UnstyledModalBottomSheet` when you need built-in dismiss behavior and semantics.

## Code Examples

### Showing modal content

Use the `rememberModalState()` function to create the modal state:

```kotlin
val state = rememberModalState(initiallyVisible = true)

Modal(state = state) {
  Scrim()
  Box(Modifier.modalFragment()) {
    BasicText("Modal content")
  }
}
```

### Adding a scrim

Use `Scrim` inside `Modal` content to render a ready-made overlay:

```kotlin
Modal(state = state) {
  Scrim(scrimColor = Color.Black.copy(alpha = 0.4f))
}
```

### Closing a modal from Escape

Use the `onKeyEvent` parameter to handle keyboard dismissal:

```kotlin
Modal(
  state = state,
  onKeyEvent = { event ->
    if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
      state.transitionState.targetState = false
      true
    } else {
      false
    }
  },
) {
  Box(Modifier.modalFragment()) {
    BasicText("Modal content")
  }
}
```

<ApiReference id="modal" />
