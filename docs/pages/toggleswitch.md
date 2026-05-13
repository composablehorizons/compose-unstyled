---
title: ToggleSwitch
description: A foundational unstyled component used to implement toggle switches in Jetpack Compose and Compose Multiplatform. Comes with accessibility features baked in and full styling options.
---

<UnstyledDemo id="toggleswitch" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-toggle-switch")
```

## Basic Example

To create a toggle switch, use the `UnstyledSwitch` component. It handles its own state and sets important accessibility
semantics.

`SwitchThumb` is animated according to the switch's state:

```kotlin
var toggled by remember { mutableStateOf(false) }

UnstyledSwitch(
    checked = toggled,
    onCheckedChange = { toggled = it },
    modifier = Modifier
        .width(58.dp)
        .height(30.dp)
        .background(Color.Gray, RoundedCornerShape(100))
        .padding(4.dp),
) {
    SwitchThumb {
        Box(
            Modifier
                .size(22.dp)
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = 8.dp,
                        spread = 0.dp,
                        color = Color.Black.copy(alpha = 0.14f),
                        offset = DpOffset(x = 0.dp, y = 2.dp)
                    )
                )
                .background(Color.White, CircleShape)
        )
    }
}
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

UnstyledSwitch(
    checked = toggled,
    onCheckedChange = { toggled = it },
    modifier = Modifier
        .width(58.dp)
        .height(30.dp)
        .background(animatedColor, RoundedCornerShape(100))
        .padding(4.dp),
) {
    SwitchThumb {
        Box(
            Modifier
                .size(22.dp)
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = 8.dp,
                        spread = 0.dp,
                        color = Color.Black.copy(alpha = 0.14f),
                        offset = DpOffset(x = 0.dp, y = 2.dp)
                    )
                )
                .background(Color.White, CircleShape)
            )
    }
}
```

### Toggle Switch Manually

You might want to change the `checked` state of the switch manually.

This is a common scenario when the interaction does not start from the switch itself, but rather an entire Row or
button.

To do this, pass `null` to the `onCheckedChange` parameter. Make sure to use the `selectable()` modifier, as it provides
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
    BasicText("Toggle Switch")
    UnstyledSwitch(
        checked = toggled,
        onCheckedChange = null,
        modifier = Modifier
            .width(58.dp)
            .height(30.dp)
            .background(if (toggled) Color.Green else Color.Red, RoundedCornerShape(100))
            .padding(4.dp),
    ) {
        SwitchThumb {
            Box(
                Modifier
                    .size(22.dp)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 8.dp,
                            spread = 0.dp,
                            color = Color.Black.copy(alpha = 0.14f),
                            offset = DpOffset(x = 0.dp, y = 2.dp)
                        )
                    )
                    .background(Color.White, CircleShape)
            )
        }
    }
}
```

<ApiReference id="toggleswitch" />
