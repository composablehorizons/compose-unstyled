---
title: Checkbox
description: A component for creating checkboxes for user selection.
---

# Checkbox

A foundational component used to build checkboxes in Compose Multiplatform.

Accessible out of the box and fully renderless, so that you can apply any styling you like.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../checkbox-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>


## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.30.0")
}
```

## Basic Example

To create a checkbox use the `Checkbox` component. To toggle it you can either click it, or press Enter or Spacebar on your keyboard, while the checkbox is focused.

Depending on its state, the checkbox will show its `checkIcon` or not.

```kotlin
var checked by remember { mutableStateOf(false) }

Checkbox(
    checked = checked,
    onCheckedChange = { checked = it },
    shape = RoundedCornerShape(4.dp),
    backgroundColor = Color.White,
    contentColor = Color.Black
) {
    // will be shown if checked
    Icon(Check, contentDescription = null)
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
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            shape = RoundedCornerShape(4.dp),
            backgroundColor = Color.White,
            borderWidth = 1.dp,
            borderColor = Color.Black.copy(0.33f),
            modifier = Modifier.size(24.dp),
            contentDescription = "Add olives"
        ) {
            Icon(Check, contentDescription = null)
        }
    }
}
```

## Keyboard Interactions

| Key                                   | Description                                                   |
|---------------------------------------|---------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Toggles the checkbox, triggering its onCheckedChange callback |
| <div class="keyboard-key">Space</div> | Toggles the checkbox, triggering its onCheckedChange callback |

## Component API

| Parameter           | Description                                                     |
|---------------------|-----------------------------------------------------------------|
| `checked`           | Whether the checkbox is checked.                                |
| `onCheckedChange`   | Callback when the checked state changes.                        |
| `backgroundColor`   | Background color of the checkbox.                               |
| `contentColor`      | Color of the content inside the checkbox.                       |
| `borderColor`       | Color of the border.                                            |
| `borderWidth`       | Width of the border.                                            |
| `shape`             | Shape of the checkbox.                                          |
| `modifier`          | Modifier to be applied to the checkbox.                         |
| `contentDescription`| Accessibility description of the checkbox.                      |
| `checkIcon`         | Composable function to define the check icon.                   |

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
}
</style>

## Check Icon and other icons

You can find the check icon used in our examples and many other icons at [composeicons.com](https://composeicons.com).

