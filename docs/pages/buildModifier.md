---
title: buildModifier
description: A utility function that enables conditional chaining of modifiers using a builder pattern, providing a clean alternative to nested conditional statements or multiple modifier chains.
---

<ApiReference id="buildModifier" />

The function returns a single `Modifier` that is the result of chaining all the modifiers added to the list.


## Installation

```kotlin
implementation("com.composables:composeunstyled-build-modifier")
```


## Code Examples
### Basic Example

The `buildModifier` function provides a clean way to conditionally chain modifiers without creating nested conditional statements.

Instead of writing:
```kotlin
val isSelected by remember { mutableStateOf(true) }
val isClickable by remember { mutableStateOf(false) }

val modifier = Modifier.padding(16.dp)
    .let { if (isSelected) it.background(Color.Blue) else it }
    .let { if (isClickable) it.clickable { /* handle click */ } else it }
```

You can write:
```kotlin
val isSelected by remember { mutableStateOf(true) }
val isClickable by remember { mutableStateOf(false) }

val modifier = buildModifier {
    add(Modifier.padding(16.dp))
    if (isSelected) {
        add(Modifier.background(Color.Blue))
    }
    if (isClickable) {
        add(Modifier.clickable { /* handle click */ })
    }
}
```

### Conditional Styling

Build modifiers based on component state or external conditions.

```kotlin
val isError by remember { mutableStateOf(true) }
val isDisabled by remember { mutableStateOf(false) }

BasicText(
    text = "Form Field",
    modifier = buildModifier {
        add(Modifier.padding(12.dp))
        if (isError) {
            add(Modifier.background(Color.Red.copy(alpha = 0.1f)))
            add(Modifier.border(1.dp, Color.Red, RoundedCornerShape(4.dp)))
        }
        if (isDisabled) {
            add(Modifier.alpha(0.5f))
        } else {
            add(Modifier.clickable { /* handle click */ })
        }
    }
)
```

### Dynamic Sizing

Handle optional size constraints elegantly.

```kotlin
val maxWidth by remember { mutableStateOf<Dp?>(300.dp) }
val fixedHeight by remember { mutableStateOf<Dp?>(null) }
val backgroundColor by remember { mutableStateOf(Color(0xFFF5F5F5)) }

Card(
    modifier = buildModifier {
        add(Modifier.padding(16.dp))
        maxWidth?.let { add(Modifier.widthIn(max = it)) }
        fixedHeight?.let { add(Modifier.height(it)) }
        add(Modifier.background(backgroundColor, RoundedCornerShape(8.dp)))
    }
) {
    // Card content
}
```

### Complex State Management

Combine multiple conditions for sophisticated modifier logic.

```kotlin
val isLoading by remember { mutableStateOf(false) }
val hasError by remember { mutableStateOf(true) }
val isSelected by remember { mutableStateOf(false) }
val isInteractive by remember { mutableStateOf(true) }

Box(
    modifier = buildModifier {
        add(Modifier.fillMaxWidth().padding(8.dp))

        // Base styling
        add(Modifier.background(Color.White, RoundedCornerShape(8.dp)))
        add(Modifier.border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)))

        // State-specific modifications
        when {
            isLoading -> {
                add(Modifier.alpha(0.7f))
                add(Modifier.shimmer()) // Custom shimmer effect
            }
            hasError -> {
                add(Modifier.border(2.dp, Color.Red, RoundedCornerShape(8.dp)))
                add(Modifier.background(Color.Red.copy(alpha = 0.05f), RoundedCornerShape(8.dp)))
            }
            isSelected -> {
                add(Modifier.border(2.dp, Color.Blue, RoundedCornerShape(8.dp)))
                add(Modifier.background(Color.Blue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)))
            }
        }

        // Interactive behavior
        if (isInteractive && !isLoading) {
            add(Modifier.clickable { /* handle selection */ })
        }
    }
) {
    // Box content
}
```
