---
title: outline
description: A modifier that draws an outline outside a composable's bounds, similar to CSS `outline` property.
---

Unlike Compose's built-in `border` modifier, `outline` does not affect layout or size - it draws purely outside the composable's bounds.

<ApiReference id="outline" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-outline")
```


## Code Examples

### Basic Example

Outline requires a `width` and ` color` to render anything around the composable.

The `shape` parameter accepts the shape of the composable you are styling.

The final shape of the outline will be calculated based of the provided shape, width and offset.

```kotlin
SimpleButton(
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
    )
)
```

### Customizing Width

You can customize the thickness of the outline by adjusting the `width` parameter.
```kotlin
SimpleButton(
    modifier = Modifier.outline(1.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp))
)
SimpleButton(
    modifier = Modifier.outline(2.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp))
)
SimpleButton(
    modifier = Modifier.outline(4.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp))
)
```

### Customizing Shape

The outline adapts to different shapes. The `shape` parameter should match the shape of your composable for proper alignment.

Note that generic shapes are not supported and will fail silently.
```kotlin
SimpleButton(
    shape = RectangleShape,
    modifier = Modifier.outline(2.dp, Color(0xFF3B82F6), shape = RectangleShape)
)
SimpleButton(
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.outline(2.dp, Color(0xFF3B82F6), shape = RoundedCornerShape(8.dp))
)
SimpleButton(
    shape = CircleShape,
    modifier = Modifier.outline(2.dp, Color(0xFF3B82F6), shape = CircleShape)
)
```

### Customizing Offset

The `offset` parameter controls the distance between the composable and its outline, creating a gap effect.
```kotlin
SimpleButton(
    modifier = Modifier.outline(2.dp, Color(0xFF3B82F6), offset = 0.dp, shape = RoundedCornerShape(8.dp))
)
SimpleButton(
    modifier = Modifier.outline(2.dp, Color(0xFF3B82F6), offset = 4.dp, shape = RoundedCornerShape(8.dp))
)
SimpleButton(
    modifier = Modifier.outline(2.dp, Color(0xFF3B82F6), offset = 8.dp, shape = RoundedCornerShape(8.dp))
)
```

### Customizing Color

You can customize the outline color to match your design system or create visual emphasis.
```kotlin
SimpleButton(
    modifier = Modifier.outline(2.dp, Color(0xFFEF4444), offset = 2.dp, shape = RoundedCornerShape(8.dp))
)
SimpleButton(
    modifier = Modifier.outline(2.dp, Color(0xFF10B981), offset = 2.dp, shape = RoundedCornerShape(8.dp))
)
SimpleButton(
    modifier = Modifier.outline(2.dp, Color(0xFF8B5CF6), offset = 2.dp, shape = RoundedCornerShape(8.dp))
)
```
