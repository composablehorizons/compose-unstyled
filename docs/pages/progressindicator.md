---
title: Progress Indicator
description: A progress indicator component for determinate and indeterminate loading states.
---

<UnstyledDemo id="progressindicator" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-progress")
```

## Anatomy

```kotlin
UnstyledProgress(progress = progress) {
  Indicator()
}
```

## Concepts

- `UnstyledProgress()` represents the visible bounds of the progress including the track.
- `Indicator` fills the available width by the current progress value.

## Accessibility

`UnstyledProgress` applies the appropriate accessibility semantics based on whether the `progress`
parameter is provided.

## Code Examples

### Creating a determinate progress indicator

Use the `progress` parameter when the current progress value is known:

```kotlin
UnstyledProgress(progress = 0.4f) {
  Indicator()
}
```

### Creating an indeterminate progress indicator

Use the overload without the `progress` parameter when the current progress value is unknown:

```kotlin
UnstyledProgress {
  BasicText("Loading")
}
```

### Drawing a custom indicator

Use the `ProgressScope.progress` property when the indicator needs custom measurement:

```kotlin
UnstyledProgress(progress = progress) {
  Box(Modifier.fillMaxWidth(progress))
}
```

<ApiReference id="progressindicator" />
