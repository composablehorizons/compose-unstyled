---
title: Modal
description: "Modals are the building blocks for components such as dialogs, alerts and modal bottom sheets.  They create their own window and block interaction with the rest of the interface until removed from the composition. They create their own window and block interaction with the rest of the interface until removed from the composition. You can think of a modal as "layer" than a component."
---

<UnstyledDemo id="modal" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-modal")
```

## Basic Example

To create a modal use the `Modal` component.

Modals do not render anything on the screen by default. However, they draw their contents on a separate window.

As soon as a modal becomes active, it will move focus to its contents.

```kotlin
Modal {
    Box(
        modifier = Modifier.padding(10.dp).background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        UnstyledDragIndication(Modifier.width(32.dp).height(4.dp))
    }
}
```

## Code Examples

### Dismiss Modal on `Escape`

Use the `onKeyEvent` parameter to handle key presses.

Note that in order to receive any key events, you need at least one focusable target within your modal (such as
a [Button](button.md)):

```kotlin
var showModal by remember { mutableStateOf(true) }

UnstyledButton(onClick = { showModal = true }) {
    BasicText("Show Modal")
}

if (showModal) {
    Modal(
        onKeyEvent = {
            if (it.key == Key.Escape) {
                if (it.type == KeyEventType.KeyDown) {
                    showModal = false
                }
                true
            }
            false
        }
    ) {
        Box(
            modifier = Modifier.padding(),
            contentAlignment = Alignment.TopCenter
        ) {
            UnstyledButton(onClick = { showModal = false }) {
                BasicText("Dismiss Modal")
            }
        }
    }
}
```

### Styling the system bars

Modals on Android create their own [`Window`](https://developer.android.com/reference/android/view/Window) to render
their contents. To hold its reference you can use the `LocalModalWindow` composition local from within the contents of the modal.

This is handy for when you need to style the status bar and/or navigation bars, while the modal is active.

This API exists only on the Android target:

```kotlin
Modal {
    val window = LocalModalWindow.current

    LaunchedEffect(Unit) {
        // change system bars to transparent
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        // don't forget to update the icons too
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsControllerCompat.isAppearanceLightStatusBars = true
        windowInsetsControllerCompat.isAppearanceLightNavigationBars = true
    }
    BasicText("Transparent bars. So cool 😎 ", modifier = Modifier.navigationBarsPadding())
}
```
