---
title: defaultMinimumComponentInteractiveSize
description: A modifier that sets the minimum interactive size of a composable based on the current device type and theme configuration.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-theming")
```

> **Warning:** `ComponentInteractiveSize` and `defaultComponentInteractiveSize` are deprecated and will be removed in
> 3.0. If your design system needs a minimum interactive size, implement that policy in your own components.

## Code Examples

### Basic Usage

Use the `defaultComponentInteractiveSize` theme property to specify the minimum interaction size for your components.

Use `Modifier.minimumInteractiveComponentSize()` when creating your components to set the minimum size:

```kotlin expandable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledButton
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.ComponentInteractiveSize
import com.composeunstyled.theme.buildThemeV2

@Composable
fun MinimumInteractiveSizeBasicExample() {
    val Theme = buildThemeV2 {
        defaultComponentInteractiveSize = ComponentInteractiveSize(
            size = 48.dp,
        )
    }

    Theme {
        UnstyledButton(
            onClick = { },
            backgroundColor = Color(0xFF3B82F6),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            BasicText("Click me", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin expandable
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.buildThemeV2
import com.composeunstyled.theme.ComponentInteractiveSize
```

```kotlin expandable
val Theme = buildThemeV2 {
    defaultComponentInteractiveSize = ComponentInteractiveSize(
        size = 48.dp,
    )
}

@Composable
fun MinimumInteractiveSizeExample() {
    Theme {
        UnstyledButton(onClick = { }, modifier = Modifier.minimumInteractiveComponentSize()) {
            BasicText("Click me")
        }
    }
}
```

### Responsive Design

Use the `touchInteractionSize` parameter to set the minimum interactive size when running on touch devices (such as mobile).
Use the `nonTouchInteractionSize` parameter to set the size when running on non-touch devices (such as desktop).

```kotlin expandable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledButton
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.ComponentInteractiveSize
import com.composeunstyled.theme.buildThemeV2

@Composable
fun MinimumInteractiveSizeResponsiveExample() {
    val Theme = buildThemeV2 {
        defaultComponentInteractiveSize = ComponentInteractiveSize(
            touchInteractionSize = 48.dp,
            nonTouchInteractionSize = 32.dp
        )
    }

    Theme {
        UnstyledButton(
            onClick = { },
            backgroundColor = Color(0xFF3B82F6),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            BasicText("Click me", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin expandable
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.buildThemeV2
import com.composeunstyled.theme.ComponentInteractiveSize
```

```kotlin expandable
val Theme = buildThemeV2 {
    defaultComponentInteractiveSize = ComponentInteractiveSize(
        touchInteractionSize = 48.dp,
        nonTouchInteractionSize = 32.dp
    )
}

@Composable
fun ResponsiveInteractiveSizeExample() {
    Theme {
        // this button will be at least 48x48 on mobile and 32x32 on desktop
        UnstyledButton(onClick = { }, modifier = Modifier.minimumInteractiveComponentSize()) {
            BasicText("Click me")
        }
    }
}
```
