---
title: Text Field
description: A component for user input through text fields.
---

# Text Field

A themamble component to build text fields with the styling of your choise.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../textfield-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.30.0")
}
```

## Basic Example

To create a text field, use the `TextField` component. It handles its own state and sets important accessibility semantics.

```kotlin
TextField(
    value = "",
    onValueChange = { /* TODO */ },
    modifier = Modifier.fillMaxWidth(),
    placeholder = "Enter text here",
    borderWidth = 1.dp,
    borderColor = Color.Gray,
    shape = RoundedCornerShape(8.dp),
    singleLine = true
)
```

## Code Examples

### Consistent typography through the app

It is recommended to use the provided `LocalTextStyle` in order to maintain consistent text styling across your app.

If you need to override a text style for specific cases, you can either override a specific parameter via the `Text` modifier or pass an entire different style via the `style` parameter:

```kotlin
CompositionLocalProvider(LocalTextStyle provides TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)) {
    Column {
        // this text field will use the provided Text Style
        TextField(
            value = "",
            onValueChange = { /* TODO */ },
        )
        Text("So will this text")
        
        Text("This text is also styled, but slighly modified", letterSpacing = 2.sp)

        Text("This text is completely different", style = TextStyle())
    }
}
```

### Specifying Input Type

You can specify the input type for the `TextField` using the `keyboardOptions` parameter. For example, to specify an email input type:

```kotlin
TextField(
    value = "",
    onValueChange = { /* TODO */ },
    modifier = Modifier.fillMaxWidth(),
    placeholder = "Email",
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
)
```

### Adding a Trailing Icon

You can add a trailing icon to the `TextField` to provide additional functionality, such as toggling password visibility:

```kotlin
var showPassword by remember { mutableStateOf(false) }

TextField(
    value = "",
    onValueChange = { /* TODO */ },
    modifier = Modifier.fillMaxWidth(),
    placeholder = "Password",
    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
    trailingIcon = {
        Button(
            onClick = { showPassword = !showPassword },
            backgroundColor = Color.Transparent,
            contentPadding = PaddingValues(4.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(
                imageVector = if (showPassword) EyeOff else Eye,
                contentDescription = if (showPassword) "Hide password" else "Show password",
                tint = Color(0xFF757575)
            )
        }
    }
)
```

### Handling Password Input

To handle password input, you can use the `visualTransformation` parameter to obscure the text:

```kotlin
TextField(
    value = "",
    onValueChange = { /* TODO */ },
    modifier = Modifier.fillMaxWidth(),
    placeholder = "Password",
    visualTransformation = PasswordVisualTransformation(),
    trailingIcon = {
        Button(
            onClick = { /* Toggle password visibility */ },
            backgroundColor = Color.Transparent,
            contentPadding = PaddingValues(4.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(
                imageVector = if (/* condition */) EyeOff else Eye,
                contentDescription = if (/* condition */) "Hide password" else "Show password",
                tint = Color(0xFF757575)
            )
        }
    }
)
```
## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters.

## Component API

### TextField

| Parameter                               | Description                      |
|-----------------------------------------|----------------------------------|
| `value`                                 | The current text in the text field. |
| `onValueChange`                         | Callback when the text changes.  |
| `editable`                              | Whether the text field is editable. |
| `modifier`                              | Modifier to be applied to the text field. |
| `contentPadding`                        | Padding inside the text field.   |
| `leadingIcon`                           | Leading icon to display inside the text field. |
| `trailingIcon`                          | Trailing icon to display inside the text field. |
| `placeholder`                           | Placeholder text when the field is empty. |
| `contentColor`                          | Color of the text content.       |
| `disabledColor`                         | Color when the text field is disabled. |
| `backgroundColor`                       | Background color of the text field. |
| `borderWidth`                           | Width of the border.             |
| `borderColor`                           | Color of the border.             |
| `shape`                                 | Shape of the text field.         |
| `textStyle`                             | Style to apply to the text.      |
| `textAlign`                             | Alignment of the text.           |
| `fontSize`                              | Size of the font.                |
| `fontWeight`                            | Weight of the font.              |
| `fontFamily`                            | Family of the font.              |
| `singleLine`                            | Whether the text field is single line. |
| `minLines`                              | Minimum number of lines to display. |
| `maxLines`                              | Maximum number of lines to display. |
| `keyboardOptions`                       | Options for the keyboard.        |
| `keyboardActions`                       | Actions for the keyboard.        |
| `interactionSource`                     | Interaction source for the text field. |
| `spacing`                               | Spacing between elements inside the text field. |
| `visualTransformation`                  | Visual transformation for the text. |

## Icons and other icons

You can find the icons used in our examples and many other icons at [composeicons.com](https://composeicons.com).

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a> 