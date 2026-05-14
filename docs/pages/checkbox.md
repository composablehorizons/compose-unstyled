---
title: Checkbox
description: A checkbox component with full control over the indicator, bounds, and checked animation.
---

<UnstyledDemo id="checkbox" />

## Features

- Custom interaction bounds
- Custom checked indicator
- Animated checked content
- Accessibility label

## Installation

```kotlin
implementation("com.composables:composeunstyled-checkbox")
```

## Anatomy

```kotlin
UnstyledCheckbox(
  checked = checked,
  onCheckedChange = onCheckedChange,
) {
  CheckedIndicator {
  }
}
```

## Concepts

- `UnstyledCheckbox` represents the interactive bounds of the checkbox.
- `CheckedIndicator` represents the visible checked state. It automatically shows and hides its
  content based on the `UnstyledCheckbox` state.
- Give `CheckedIndicator` a fixed size when its content only exists while checked. Without a fixed
  size, the checkbox can change layout size when the indicator appears or disappears.

## Accessibility

Screen readers will automatically read any text placed inside `UnstyledCheckbox`.

Use the `accessibilityLabel` parameter when the checkbox has no visible text label.

## Code Examples

### Making an entire row checkable

Use the `UnstyledCheckbox` component as the row container to make the full row toggleable. This is useful when the label should also toggle the checkbox:

```kotlin
UnstyledCheckbox(
  checked = checked,
  onCheckedChange = { checked = it },
) {
  Row {
    CheckedIndicator {
      BasicText("✓")
    }

    BasicText("Accept all terms")
  }
}
```

### Creating larger checkbox interaction bounds

Use padding on the `CheckedIndicator` component to place the visible checkbox inside a larger
interaction area. This is useful when the ripple or touch target should be larger than the visible
checkbox:

<UnstyledDemo id="checkbox-extended-indicator-bounds" />

### Animating the checked indicator

Use the `enter` and `exit` parameters on `CheckedIndicator` to animate the checked content.

```kotlin
UnstyledCheckbox(
  checked = checked,
  onCheckedChange = { checked = it },
) {
  CheckedIndicator(
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    BasicText("Selected")
  }
}
```

### Creating a custom checked indicator animation

Use the `checked` state value to draw your own indicator instead of `CheckedIndicator`. This is useful when your design system needs a custom checkmark animation:

<UnstyledDemo id="checkbox-custom-checked-indicator" />

### Labeling an icon-only checkbox

Use the `accessibilityLabel` parameter when the checkbox content has no text:

```kotlin
UnstyledCheckbox(
  checked = checked,
  onCheckedChange = { checked = it },
  accessibilityLabel = "Enable notifications",
) {
  CheckedIndicator {
    BasicText("✓")
  }
}
```

<ApiReference id="checkbox" />
