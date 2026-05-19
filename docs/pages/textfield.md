---
title: Text Field
description: A text field component with placeholders, transformations, and text styling hooks.
---

<UnstyledDemo id="textfield" />

## Features

- State-based text input
- Placeholder slot
- Input and output transformations
- Text and selection style parameters

## Installation

```kotlin
implementation("com.composables:composeunstyled-text-field")
```

## Anatomy

```kotlin
UnstyledTextField(state = state) {
  TextInput()
}
```

## Concepts

- `UnstyledTextField` represents the text field container.
- `TextInput` renders the editable text and optional placeholder.

## Accessibility

Use `accessibilityLabel` when the text field does not include a readable label in its content.

## Code Examples

### Adding placeholder text

Use the `placeholder` parameter on `TextInput` to render content while the field is empty:

```kotlin
val state = rememberTextFieldState()

UnstyledTextField(state = state) {
  TextInput(
    placeholder = {
      BasicText("Email")
    },
  )
}
```

### Creating a single-line text field

Use the `lineLimits` parameter to restrict input to one line:

```kotlin
UnstyledTextField(
  state = state,
  lineLimits = TextFieldLineLimits.SingleLine,
) {
  TextInput()
}
```

### Setting the keyboard type

Use the `keyboardOptions` parameter to request a specific software keyboard:

```kotlin
UnstyledTextField(
  state = state,
  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
) {
  TextInput()
}
```

### Styling entered text

Use the text style parameters to set the style of the editable text:

```kotlin
UnstyledTextField(
  state = state,
  fontWeight = FontWeight.Medium,
  textColor = Color.Black,
) {
  TextInput()
}
```

### Styling selected text

By default, selection colors are unspecified. Use the `selectionColors` parameter to set the
selection handle and background colors:

```kotlin
UnstyledTextField(
  state = state,
  selectionColors = TextSelectionColors(
    handleColor = Color.Black,
    backgroundColor = Color.Black.copy(alpha = 0.4f),
  ),
) {
  TextInput()
}
```

### Labeling an icon-only text field

Use the `accessibilityLabel` parameter when the visible text field has no text label:

```kotlin
UnstyledTextField(
  state = state,
  accessibilityLabel = "Search",
) {
  TextInput()
}
```

<ApiReference id="textfield" />
