---
title: Window Container Size
description: A Composable function that returns the current window size and automatically triggers recomposition when the window is resized, enabling responsive layouts.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-window-container-size:2.9.2")
```


## Code Examples

### Basic Example

Use `currentWindowContainerSize()` to get the current window dimensions:

<UnstyledDemo id="window-container-size" />

> **HINT:** Resize your __browser's__ width to see the size changing.

```kotlin expandable
import com.composeunstyled.currentWindowContainerSize
```

```kotlin expandable
val containerSize = currentWindowContainerSize()
```
