---
title: TriStateCheckbox
description: A three-state checkbox component for checked, unchecked, and indeterminate values.
---

<UnstyledDemo id="tristatecheckbox" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-tri-state-checkbox")
```

## Anatomy

```kotlin
UnstyledTriStateCheckbox(
  value = value,
  onClick = onClick,
) {
  StateIndicator {
  }
}
```

## Concepts

- `UnstyledTriStateCheckbox` represents the tri-state checkbox interaction target.
- `StateIndicator` renders content for the current `ToggleableState`.

## Accessibility

Use tri-state checkboxes for parent selection controls where only some child items are selected.

## Code Examples

### Rendering each checkbox state

Use the `StateIndicator` component to render content for each `ToggleableState`:

```kotlin
UnstyledTriStateCheckbox(
  value = value,
  onClick = { toggleParent() },
) {
  StateIndicator { state ->
    when (state) {
      ToggleableState.On -> BasicText("Selected")
      ToggleableState.Off -> BasicText("Not selected")
      ToggleableState.Indeterminate -> BasicText("Partially selected")
    }
  }
}
```

### Building a select-all checkbox

Use the `ToggleableState.Indeterminate` value when only some items are selected:

```kotlin
val selectedCount = selectedItems.count()
val parentState = when (selectedCount) {
  0 -> ToggleableState.Off
  items.size -> ToggleableState.On
  else -> ToggleableState.Indeterminate
}

UnstyledTriStateCheckbox(
  value = parentState,
  onClick = {
    selectedItems = if (parentState == ToggleableState.On) emptySet() else items.toSet()
  },
) {
  StateIndicator { state ->
    BasicText(state.toString())
  }
}
```

<ApiReference id="tristatecheckbox" />
