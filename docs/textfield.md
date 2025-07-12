---
title: Text Field
description: A component for user input through text fields.
---

# Text Field

A themable component to build text fields with the styling of your choice.

Supports placeholders, leading and trailing slots out of the box along with accessibility labels (visual or not).

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../demo/index.html?id=textfield" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.37.0")
}
```

## Basic Example

To create a text field, use the `TextField` component and use the `TextInput()` component within its content scope.

The `TextField` itself groups together every part of the text field in order to make sense for screen reader technology.

Other than the `TextInput` which handles the part of the text field the user can enter text at, it can contain elements
such as `Text` for a visual label.

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    singleLine = true
) {
    TextInput()
}
```

## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (
according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the
order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters.

## Code Examples

### Consistent typography through your app

It is recommended to use the provided `LocalTextStyle` in order to maintain consistent text styling across your app.

If you need to override a text style for specific cases, you can either override a specific parameter via the `Text`
modifier or pass an entire different style via the `style` parameter:

```kotlin
CompositionLocalProvider(LocalTextStyle provides TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)) {
    Column {
        // this text field will use the provided Text Style
        TextField(
            value = "",
            onValueChange = { /* TODO */ },
        ) {
            // the provided text style will be used in the text input 
            TextInput()
        }
        Text("So will this text")

        Text("This text is also styled, but slightly modified", letterSpacing = 2.sp)

        Text("This text is completely different", style = TextStyle())
    }
}
```

### Specifying Input Type

You can specify the input type for the `TextField` using the `keyboardOptions` parameter. For example, to specify an
email input type:

```kotlin
TextField(
    value = "",
    onValueChange = { /* TODO */ },
    modifier = Modifier.fillMaxWidth(),
    placeholder = "Email",
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
) {
    InputText()
}
```

### Adding a Trailing Icon

You can add a trailing icon to the `TextField` to provide additional functionality, such as toggling password
visibility:

```kotlin
var password by remember { mutableStateOf("") }
var showPassword by remember { mutableStateOf(false) }

TextField(
    value = password,
    onValueChange = { password = it },
) {
    TextInput(
        placeholder = { Text("Password") },
        trailing = {
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
}
```

### Handling Password Input

To handle password input, you can use the `visualTransformation` parameter to obscure the text:

```kotlin
var password by remember { mutableStateOf("") }
var showPassword by remember { mutableStateOf(false) }

TextField(
    value = password,
    onValueChange = { password = it },
    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
) {
    TextInput(
        placeholder = { Text("Password") },
        trailing = {
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
}
```

### Adding an Invisible Label

You can add an invisible label to your text field for accessibility purposes. 

This is particularly useful when you want to provide context for screen readers without showing a visual label:

```kotlin
TextField(
    value = "",
    onValueChange = { /* TODO */ },
) {
    TextInput(
        label = "Email address", // This label is only visible to screen readers
        placeholder = { Text("Enter your email") }
    )
}
```

## Component API

### TextField

| Parameter              | Description                                     |
|------------------------|-------------------------------------------------|
| `value`                | The current text in the text field.             |
| `onValueChange`        | Callback when the text changes.                 |
| `modifier`             | Modifier to be applied to the text field.       |
| `editable`             | Whether the text field is editable.             |
| `cursorBrush`          | The brush to use for the cursor.                |
| `textStyle`            | Style to apply to the text.                     |
| `textAlign`            | Alignment of the text.                          |
| `lineHeight`           | Height of each line of text.                    |
| `fontSize`             | Size of the font.                               |
| `letterSpacing`        | Spacing between letters.                        |
| `fontWeight`           | Weight of the font.                             |
| `fontFamily`           | Family of the font.                             |
| `singleLine`           | Whether the text field is single line.          |
| `minLines`             | Minimum number of lines to display.             |
| `maxLines`             | Maximum number of lines to display.             |
| `keyboardActions`      | Actions for the keyboard.                       |
| `keyboardOptions`      | Options for the keyboard.                       |
| `visualTransformation` | Visual transformation for the text.             |
| `interactionSource`    | Interaction source for the text field.          |
| `content`              | Content composable that defines the text field's appearance. |

### TextFieldScope.TextInput

| Parameter              | Description                                     |
|------------------------|-------------------------------------------------|
| `modifier`             | Modifier to be applied to the text input.       |
| `shape`                | Shape of the text input.                        |
| `backgroundColor`      | Background color of the text input.             |
| `contentColor`         | Color of the text content.                      |
| `label`                | Accessibility label for the text input.         |
| `placeholder`          | Placeholder composable when the field is empty. |
| `leading`              | Leading composable to display.                  |
| `trailing`             | Trailing composable to display.                 |
| `verticalAlignment`    | Vertical alignment of the content.              |

## Icons and other icons

You can find the icons used in our examples and many other icons at [composeicons.com](https://composeicons.com).
