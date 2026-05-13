---
title: ColoredIndication
description: A customizable indication effect that displays colored overlays based on user interactions like hover, press, focus, and drag.
---

<ApiReference id="coloredindication" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-colored-indication")
```


## Code Examples

### Basic Usage

Use `rememberColoredIndication` to create a colored indication effect with a single base color:

```kotlin
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledButton
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.theme.rememberColoredIndication

@Composable
fun ColoredIndicationBasicExample() {
    val interactionSource = remember { MutableInteractionSource() }

    UnstyledButton(
        onClick = { },
        backgroundColor = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        indication = rememberColoredIndication(color = Color.White),
        interactionSource = interactionSource
    ) {
        BasicText("Hover or Click", style = TextStyle(color = Color.White))
    }
}
```

```kotlin
import com.composeunstyled.UnstyledButton
import com.composeunstyled.theme.rememberColoredIndication
```

```kotlin
UnstyledButton(
    onClick = { },
    indication = rememberColoredIndication(color = Color.White),
) {
    BasicText("Hover or Click")
}
```
