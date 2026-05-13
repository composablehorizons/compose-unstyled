---
title: focusRing
description: A modifier that draws an outline around a composable's bounds when the component is focused, similar to browser focus indicators.
---

The `focusRing` is based on the [outline](outline.md) modifier and does not affect layout or size - it draws purely outside the composable's bounds and only appears when focused.

<ApiReference id="focusRing" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-focus-ring")
```


## Code Examples

### Basic Example

Focus ring requires an `interactionSource`, `width` and `color` to render a focus indicator around the component.

The `shape` parameter should match the shape of the composable you are styling for proper alignment.
```kotlin
val interactionSource = remember { MutableInteractionSource() }

SimpleButton(
    modifier = Modifier.focusRing(
        interactionSource = interactionSource,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp
    ),
    interactionSource = interactionSource
)
```

### Customizing Width

You can customize the thickness of the focus ring by adjusting the `width` parameter.
```kotlin
val interactionSource = remember { MutableInteractionSource() }

SimpleButton(
    modifier = Modifier.focusRing(interactionSource, 1.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp)),
    interactionSource = interactionSource
)
SimpleButton(
    modifier = Modifier.focusRing(interactionSource, 2.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp)),
    interactionSource = interactionSource
)
SimpleButton(
    modifier = Modifier.focusRing(interactionSource, 4.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp)),
    interactionSource = interactionSource
)
```

### Customizing Shape

The focus ring adapts to different shapes. The `shape` parameter should match the shape of your composable for proper alignment.

Note that generic shapes are not supported and will fail silently.
```kotlin
val interactionSource = remember { MutableInteractionSource() }

SimpleButton(
    shape = RectangleShape,
    modifier = Modifier.focusRing(interactionSource, 2.dp, Color(0xFF3B82F6), shape = RectangleShape),
    interactionSource = interactionSource
)
SimpleButton(
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.focusRing(interactionSource, 2.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp)),
    interactionSource = interactionSource
)
SimpleButton(
    shape = RoundedCornerShape(100),
    modifier = Modifier.focusRing(interactionSource, 2.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(100)),
    interactionSource = interactionSource
)
```

### Customizing Offset

The `offset` parameter controls the distance between the composable and its focus ring, creating a gap effect.
```kotlin
val interactionSource = remember { MutableInteractionSource() }

SimpleButton(
    modifier = Modifier.focusRing(interactionSource, 2.dp, Color(0xFF3B82F6), offset = 0.dp, shape = RoundedCornerShape(8.dp)),
    interactionSource = interactionSource
)
SimpleButton(
    modifier = Modifier.focusRing(interactionSource, 2.dp, Color(0xFF3B82F6), offset = 4.dp, shape = RoundedCornerShape(8.dp)),
    interactionSource = interactionSource
)
SimpleButton(
    modifier = Modifier.focusRing(interactionSource, 2.dp, Color(0xFF3B82F6), offset = 8.dp, shape = RoundedCornerShape(8.dp)),
    interactionSource = interactionSource
)
```

### Customizing Color

You can customize the focus ring color to match your design system or create visual emphasis.
```kotlin
val redInteractionSource = remember { MutableInteractionSource() }
val greenInteractionSource = remember { MutableInteractionSource() }
val purpleInteractionSource = remember { MutableInteractionSource() }

SimpleButton(
    modifier = Modifier.focusRing(redInteractionSource, 2.dp, Color(0xFFEF4444), offset = 2.dp, shape = RoundedCornerShape(8.dp)),
    interactionSource = redInteractionSource
)
SimpleButton(
    modifier = Modifier.focusRing(greenInteractionSource, 2.dp, Color(0xFF10B981), offset = 2.dp, shape = RoundedCornerShape(8.dp)),
    interactionSource = greenInteractionSource
)
SimpleButton(
    modifier = Modifier.focusRing(purpleInteractionSource, 2.dp, Color(0xFF8B5CF6), offset = 2.dp, shape = RoundedCornerShape(8.dp)),
    interactionSource = purpleInteractionSource
)
```
