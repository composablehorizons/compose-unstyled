---
title: ToggleSwitch
description: A component for creating toggle switches for user interaction.
---

# Toggle Switch

A foundational unstyled component used to implement toggle switches in Compose Multiplatform.

Comes with accessibility features baked in and full styling options.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../toggleswitch-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.30.0")
}
```

## Basic Example

To create a toggle switch, use the `ToggleSwitch` component. It handles its own state and sets important accessibility
semantics.

The `Thumb` is animated according to the switch's state:

```kotlin
var toggled by remember { mutableStateOf(false) }

ToggleSwitch(
    toggled = toggled,
    onToggled = { toggled = it },
    modifier = Modifier.fillMaxWidth(),
    thumb = {
        Thumb(
            shape = CircleShape,
            color = Color.White,
            modifier = Modifier.shadow(elevation = 4.dp, CircleShape)
        )
    },
    backgroundColor = Color.Gray
)
```

## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (
according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the
order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters.

## Code Examples

### Toggle with Animated Color

```kotlin
var toggled by remember { mutableStateOf(false) }

val animatedColor by animateColorAsState(
    if (toggled) Color(0xFF2196F3) else Color(0xFFE0E0E0)
)

ToggleSwitch(
    toggled = toggled,
    shape = RoundedCornerShape(100),
    backgroundColor = animatedColor,
    modifier = Modifier.width(58.dp),
    contentPadding = PaddingValues(4.dp),
) {
    Thumb(
        shape = CircleShape,
        color = Color.White,
        modifier = Modifier.shadow(elevation = 4.dp, CircleShape),
    )
}
```

### Toggle Switch Manually

You might want to change the `toggled` state of the `ToggleSwitch` manually.

This is a common scenario when the interaction does not start from the ToggleSwitch itself, but rather an entire Row or
button.

To do this, pass `null` to the `onToggled` parameter. Make sure to use the `selectable()` modifier, as it provides
important accessibility semantics:

```kotlin
var toggled by remember { mutableStateOf(false) }

Row(
    Modifier
        .selectable(
            selected = toggled,
            onClick = { toggled = !toggled },
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            role = Role.Switch
        ),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text("Toggle Switch")
    ToggleSwitch(
        toggled = toggled,
        onToggled = null,
        modifier = Modifier.width(58.dp),
        thumb = {
            Thumb(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.shadow(elevation = 4.dp, CircleShape)
            )
        },
        backgroundColor = if (toggled) Color.Green else Color.Red
    )
}
```

## Component API

### ToggleSwitch

| Parameter         | Description                                            |
|-------------------|--------------------------------------------------------|
| `toggled`         | Whether the switch is on or off.                       |
| `onToggled`       | Callback when the switch changes state.                |
| `modifier`        | Modifier to be applied to the switch.                  |
| `thumb`           | Composable function to define the thumb of the switch. |
| `backgroundColor` | Background color of the switch track.                  |
| `contentPadding`  | Padding inside the switch.                             |
| `enabled`         | Whether the switch is enabled.                         |
| `shape`           | Shape of the switch.                                   |

