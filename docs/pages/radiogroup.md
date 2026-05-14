---
title: Radio Group
description: A radio group component for single-choice selection.
---

<UnstyledDemo id="radiogroup" />

## Features

- Generic radio values
- Arrow-key focus movement
- Animated selected indicator

## Installation

```kotlin
implementation("com.composables:composeunstyled-radio-group")
```

## Anatomy

```kotlin
UnstyledRadioGroup(
  value = value,
  onValueChange = onValueChange,
) {
  RadioButton(value) {
    SelectedIndicator {
    }
  }
}
```

## Concepts

- `UnstyledRadioGroup` groups radio options for one selected value.
- `RadioButton` renders an option inside the group.
- `SelectedIndicator` renders only when its radio button is selected.

## Accessibility

Use `accessibilityLabel` when the radio group does not contain a readable group label.

## Code Examples

### Selecting one radio option

Use the `value` parameter on each `RadioButton` to connect it to the group value:

```kotlin
var selected by remember { mutableStateOf("small") }

UnstyledRadioGroup(
  value = selected,
  onValueChange = { selected = it },
) {
  RadioButton("small") {
    BasicText("Small")
  }

  RadioButton("large") {
    BasicText("Large")
  }
}
```

### Rendering the selected indicator

Use the `SelectedIndicator` component to render content only for the selected option:

```kotlin
RadioButton("large") {
  SelectedIndicator {
    BasicText("Selected")
  }

  BasicText("Large")
}
```

### Animating the selected indicator

Use the `enter` and `exit` parameters on `SelectedIndicator` to animate the selected indicator:

```kotlin
RadioButton("large") {
  SelectedIndicator(
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    BasicText("Selected")
  }
}
```

<ApiReference id="radiogroup" />
