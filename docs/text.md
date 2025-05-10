---
title: Text
description: A component for displaying text with various styles.
---

# Text

A themable component for displaying text with various styles and customizations.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../text-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.30.0")
}
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

### Consistent typography through the app

It is recommended to use the provided `LocalTextStyle` in order to maintain consistent text styling across your app.

If you need to override a text style for specific cases, you can either override a specific parameter via the `Text` modifier or pass an entire different style via the `style` parameter: 

```kotlin
CompositionLocalProvider(LocalTextStyle provides TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)) {
    Column {
        Text("This text will use the provided LocalTextStyle")
        Text("So will this text")
        
        Text("This text is also styled, but slighly modified", letterSpacing = 2.sp)

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

## Component API

### Text

| Parameter                               | Description                      |
|-----------------------------------------|----------------------------------|
| <div class='parameter'>`text`</div>     | The text to display.             |
| <div class='parameter'>`style`</div>    | The style to apply to the text.  |
| <div class='parameter'>`modifier`</div> | The `Modifier` for the text.     |
| <div class='parameter'>`textAlign`</div>| The alignment of the text.       |
| <div class='parameter'>`lineHeight`</div>| The height of the lines.         |
| <div class='parameter'>`fontSize`</div> | The size of the font.            |
| <div class='parameter'>`letterSpacing`</div>| The spacing between letters. |
| <div class='parameter'>`fontWeight`</div>| The weight of the font.          |
| <div class='parameter'>`color`</div>    | The color of the text.           |
| <div class='parameter'>`fontFamily`</div>| The family of the font.          |
| <div class='parameter'>`singleLine`</div>| Whether the text is single line. |
| <div class='parameter'>`minLines`</div> | Minimum number of lines to display. |
| <div class='parameter'>`maxLines`</div> | Maximum number of lines to display. |
| <div class='parameter'>`overflow`</div> | How visual overflow should be handled. |
