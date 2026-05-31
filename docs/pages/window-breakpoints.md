---
title: Window Breakpoints
description: Window breakpoint utilities for building adaptive Compose layouts.
---

<UnstyledDemo id="breakpoints" />

## Features

- Define custom width and/or height breakpoints.
- Build responsive layouts around named semantics instead of copy-pasting dimensions.
- Emit new state only when the resolved breakpoint changes, not on every window resize.

## Installation

```kotlin
implementation("com.composables:composeunstyled-breakpoints")
```

## Anatomy

```kotlin
val Compact = WidthBreakpoint("compact")
val Medium = WidthBreakpoint("medium")

val widthBreakpoints = WindowWidthBreakpoints {
  Compact startsAt 0.dp
  Medium startsAt 600.dp
}

ProvideWindowWidthBreakpoints(widthBreakpoints) {
  val widthBreakpoint = currentWindowWidthBreakpoint()
}
```

## Concepts

- `WidthBreakpoint`/`HeightBreakpoint` define a named breakpoint.
- `WindowWidthBreakpoints`/`WindowHeightBreakpoints` define the minimum window size where each breakpoint
  starts.
- Every breakpoint scale must include one breakpoint that starts at `0.dp`.
- `ProvideWindowWidthBreakpoints`/`ProvideWindowHeightBreakpoints` make one breakpoint scale available
  to their content scope.
- `ProvideWindowBreakpoints` makes both width and height breakpoint scales available to its content
  scope.
- `currentWindowWidthBreakpoint()`/`currentWindowHeightBreakpoint()` return a new resolved breakpoint
  only when the current window crosses into another breakpoint.

## Code Examples

### Building responsive layouts

Define the window widths your layout should respond to using `WindowWidthBreakpoints`.

Then use `ProvideWindowWidthBreakpoints` near the root of your app to make the current breakpoint available down the UI tree.

Use `currentWindowWidthBreakpoint()` to access the currently resolved breakpoint:

```kotlin
val Compact = WidthBreakpoint("compact")
val Medium = WidthBreakpoint("medium")
val Expanded = WidthBreakpoint("expanded")

val widthBreakpoints = WindowWidthBreakpoints {
  Compact startsAt 0.dp
  Medium startsAt 600.dp
  Expanded startsAt 840.dp
}

ProvideWindowWidthBreakpoints(widthBreakpoints) {
  val widthBreakpoint = currentWindowWidthBreakpoint()
  val expanded = widthBreakpoint isAtLeast Expanded

  if (expanded) {
    BasicText("Expanded layout")
  } else {
    BasicText("Compact layout")
  }
}
```

### Using height breakpoints

Use `ProvideWindowHeightBreakpoints` with `currentWindowHeightBreakpoint()` when layout behavior
depends on available height:

```kotlin
val Short = HeightBreakpoint("short")
val Tall = HeightBreakpoint("tall")

val heightBreakpoints = WindowHeightBreakpoints {
  Short startsAt 0.dp
  Tall startsAt 720.dp
}

ProvideWindowHeightBreakpoints(heightBreakpoints) {
  val heightBreakpoint = currentWindowHeightBreakpoint()
  val canShowDetails = heightBreakpoint isAtLeast Tall
}
```

<ApiReference id="breakpoints" />
