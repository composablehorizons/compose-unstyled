---
title: Button
description: An accessible clickable component used to create buttons with the styling of your choice. Comes with opinionated styling options such as content padding, shape and content color.
---

<UnstyledDemo id="button" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-button")
```

## Basic Example

You can create your own buttons using the `Button` component.

```kotlin
UnstyledButton(
    onClick = { /* TODO */ },
    backgroundColor = Color(0xFFFFFFFF),
    contentColor = Color(0xFF020817),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(12.dp),
) {
    BasicText("Submit")
}
```

## Styling

The Button renders nothing on the screen by default. It handles all accessibility semantics for you and comes with opinionated styling options, which covers most button needs out of the box.

The `shape` of the button is used to clip the button for you, so that the touch indicator within the bounds of the button.

The `borderColor` and `borderWidth` parameters place a border to the button. These take the given shape into consideration as opposed to using the respective Modifier alone.

The `backgroundColor` sets the color of the button's surface.

The `contentColor` specifies the color to use for the button's content. Components such as [Text](typography.md), [Text Field](textfield.md), [Icon](icon.md) will use this color to render their content.

The `contentPadding` parameter places padding within the bounds of the button. Using the `Modifier.padding()` modifier will cause the button to have a visual margin instead, due to the way of ordering Modifiers in compose works.

The `Button` component lays its contents horizontally and centers its contents horizontally and vertically.

```kotlin
UnstyledButton(
    onClick = { /* TODO */ },
    backgroundColor = Color(0xFFFFFFFF),
    contentColor = Color(0xFF020817),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(12.dp),
) {
    BasicText("Submit")
}
```

## Code Examples

### Icon Button

```kotlin
UnstyledButton(onClick = { /* TODO */ }) {
    // icon from composeicons.com/icons/lucide/pencil
    UnstyledIcon(Pencil, contentDescription = null)
    Spacer(Modifier.width(12.dp))
    BasicText("Compose")
}
```

### Outlined Button

```kotlin
UnstyledButton(
    onClick = { /* TODO */ },
    borderColor = Color.Gray,
    borderWidth = 1.dp,
    backgroundColor = Color.Transparent,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(6.dp)
) {
    BasicText("Outline")
}
```

### Text Button

```kotlin
UnstyledButton(
    onClick = { /* TODO */ },
    backgroundColor = Color.Transparent,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(6.dp)
) {
    BasicText("Text")
}


```

## Keyboard Interactions

| Key                                   | Description                                                   |
|---------------------------------------|---------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Activates the button, triggering the onClick event.            |
| <div class="keyboard-key">Space</div> | Activates the button, triggering the onClick event.            |

<ApiReference id="button" />
