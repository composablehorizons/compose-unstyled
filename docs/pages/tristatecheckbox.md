---
title: TriStateCheckbox
description: A foundational component for creating three-state checkboxes that can be checked, unchecked, or in an indeterminate state. Perfect for "select all" scenarios and hierarchical selections. Accessible out of the box and fully renderless, so that you can apply any styling you like.
---

<UnstyledDemo id="tristatecheckbox" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-tri-state-checkbox")
```

## Basic Example

To create a tri-state checkbox use the `TriStateCheckbox` component. The checkbox supports three states: `On`, `Off`, and `Indeterminate`. You can cycle through states by clicking or pressing Enter/Space while focused.

The `checkIcon` composable receives the current `ToggleableState` and should render the appropriate icon for each state.

```kotlin
var triState by remember { mutableStateOf(ToggleableState.Off) }

UnstyledTriStateCheckbox(
    value = triState,
    onClick = {
        triState = when (triState) {
            ToggleableState.Off -> ToggleableState.On
            ToggleableState.On -> ToggleableState.Indeterminate
            ToggleableState.Indeterminate -> ToggleableState.Off
        }
    },
    shape = RoundedCornerShape(4.dp),
    backgroundColor = Color.White,
    borderWidth = 1.dp,
    borderColor = Color.Black.copy(0.33f),
    modifier = Modifier.size(24.dp)
) { state ->
    when (state) {
        ToggleableState.On -> UnstyledIcon(Check, contentDescription = null)
        ToggleableState.Indeterminate -> UnstyledIcon(Minus, contentDescription = null)
        ToggleableState.Off -> Unit // No icon shown
    }
}
```

## Styling

The TriStateCheckbox is fully renderless and handles all UX logic, accessibility and keyboard interactions for you, but does not display any information on the screen by default.

The `shape` of the checkbox is used to clip the checkbox and applies to both the background and border.

The `borderColor` and `borderWidth` parameters place a border around the checkbox, taking the given shape into consideration.

The `backgroundColor` sets the color of the checkbox's surface.

The `checkIcon` composable is where you define what gets displayed for each state. It receives the current `ToggleableState` as a parameter.

## Code Examples

### Select All Pattern

A common use case for TriStateCheckbox is implementing "select all" functionality with a group of child checkboxes.

```kotlin
val checkboxOptions = listOf("Option 1", "Option 2", "Option 3")
var selected by remember { mutableStateOf(List(checkboxOptions.size) { false }) }

val triState = when {
    selected.all { it } -> ToggleableState.On
    selected.none { it } -> ToggleableState.Off
    else -> ToggleableState.Indeterminate
}

Column {
    // Parent TriState checkbox - "Select All"
    Row(verticalAlignment = Alignment.CenterVertically) {
        UnstyledTriStateCheckbox(
            value = triState,
            onClick = {
                val newState = when (triState) {
                    ToggleableState.Off -> true
                    ToggleableState.Indeterminate -> true
                    ToggleableState.On -> false
                }
                selected = List(checkboxOptions.size) { newState }
            },
            shape = RoundedCornerShape(4.dp),
            backgroundColor = Color.White,
            borderWidth = 1.dp,
            borderColor = Color.Black.copy(0.33f),
            modifier = Modifier.size(24.dp),
            contentDescription = "Select all options"
        ) { state ->
            when (state) {
                ToggleableState.On -> UnstyledIcon(Check, contentDescription = null)
                ToggleableState.Indeterminate -> UnstyledIcon(Minus, contentDescription = null)
                ToggleableState.Off -> Unit
            }
        }
        Spacer(Modifier.width(12.dp))
        BasicText("Select All")
    }

    // Child checkboxes
    checkboxOptions.forEachIndexed { index, option ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            UnstyledCheckbox(
                checked = selected[index],
                onCheckedChange = { checked ->
                    selected = selected.toMutableList().apply {
                        this[index] = checked
                    }
                },
                shape = RoundedCornerShape(4.dp),
                backgroundColor = Color.White,
                borderWidth = 1.dp,
                borderColor = Color.Black.copy(0.33f),
                modifier = Modifier.size(24.dp),
                contentDescription = option
            ) {
                UnstyledIcon(Check, contentDescription = null)
            }
            Spacer(Modifier.width(12.dp))
            BasicText(option)
        }
    }
}
```

## Keyboard Interactions

| Key                                   | Description                                                   |
|---------------------------------------|---------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Triggers the onClick callback |
| <div class="keyboard-key">Space</div> | Triggers the onClick callback |

<ApiReference id="tristatecheckbox" />
