---
title: Stack
description: A flexible Compose layout composable that arranges its children either horizontally or vertically, providing a unified API that abstracts over Row and Column layouts.
---

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.UnstyledCheckbox
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.theme.rememberColoredIndication

@Composable
fun StackExample() {
    var isVertical by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    @Composable
    fun StackItem(label: String) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFF18181B), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                label,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Stack(
            orientation = StackOrientation.Vertical,
            spacing = 24.dp,
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.toggleable(
                    value = isVertical,
                    interactionSource = interactionSource,
                    indication = rememberColoredIndication(
                        color = Color.White,
                        hoveredAlpha = 0.33f,
                        focusedAlpha = 0.33f,
                        pressedAlpha = 0.33f
                    ),
                    role = Role.Checkbox,
                    onValueChange = { isVertical = it }
                )
            ) {
                UnstyledCheckbox(
                    checked = isVertical,
                    onCheckedChange = null,
                    shape = RoundedCornerShape(6.dp),
                    backgroundColor = Color.White,
                    borderColor = Color.Black.copy(alpha = 0.1f),
                    borderWidth = 1.dp,
                    modifier = Modifier.size(28.dp),
                    contentDescription = "Use vertical orientation"
                ) {
                    UnstyledIcon(
                        Lucide.Check,
                        contentDescription = null,
                        tint = Color(0xFF18181B),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                BasicText(
                    "Vertical orientation",
                    style = TextStyle(color = Color(0xFF18181B), fontSize = 14.sp)
                )
            }
            Stack(
                orientation = if (isVertical) StackOrientation.Vertical else StackOrientation.Horizontal,
                spacing = 12.dp,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                StackItem("A")
                StackItem("B")
                StackItem("C")
            }
        }
    }
}
```

<ApiReference id="stack" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-stack")
```


## Code Examples

### Basic Example

Use `Stack` to arrange its children horizontally by default:

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import androidx.compose.foundation.text.BasicText

@Composable
fun StackBasicExample() {
    Stack(spacing = 12.dp) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("0", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("1", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("2", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin
import com.composeunstyled.Stack
```

```kotlin
Stack {
    Box(/*...*/) {
        BasicText("0")
    }
    Box(/*...*/) {
        BasicText("1")
    }
    Box(/*...*/) {
        BasicText("2")
    }
}
```

### Orientation

Use `orientation` with the desired `StackOrientation` to arrange children vertically or horizontally:

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import androidx.compose.foundation.text.BasicText

@Composable
fun StackVerticalExample() {
    Stack(
        orientation = StackOrientation.Vertical,
        spacing = 12.dp
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("0", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("1", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("2", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
```

```kotlin
Stack(orientation = StackOrientation.Vertical) {
    Box(/*...*/) {
        BasicText("0")
    }
    Box(/*...*/) {
        BasicText("1")
    }
    Box(/*...*/) {
        BasicText("2")
    }
}
```

### Main Axis Arrangement

Use `mainAxisArrangement` to control how children are distributed along the main axis.

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import androidx.compose.foundation.text.BasicText

@Composable
fun StackMainAxisExample() {
    Stack(
        orientation = StackOrientation.Horizontal,
        mainAxisArrangement = MainAxisArrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("0", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("1", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("2", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.Stack
```

```kotlin
Stack(mainAxisArrangement = MainAxisArrangement.SpaceBetween) {
    Box(/*...*/) {
        BasicText("0")
    }
    Box(/*...*/) {
        BasicText("1")
    }
    Box(/*...*/) {
        BasicText("2")
    }
}
```

### Cross Axis Alignment

Use `crossAxisAlignment` to align children on the perpendicular axis.

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import androidx.compose.foundation.text.BasicText

@Composable
fun StackCrossAxisExample() {
    Stack(
        orientation = StackOrientation.Horizontal,
        crossAxisAlignment = CrossAxisAlignment.Center,
        spacing = 12.dp
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("0", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.width(48.dp).height(80.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("1", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.width(48.dp).height(64.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("2", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
```

```kotlin
Stack(
    orientation = StackOrientation.Horizontal,
    crossAxisAlignment = CrossAxisAlignment.Center,
) {
    Box(/*...*/) {
        BasicText("0")
    }
    Box(/*...*/) {
        BasicText("1")
    }
    Box(/*...*/) {
        BasicText("2")
    }
}
```

### Spacing

Use `spacing` to control the amount of spacing between children.

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Stack
import androidx.compose.foundation.text.BasicText

@Composable
fun StackSpacingExample() {
    Stack(spacing = 48.dp) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("0", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("1", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("2", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin
import com.composeunstyled.Stack
```

```kotlin
Stack(spacing = 48.dp) {
    Box(/*...*/) {
        BasicText("0")
    }
    Box(/*...*/) {
        BasicText("1")
    }
    Box(/*...*/) {
        BasicText("2")
    }
}
```

### Weights

Use `Modifier.weight()` on any children within the `StackScope` to control its size relative to other children in the Stack.

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import androidx.compose.foundation.text.BasicText

@Composable
fun StackWeightsExample() {
    Stack(
        orientation = StackOrientation.Horizontal,
        spacing = 12.dp,
        modifier = Modifier.width(500.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("1f", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.weight(1f).height(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("1f", style = TextStyle(color = Color.White))
        }
        Box(
            modifier = Modifier.weight(2f).height(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText("2f", style = TextStyle(color = Color.White))
        }
    }
}
```

```kotlin
import com.composeunstyled.Stack
```

```kotlin
Stack {
    Box(Modifier.size(48.dp)) {
        BasicText("1f")
    }
    Box(Modifier.weight(1f)/*...*/) {
        BasicText("1f")
    }
    Box(Modifier.weight(2f)/*...*/) {
        BasicText("2f")
    }
}
```


### Responsive design

Use `orientation` to dynamically change how the `Stack` lays out its children based on screen size.

To get context about the screen size, use [`currentWindowContainerSize()`](window-container-size):

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.AlignHorizontalDistributeCenter
import com.composables.icons.lucide.AlignVerticalDistributeCenter
import com.composables.icons.lucide.Lucide
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.outline

@Composable
fun StackResponsiveExample() {
    var isHorizontal by remember { mutableStateOf(true) }

    Stack(
        orientation = StackOrientation.Vertical,
        spacing = 16.dp,
        crossAxisAlignment = com.composeunstyled.CrossAxisAlignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(100))
                .background(Color(0xFFF3F4F6), RoundedCornerShape(100))
                .padding(start = 4.dp)
                .padding(8.dp)
        ) {
            Stack(
                orientation = StackOrientation.Horizontal,
                mainAxisArrangement = com.composeunstyled.MainAxisArrangement.SpaceBetween,
                crossAxisAlignment = com.composeunstyled.CrossAxisAlignment.Center,
                modifier = Modifier.width(300.dp - 16.dp)
            ) {
                BasicText("Orientation")

                Stack(
                    orientation = StackOrientation.Horizontal,
                    spacing = 4.dp
                ) {
                    UnstyledButton(
                        onClick = { isHorizontal = true },
                        backgroundColor = if (isHorizontal) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(50),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                        modifier = Modifier
                            .size(40.dp)
                            .then(
                                if (isHorizontal) {
                                    Modifier.outline(
                                        width = 1.dp,
                                        color = Color(0xFFD1D5DB),
                                        shape = RoundedCornerShape(50)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        UnstyledIcon(
                            imageVector = Lucide.AlignHorizontalDistributeCenter,
                            contentDescription = "Horizontal orientation",
                            modifier = Modifier.size(24.dp),
                            tint = if (isHorizontal) Color.Black else Color(0xFF6B7280)
                        )
                    }

                    UnstyledButton(
                        onClick = { isHorizontal = false },
                        backgroundColor = if (!isHorizontal) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(50),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                        modifier = Modifier
                            .size(40.dp)
                            .then(
                                if (!isHorizontal) {
                                    Modifier.outline(
                                        width = 1.dp,
                                        color = Color(0xFFD1D5DB),
                                        shape = RoundedCornerShape(50)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        UnstyledIcon(
                            imageVector = Lucide.AlignVerticalDistributeCenter,
                            contentDescription = "Vertical orientation",
                            modifier = Modifier.size(24.dp),
                            tint = if (!isHorizontal) Color.Black else Color(0xFF6B7280)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.width(200.dp).height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Stack(
                orientation = if (isHorizontal) {
                    StackOrientation.Horizontal
                } else {
                    StackOrientation.Vertical
                },
                spacing = 12.dp
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText("0", style = TextStyle(color = Color.White))
                }
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText("1", style = TextStyle(color = Color.White))
                }
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText("2", style = TextStyle(color = Color.White))
                }
            }
        }
    }
}
```

```kotlin
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.currentWindowContainerSize
```

```kotlin
val containerSize = currentWindowContainerSize()
val orientation = if (containerSize.width >= 600.dp) {
    StackOrientation.Horizontal
} else {
    StackOrientation.Vertical
}

Stack(
    orientation = orientation,
    spacing = 16.dp
) {
    Box(/*...*/) {
        BasicText("0")
    }
    Box(/*...*/) {
        BasicText("1")
    }
    Box(/*...*/) {
        BasicText("2")
    }
}
```
