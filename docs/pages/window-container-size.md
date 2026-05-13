---
title: Window Container Size
description: A Composable function that returns the current window size and automatically triggers recomposition when the window is resized, enabling responsive layouts.
---

<ApiReference id="window-container-size" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-window-container-size")
```


## Code Examples

### Basic Example

Use `currentWindowContainerSize()` to get the current window dimensions:

<UnstyledDemo id="window-container-size" />

> **HINT:** Resize your __browser's__ width to see the size changing.

```kotlin
import com.composeunstyled.currentWindowContainerSize
```

```kotlin
val containerSize = currentWindowContainerSize()
```
