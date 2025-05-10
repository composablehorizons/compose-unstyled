---
title: Button
description: A foundational component for creating Buttons in Compose Multiplatform
---

An accessible clickable component used to create buttons with the styling of your choice.

Comes with opinionated styling options such as content padding, shape and content color.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../button-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
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

You can create your own buttons using the `Button` component.

```kotlin
Button(
    onClick = { /* TODO */ },
    backgroundColor = Color(0xFFFFFFFF),
    contentColor = Color(0xFF020817),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(12.dp),
) {
    Text("Submit")
}
```

## Styling

The Button renders nothing on the screen by default. It handles all accessibility semantics for you and comes with opinionated styling options, which covers most button needs out of the box.

The `shape` of the button is used to clip the button for you, so that the touch indicator within the bounds of the button.

The `borderColor` and `borderWidth` parameters place a border to the button. These take the given shape into consideration as opposed to using the respective Modifier alone. 

The `backgroundColor` sets the color of the button's surface. 

The `contentColor` specifies the color to use for the button's content. Components such as [Text](text.md), [Text Field](textfield.md), [Icon](icon.md) will use this color to render their content.

The `contentPadding` parameter places padding within the bounds of the button. Using the `Modifier.padding()` modifier will cause the button to have a visual margin instead, due to the way of ordering Modifiers in compose works.

The `Button` component lays its contents horizontally and centers its contents horizontally and vertically. 

```kotlin
Button(
    onClick = { /* TODO */ },
    backgroundColor = Color(0xFFFFFFFF),
    contentColor = Color(0xFF020817),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(12.dp),
) {
    Text("Submit")
}
```

## Code Examples

### Icon Button

```kotlin
Button(onClick = { /* TODO */ }) {
    // icon from composeicons.com/icons/lucide/pencil
    Icon(Pencil, contentDescription = null)
    Spacer(Modifier.width(12.dp))
    Text("Compose")
}
```

### Outlined Button

```kotlin
Button(
    onClick = { /* TODO */ },
    borderColor = Color.Gray,
    borderWidth = 1.dp,
    backgroundColor = Color.Transparent,
    contentColor = Color.Black,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(6.dp)
) {
    Text("Outline")
}
```

### Text Button

```kotlin
Button(
    onClick = { /* TODO */ },
    backgroundColor = Color.Transparent,
    contentColor = Color.Black,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(6.dp)
) {
    Text("Text")
}


```

## Keyboard Interactions

| Key                                   | Description                                                   |
|---------------------------------------|---------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Activates the button, triggering the onClick event.            |
| <div class="keyboard-key">Space</div> | Activates the button, triggering the onClick event.            |

## Component API

### Button

| Parameter           | Description                                                     |
|---------------------|-----------------------------------------------------------------|
| `onClick`           | The callback to be invoked when the button is clicked.          |
| `enabled`           | Whether the button is enabled.                                  |
| `shape`             | The shape of the button.                                        |
| `backgroundColor`   | The background color of the button.                             |
| `contentColor`      | The color to apply to the contents of the button.               |
| `contentPadding`    | Padding values for the content.                                 |
| `borderColor`       | The color of the border.                                        |
| `borderWidth`       | The width of the border.                                        |
| `modifier`          | Modifier to be applied to the button.                           |
| `role`              | The role of the button for accessibility purposes.              |
| `indication`        | The indication to be shown when the button is interacted with.  |
| `interactionSource` | The interaction source for the button.                          |
| `content`           | A composable function that defines the content of the button.   |

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
}
</style>
