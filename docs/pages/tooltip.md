---
title: Tooltip
description: A non-interactive popup component that displays contextual information when an element is focused, hovered, or long-pressed. Comes with tons of customization options such as animation support, arrow (caret) support and handle screen reader announcements out of the box.
---

<UnstyledDemo id="tooltip" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-tooltip")
```

```kotlin
import com.composeunstyled.UnstyledTooltip
import com.composeunstyled.UnstyledTooltipPanel
```

```kotlin
@Composable
fun ArrowUp(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(8.dp, 4.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, color = color)
    }
}

@Composable
fun TooltipExample() {
    UnstyledTooltip(
        placement = RelativeAlignment.TopCenter,
        panel = {
            UnstyledTooltipPanel(
                modifier = Modifier.zIndex(15f),
                arrow = { direction ->
                    // Draw your arrow pointing towards the given direction
                    val degrees = when (direction) {
                        TooltipArrowDirection.Up -> 0f
                        TooltipArrowDirection.Down -> 180f
                        TooltipArrowDirection.Left -> 90f
                        TooltipArrowDirection.Right -> 270f
                    }
                    ArrowUp(Modifier.rotate(degrees), Color.Black.copy(0.8f))
                }
            ) {
                BasicText("This is a tooltip")
            }
        }
    ) {
        UnstyledButton(onClick = {}) {
            BasicText("Hover me")
        }
    }
}
```

## Basic Example

The `Tooltip` component has 2 main slots: its *panel* and its *anchor*.

The *anchor* contains the element to which the tooltip is anchored. When this element has focus, is hovered or
long-pressed (on touch) the tooltip will be displayed.

The *panel* contains one of the overloads of `TooltipPanel` which renders the content of the tooltip according to the
`Tooltip`'s internal state.

Tooltips are placed in the same layout as the trigger. This is different
to [how Tooltips work in Compose Foundation](#unstyled-tooltip-vs-compose-foundation-tooltips), in order to prevent any
focus and pointer issues. Because of this, you need to use `Modifier.zIndex()` to your *panel* to place it above other
elements in the same layout.

```kotlin
UnstyledTooltip(
    placement = RelativeAlignment.TopCenter,
    panel = {
        UnstyledTooltipPanel(
            modifier = Modifier.zIndex(15f),
            enter = slideInVertically(tween(150), initialOffsetY = { (it * 0.25).toInt() }) +
                    scaleIn(
                        animationSpec = tween(150),
                        transformOrigin = TransformOrigin(0.5f, 1f),
                        initialScale = 0.65f
                    ) + fadeIn(tween(150)),
            exit = fadeOut(tween(250)),
            arrow = { direction ->
                val degrees = when (direction) {
                    TooltipArrowDirection.Up -> 0f
                    TooltipArrowDirection.Down -> 180f
                    TooltipArrowDirection.Left -> 90f
                    TooltipArrowDirection.Right -> 270f
                }
                ArrowUp(Modifier.rotate(degrees), Color.Black.copy(0.8f))
            }
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(Color.Black.copy(0.8f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
            ) {
                BasicText("Notifications", style = TextStyle(color = Color.White))
            }
        }
    }
) {
    val interactionSource = remember { MutableInteractionSource() }

    UnstyledButton(
        onClick = { },
        contentPadding = PaddingValues(8.dp),
        shape = CircleShape,
        modifier = Modifier.focusRing(interactionSource, 1.dp, Color(0xFF3B82F6), CircleShape),
        interactionSource = interactionSource
    ) {
        UnstyledIcon(Lucide.BellDot, contentDescription = null)
    }
}
```

## Code Examples

### Styling the Tooltip

The `TooltipPanel` composable contains styling properties such as *backgroundColor*, *contentColor*, *shape*  and
*contentPadding* for easy styling.

```kotlin
UnstyledTooltip(
    placement = RelativeAlignment.TopCenter,
    panel = {
        UnstyledTooltipPanel(
            modifier = Modifier.zIndex(15f),
            backgroundColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            BasicText("Styled tooltip")
        }
    }
) {
    UnstyledButton(onClick = {}) {
        BasicText("Hover me")
    }
}
```

### Positioning the Tooltip

Pass the relative alignment you want to the `Tooltip`'s *placement* property:

```kotlin
UnstyledTooltip(
    placement = RelativeAlignment.TopEnd,
    panel = {
        TooltipPanel {
            BasicText("This is a tooltip")
        }
    }
) {
    UnstyledButton(onClick = {}) {
        BasicText("Hover me")
    }
}
```

### Tooltip with Arrow (Caret)

Use the `TooltipPanel` with the *arrow* parameter. The *arrow* lambda gives you the `TooltipArrowDirection` which the
arrow needs to be pointing towards.

We will always place the arrow between the `anchor` and the `panel` centering it to the respective direction.

```kotlin
@Composable
fun ArrowUp(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(8.dp, 4.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, color = color)
    }
}

UnstyledTooltip(
    placement = RelativeAlignment.BottomCenter,
    panel = {
        UnstyledTooltipPanel(
            modifier = Modifier.zIndex(15f),
            arrow = { direction ->
                // Draw your arrow pointing towards the given direction
                val degrees = when (direction) {
                    TooltipArrowDirection.Up -> 0f
                    TooltipArrowDirection.Down -> 180f
                    TooltipArrowDirection.Left -> 90f
                    TooltipArrowDirection.Right -> 270f
                }
                ArrowUp(Modifier.rotate(degrees), Color.Black)
            }
        ) {
            BasicText("Tooltip with arrow")
        }
    }
) {
    UnstyledButton(onClick = {}) {
        BasicText("Hover me")
    }
}
```

### Animating the Tooltip

Pass the respective animation specs you want in the `TooltipPanel`'s *enter* and *exit* parameters:

```kotlin
UnstyledTooltip(
    placement = RelativeAlignment.TopCenter,
    panel = {
        UnstyledTooltipPanel(
            modifier = Modifier.zIndex(15f),
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            BasicText("This tooltip fades in and out")
        }
    }
) {
    UnstyledButton(onClick = {}) {
        BasicText("Hover me")
    }
}
```

## Unstyled Tooltip vs Compose Foundation Tooltips

Tooltips in Compose Foundation are implemented using Popups. Popups do not allow pointer events behind them. This can
cause weird glitches when the Tooltip is right above the trigger and mouse overed.

Unstyled Tooltips are placed in the same layout as its trigger, and do not use Popups. As a result, there are no
weird focus or pointer issues.

## Keyboard Interactions

| Key                                    | Description                               |
|----------------------------------------|-------------------------------------------|
| <div class="keyboard-key">Escape</div> | Dismisses the tooltip when it is visible. |

<ApiReference id="tooltip" />
