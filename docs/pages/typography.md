---
title: Typography
description: A themable component for displaying text with various styles and customizations.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-theming")
```

## Basic Example

The `Text` component is used to display text with various styles and properties.

```kotlin
Text("Hello, World!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
```

## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters or optional components.


## Code Examples

### Consistent typography through your app

It is recommended to use the provided `LocalTextStyle` in order to maintain consistent text styling across your app.

If you need to override a text style for specific cases, you can either override a specific parameter via the `Text` modifier or pass an entire different style via the `style` parameter:

```kotlin
CompositionLocalProvider(LocalTextStyle provides TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)) {
    Column {
        Text("This text will use the provided LocalTextStyle")
        Text("So will this text")

        Text("This text is also styled, but slightly modified", letterSpacing = 2.sp)

        Text("This text is completely different", style = TextStyle())
    }
}
```

### Styling Text

```kotlin
Text("Bold Text", fontWeight = FontWeight.Bold)
Text("Italic Text", fontStyle = FontStyle.Italic)
Text("Underlined Text", textDecoration = TextDecoration.Underline)
Text("Colored Text", color = Color.Red)
Text("Large Text", fontSize = 24.sp)
```

### Handling Text Overflow

```kotlin
Text(
    "This is a very long text that might overflow",
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
```

<ApiReference id="typography" />
