---
title: Checkbox
description: A foundational component used to build checkboxes in Jetpack Compose and Compose Multiplatform. Accessible out of the box and fully renderless, so that you can apply any styling you like.
---

<UnstyledDemo id="checkbox" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-checkbox")
```

## Basic Example

To create a checkbox use the `Checkbox` component. To toggle it you can either click it, or press Enter or Spacebar on your keyboard, while the checkbox is focused.

Depending on its state, the checkbox will show its `checkIcon` or not.

```kotlin
var checked by remember { mutableStateOf(false) }

UnstyledCheckbox(
    checked = checked,
    onCheckedChange = { checked = it },
    shape = RoundedCornerShape(4.dp),
    backgroundColor = Color.White,
    contentColor = Color.Black
) {
    // will be shown if checked
    UnstyledIcon(Check, contentDescription = null)
}
```

## Styling

Every component in Compose Unstyled is renderless. They all handle all UX pattern's logic, internal state, accessibility and keyboard interactions for you, but they do not display any information on the screen.

This is by design so that you can style your components exactly to your needs.

Most of the times, styling is done using `Modifiers` of your choise. However, sometimes this is not enough due to the order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters.


## Code Example

Below is a detailed example from `CheckboxDemo.kt`:

```kotlin
@Composable
fun CheckboxDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))),
        contentAlignment = Alignment.Center
    ) {
        var checked by remember { mutableStateOf(false) }
        UnstyledCheckbox(
            checked = checked,
            onCheckedChange = { checked = it },
            shape = RoundedCornerShape(4.dp),
            backgroundColor = Color.White,
            borderWidth = 1.dp,
            borderColor = Color.Black.copy(0.33f),
            modifier = Modifier.size(24.dp),
            contentDescription = "Add olives"
        ) {
            UnstyledIcon(Check, contentDescription = null)
        }
    }
}
```

## Keyboard Interactions

| Key                                   | Description                                                   |
|---------------------------------------|---------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Toggles the checkbox, triggering its onCheckedChange callback |
| <div class="keyboard-key">Space</div> | Toggles the checkbox, triggering its onCheckedChange callback |

<ApiReference id="checkbox" />
