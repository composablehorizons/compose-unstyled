---
title: Slider
description: A slider component with custom track and thumb slots.
---

<UnstyledDemo id="slider" />

## Features

- Horizontal and vertical sliders
- Discrete step support
- Custom track and thumb slots
- Keyboard and screen reader value changes

## Installation

```kotlin
implementation("com.composables:composeunstyled-slider")
```

## Anatomy

```kotlin
UnstyledSlider(
  value = value,
  onValueChange = onValueChange,
  track = {
  },
  thumb = {
  },
)
```

## Concepts

- `UnstyledSlider` represents the interactive range users can drag or adjust.
- `SliderState` is passed to the track and thumb slots.
- The `track` slot renders below the thumb.
- The `thumb` slot renders at the current slider offset.

## Accessibility

`UnstyledSlider` exposes progress semantics and supports arrow keys, Page Up, Page Down, Home, and End.

## Code Examples

### Creating a stepped slider

Use the `steps` parameter to snap the slider value to discrete stops:

```kotlin
UnstyledSlider(
  value = value,
  onValueChange = { value = it },
  steps = 4,
  track = { state ->
    BasicText("${state.value}")
  },
  thumb = { state ->
    BasicText("${state.value}")
  },
)
```

### Using a custom value range

Use the `valueRange` parameter when the slider value is not from `0f` to `1f`:

```kotlin
UnstyledSlider(
  value = volume,
  onValueChange = { volume = it },
  valueRange = 0f..100f,
  track = { state ->
    BasicText("${state.value}")
  },
  thumb = { state ->
    BasicText("${state.value}")
  },
)
```

### Creating a vertical slider

Use the `orientation` parameter to make the slider vertical:

```kotlin
UnstyledSlider(
  value = value,
  onValueChange = { value = it },
  orientation = Orientation.Vertical,
  track = { state ->
    BasicText("${state.value}")
  },
  thumb = { state ->
    BasicText("${state.value}")
  },
)
```

### Running code after value changes finish

Use the `onValueChangeFinished` callback to react after drag, tap, keyboard, or screen reader changes finish:

```kotlin
UnstyledSlider(
  value = value,
  onValueChange = { value = it },
  onValueChangeFinished = { save(value) },
  track = { state ->
    BasicText("${state.value}")
  },
  thumb = { state ->
    BasicText("${state.value}")
  },
)
```

<ApiReference id="slider" />
