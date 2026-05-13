---
title: defaultMinimumComponentInteractiveSize
description: A modifier that sets the minimum interactive size of a composable based on the current device type and theme configuration.
---

<ApiReference id="defaultMinimumComponentInteractiveSize" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-theming")
```


## Code Examples

### Basic Usage

Use the `defaultComponentInteractiveSize` theme property to specify the minimum interaction size for your components.

Use the `Modifier.defaultComponentInteractiveSize()` when creating your components to set the minimum size:

```kotlin
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
import com.composeunstyled.theme.buildTheme

@Composable
fun MinimumInteractiveSizeBasicExample() {
    val Theme = buildTheme {
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

```kotlin
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.ComponentInteractiveSize
```

```kotlin
val Theme = buildTheme {
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

```kotlin
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
import com.composeunstyled.theme.buildTheme

@Composable
fun MinimumInteractiveSizeResponsiveExample() {
    val Theme = buildTheme {
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

```kotlin
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.ComponentInteractiveSize
```

```kotlin
val Theme = buildTheme {
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
