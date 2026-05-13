---
title: Text Field
description: A themable component to build text fields with the styling of your choice. Supports placeholders, leading and trailing slots out of the box along with accessibility labels (visual or not).
---

<UnstyledDemo id="textfield" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-text-field")
```

## Basic Example

To create a text field, use the `TextField` component with `TextFieldState` and use the `TextInput()` component within its content scope.

The `TextField` itself groups together every part of the text field in order to make sense for screen reader technology.

Other than the `TextInput` which handles the part of the text field the user can enter text at, it can contain elements
such as `Text` for a visual label.

```kotlin
val state = remember { TextFieldState() }

UnstyledTextField(
    state = state,
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
val state = remember { TextFieldState() }

CompositionLocalProvider(LocalTextStyle provides TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)) {
    Column {
        // this text field will use the provided Text Style
        UnstyledTextField(
            state = state,
        ) {
            // the provided text style will be used in the text input
            TextInput()
        }
        BasicText("So will this text")

        BasicText("This text is also styled, but slightly modified", style = TextStyle(letterSpacing = 2.sp))

        BasicText("This text is completely different", style = TextStyle())
    }
}
```

### Specifying Input Type

You can specify the input type for the `TextField` using the `keyboardOptions` parameter. For example, to specify an
email input type:

```kotlin
val state = remember { TextFieldState() }

UnstyledTextField(
    state = state,
    modifier = Modifier.fillMaxWidth(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
) {
    TextInput(
        placeholder = { BasicText("Email") }
    )
}
```

### Adding a Trailing Icon

You can add a trailing icon to the `TextField` to provide additional functionality, such as toggling password
visibility:

```kotlin
val state = remember { TextFieldState() }
var showPassword by remember { mutableStateOf(false) }

UnstyledTextField(
    state = state,
) {
    TextInput(
        placeholder = { BasicText("Password") },
        trailing = {
            UnstyledButton(
                onClick = { showPassword = !showPassword },
                backgroundColor = Color.Transparent,
                contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                UnstyledIcon(
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
val state = remember { TextFieldState() }
var showPassword by remember { mutableStateOf(false) }

UnstyledTextField(
    state = state,
    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
) {
    TextInput(
        placeholder = { BasicText("Password") },
        trailing = {
            UnstyledButton(
                onClick = { showPassword = !showPassword },
                backgroundColor = Color.Transparent,
                contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                UnstyledIcon(
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
val state = remember { TextFieldState() }

UnstyledTextField(
    state = state,
) {
    TextInput(
        label = "Email address", // This label is only visible to screen readers
        placeholder = { BasicText("Enter your email") }
    )
}
```

<ApiReference id="textfield" />
