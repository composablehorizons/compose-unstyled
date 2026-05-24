---
title: Breakpoints
description: Screen breakpoint utilities for adaptive Compose layouts.
---

<UnstyledDemo id="breakpoints" />

## Features

- Width and height breakpoint scales
- Named breakpoints with custom thresholds
- Active breakpoint comparison with the scale that resolved it
- Window resize updates through Compose state

## Installation

```kotlin
implementation("com.composables:composeunstyled-breakpoints")
```

## Anatomy

```kotlin
val sm = Breakpoint("sm")
val md = Breakpoint("md")

val widthBreakpoints = ScreenBreakpoints {
  sm at 640.dp
  md at 768.dp
}

ProvideBreakpoints(widthBreakpoints = widthBreakpoints) {
  val widthBreakpoint = currentWidthBreakpoint()
}
```

## Concepts

- `Breakpoint` names one step in your responsive scale.
- `ScreenBreakpoints` defines the minimum size where each breakpoint starts.
- `ProvideBreakpoints` makes width and height breakpoint scales available to content.
- `currentWidthBreakpoint()` and `currentHeightBreakpoint()` resolve the current window container size against those scales.
- The active breakpoint compares against other breakpoints from the same scale.

## Code Examples

### Creating width breakpoints

Use `ScreenBreakpoints` to define the minimum width for each named breakpoint:

```kotlin
val sm = Breakpoint("sm")
val md = Breakpoint("md")
val lg = Breakpoint("lg")

val widthBreakpoints = ScreenBreakpoints {
  sm at 640.dp
  md at 768.dp
  lg at 1024.dp
}
```

### Reading the current width breakpoint

Use the `currentWidthBreakpoint()` function inside `ProvideBreakpoints` to react to the current window width:

```kotlin
ProvideBreakpoints(widthBreakpoints = widthBreakpoints) {
  val widthBreakpoint = currentWidthBreakpoint()
  val expanded = widthBreakpoint isAtLeast lg

  if (expanded) {
    BasicText("Expanded layout")
  } else {
    BasicText("Compact layout")
  }
}
```

### Using height breakpoints

Use the `heightBreakpoints` parameter with `currentHeightBreakpoint()` when layout behavior depends on available height:

```kotlin
val short = Breakpoint("short")
val tall = Breakpoint("tall")

val heightBreakpoints = ScreenBreakpoints {
  short at 480.dp
  tall at 720.dp
}

ProvideBreakpoints(heightBreakpoints = heightBreakpoints) {
  val heightBreakpoint = currentHeightBreakpoint()
  val canShowDetails = heightBreakpoint isAtLeast tall
}
```

### Resolving a breakpoint manually

Use the `breakpointFor()` function when you already have a size and do not need to read the current window:

```kotlin
val breakpoint = widthBreakpoints.breakpointFor(900.dp)

BasicText("Current breakpoint: ${breakpoint.name}")
```

<ApiReference id="breakpoints" />
