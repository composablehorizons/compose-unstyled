---
title: Slider
description: A foundational component used to create sliders with the styling of your choice.  Accessible, keyboard interactions included, just bring the styling.
---

<UnstyledDemo id="slider" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-slider")
```

## Basic Example

To create a slider, use `UnstyledSlider` with a controlled value. The slider handles accessibility semantics and keyboard interactions.

```kotlin
var value by remember { mutableStateOf(0.5f) }

UnstyledSlider(
    value = value,
    onValueChange = { value = it },
    modifier = Modifier.width(300.dp),
    track = { state ->
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.Gray)
        )
    },
    thumb = { state ->
        Box(
            Modifier
                .size(20.dp)
                .background(Color.White, CircleShape)
        )
    }
)
```

## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters.

## Code Example

### Change Progress Manually

You can manually change the progress of the slider by updating the controlled value.

```kotlin
var value by remember { mutableStateOf(0.5f) }

UnstyledButton(onClick = { value = (value + 0.1f).coerceAtMost(1f) }) {
    BasicText("Increase")
}

UnstyledSlider(
    value = value,
    onValueChange = { value = it },
    track = { state ->
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray)
        )
    },
    thumb = { state ->
        Box(
            Modifier
                .size(20.dp)
                .background(Color.White, CircleShape)
        )
    }
)
```

### React to Hover Events

A common pattern is to show a hover effect on the slider thumb. You can achieve this by using the `hoverable` modifier and `MutableInteractionSource` to react to hover events.

```kotlin
var value by remember { mutableStateOf(0.5f) }

UnstyledSlider(
    value = value,
    onValueChange = { value = it },
    track = { state ->
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray)
        )
    },
    thumb = { state ->
        val thumbInteractionSource = remember { MutableInteractionSource() }
        val isHovered by thumbInteractionSource.collectIsHoveredAsState()
        val glowColor by animateColorAsState(
            if (isFocused || isHovered) Color.White.copy(0.33f) else Color.Transparent
        )

        // box to hold the 'glow' effect
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(glowColor).padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 8.dp,
                            spread = 0.dp,
                            color = Color.Black.copy(alpha = 0.14f),
                            offset = DpOffset(x = 0.dp, y = 2.dp)
                        )
                    )
                    .hoverable(thumbInteractionSource)
                    .background(Color.White, CircleShape)
            )
        }
    }
)
```

### Resize Thumb on Drag

A common slider pattern is to resize the slider thumb when it is dragged. This can be achieved using the `animateDpAsState` to smoothly transition the size of the thumb.

```kotlin
var value by remember { mutableStateOf(0.5f) }
val interactionSource = remember { MutableInteractionSource() }
val isPressed by interactionSource.collectIsPressedAsState()

UnstyledSlider(
    interactionSource = interactionSource,
    value = value,
    onValueChange = { value = it },
    track = { state ->
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray)
        )
    },
    thumb = { state ->
        val thumbSize by animateDpAsState(targetValue = if (isPressed) 20.dp else 18.dp)

        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(thumbSize)
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
)
```

## Keyboard Interactions

| Key                                   | Description                                                                                             |
|---------------------------------------|---------------------------------------------------------------------------------------------------------|
| <div class="keyboard-key">⬆</div>     | Increases the slider value.                                                                             |
| <div class="keyboard-key">⬇</div>     | Decreases the slider value.                                                                             |
| <div class="keyboard-key">Home</div>  | Sets the slider to the minimum value.                                                                   |
| <div class="keyboard-key">End</div>   | Sets the slider to the maximum value.                                                                   |

<ApiReference id="slider" />
