---
title: Tooltip
description: A tooltip component for contextual help on hover, focus, and long press.
---

<UnstyledDemo id="tooltip" />

## Features

- Focus, hover, and long-press triggers
- Non-modal tooltip panel
- Collision-aware positioning
- Screen reader announcements

## Installation

```kotlin
implementation("com.composables:composeunstyled-tooltip")
```

## Anatomy

```kotlin
TooltipHost {
  UnstyledTooltip(
    panel = {
      TooltipPanel {
      }
    },
  ) {
  }
}
```

## Concepts

- `TooltipHost` provides the destination where tooltip panels are rendered.
- `UnstyledTooltip` marks the interactive area the user can hover, focus, or long-press to show the
  tooltip.
- The `anchor` slot renders the content on which the tooltip will be anchored to.
- The `panel` slot renders the floating content that is shown for the anchor.
- `TooltipPanel` renders the floating tooltip content.

## Usage Considerations

`UnstyledTooltip` does not make the anchor focusable. Use focusable content in the anchor slot when
the tooltip needs to show on keyboard focus.

## Accessibility

`TooltipPanel` automatically announces the tooltip content when the tooltip becomes visible.

## Code Examples

### Positioning a tooltip

Use the `side`, `alignment`, `sideOffset`, and `alignmentOffset` parameters to place the tooltip
relative to the anchor:

```kotlin
UnstyledTooltip(
  side = AnchorSide.Bottom,
  alignment = AnchorAlignment.Center,
  sideOffset = 8.dp,
  panel = {
    TooltipPanel {
      BasicText("More information")
    }
  },
) {
  BasicText("Help")
}
```

### Delaying tooltip hover

Use the `hoverDelayMillis` parameter to wait before showing the tooltip on hover:

```kotlin
UnstyledTooltip(
  hoverDelayMillis = 500,
  panel = {
    TooltipPanel {
      BasicText("More information")
    }
  },
) {
  BasicText("Help")
}
```

### Changing the long-press duration

Use the `longPressShowDurationMillis` parameter to change how long the tooltip stays visible after a
long press:

```kotlin
UnstyledTooltip(
  longPressShowDurationMillis = 3_000,
  panel = {
    TooltipPanel {
      BasicText("More information")
    }
  },
) {
  BasicText("Help")
}
```

### Animating a tooltip panel

Use the `enter` and `exit` parameters on `TooltipPanel` to animate the tooltip panel:

```kotlin
UnstyledTooltip(
  panel = {
    TooltipPanel(
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      BasicText("More information")
    }
  },
) {
  BasicText("Help")
}
```

<ApiReference id="tooltip" />
