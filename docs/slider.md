---
title: Slider
description: A component for creating sliders for user input.
---

# Slider

A foundational component used to create sliders with the styling of your choise.

Accessible, keyboard interactions included, just bring the styling.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../slider-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.30.0")
}
```

## Basic Example

To create a slider, use the `Slider` component. The slider handles its own state and sets important accessibility semantics.

```kotlin
val sliderState = rememberSliderState(initialValue = 0.5f)

Slider(
    state = sliderState,
    modifier = Modifier.width(300.dp),
    track = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.Gray)
        )
    },
    thumb = {
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

You can manually change the progress of the slider using a button to increment the slider's value. This allows you to programmatically control the slider's position.

```kotlin
val sliderState = rememberSliderState(initialValue = 0.5f)

Button(onClick = { sliderState.value += 0.1f }) {
    Text("Increase")
}

Slider(
    state = sliderState,
    track = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray)
        )
    },
    thumb = {
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
val sliderState = rememberSliderState()

Slider(
    state = sliderState,
    track = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray)
        )
    },
    thumb = {
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
            Thumb(
                color = Color.White,
                modifier = Modifier
                    .shadow(4.dp, CircleShape)
                    .hoverable(thumbInteractionSource),
                shape = CircleShape,
            )
        }
    }
)
```

### Resize Thumb on Drag

A common slider pattern is to resize the slider thumb when it is dragged. This can be achieved using the `animateDpAsState` to smoothly transition the size of the thumb.

```kotlin
val sliderState = rememberSliderState()
val interactionSource = remember { MutableInteractionSource() }
val isPressed by interactionSource.collectIsPressedAsState()

Slider(
    interactionSource = interactionSource,
    state = sliderState,
    track = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray)
        )
    },
    thumb = {
        val thumbSize by animateDpAsState(targetValue = if (isPressed) 20.dp else 18.dp)

        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Thumb(
                color = Color.White,
                modifier = Modifier
                    .size(thumbSize)
                    .shadow(4.dp, CircleShape),
                shape = CircleShape
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

## Component API

### SliderState

| Parameter        | Description                                       |
|------------------|---------------------------------------------------|
| `value`          | The current value of the slider.                  |
| `valueRange`     | The range of values the slider can take.          |
| `steps`          | The number of discrete steps in the slider.       |

### rememberSliderState

| Parameter      | Description                                            |
|----------------|--------------------------------------------------------|
| `initialValue` | The initial value for the slider state.                |
| `valueRange`   | The range of values the slider can take.               |
| `steps`        | The number of discrete steps in the slider.            |

### Slider

| Parameter            | Description                                                 |
|----------------------|-------------------------------------------------------------|
| `state`              | The state of the slider, managing the current value.        |
| `modifier`           | Modifier to be applied to the slider.                       |
| `track`              | Composable function to define the track of the slider.      |
| `thumb`              | Composable function to define the thumb of the slider.      |

### Thumb

| Parameter            | Description                                                 |
|----------------------|-------------------------------------------------------------|
| `color`              | The color of the thumb.                                     |
| `modifier`           | Modifier to be applied to the thumb.                        |
| `shape`              | The shape of the thumb.                                     |
