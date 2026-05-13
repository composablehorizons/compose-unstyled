---
title: Create Custom Themes
description: Learn how to create fully custom themes and how to use them to maintain consistent styling in your Compose apps.
---

## Installation

Include the Theming module in your app's dependencies:

```kotlin
implementation("com.composables:composeunstyled-theming")
```

## Create a theme

To create a theme, use the `buildTheme { }` function.

```kotlin
val MyTheme = buildTheme { }
```

It returns a theming `@Composable` that you can use to wrap your app with:

```kotlin
@Composable
fun App() {
    MyTheme {
        Box(Modifier.fillMaxSize()) {
            BasicText("My awesome app")
        }
    }
}
```

The theming function we just created forwards any styling properties to its children.
Children can access those properties using the `Theme` object. Those are usually **colors**, **typography**, **shapes**
and anything you need to style your apps with.

But we haven't defined any, so let's do that next:

## Customize your theme

Let's define some colors. To do this, let's create a 'colors' **theme property**.

Theme Properties hold a `Map` of **theme tokens**. This links the tokens to the actual values.

```kotlin
val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("on_background")

val MyTheme = buildTheme {
    properties[colors] = mapOf(
        background to Color(0xFFFAFAFA),
        onBackground to Color(0XFF0C0A09),
    )
}
```

Compose Unstyled does not force the structure of your themes and does not come with default styling options that you
will end up removing afterwards.

You can create any kind of properties you need that fit your design needs.

> Even though `buildTheme` is not a `@Composable` function, the scope it provides for defining your properties is. This
> is handy for when you need to prepare properties asynchronously without blocking the UI thread (such as loading fonts)
> and creating [dynamic themes](dynamic-themes.md).

## Styling your app using your theme

We can now style our app using the `Theme` object to access the values for each token:

```kotlin
@Composable
fun App() {
    MyTheme {
        Box(Modifier.fillMaxSize().background(Theme[colors][background])) {
            BasicText("My awesome app", style = TextStyle(color = Theme[colors][onBackground]))
        }
    }
}
```

And that's the gist. You now know how to create custom themes, customize them with the properties you need, and use them
in your app.

## Default properties

Compose Unstyled themes come with some default properties you can set to style your app.

### Content Color

Sets the default `Color` used by all Unstyled [components](/compose-unstyled/docs/components) to render their content.

```kotlin
val MyTheme = buildTheme {
    defaultContentColor = Color(0XFF0C0A09)
}
```

### Text Style

Sets the default `TextStyle` used by [Text](typography.md) and [TextField](textfield.md) components to render their text
contents.

```kotlin
val MyTheme = buildTheme {
    defaultTextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
}
```

### Indication

Sets the default `Indication` used by interactive elements such as buttons and clickables. This is the same as providing
an `Indication` value for the `LocalIndication`.

```kotlin
import com.composeunstyled.theme.rememberColoredIndication

val MyTheme = buildTheme {
    defaultIndication = rememberColoredIndication(
        hoveredColor = Color.White.copy(alpha = 0.3f),
        pressedColor = Color.White.copy(alpha = 0.5f),
        focusedColor = Color.Black.copy(alpha = 0.1f),
    )
}
```

### TextSelectionColors

Controls the colors used by text and text fields for text selection. This is the same as providing a
`TextSelectionColors` value for the `LocalTextSelectionColors`.

```kotlin
val MyTheme = buildTheme {
    defaultTextSelectionColors = TextSelectionColors(
        handleColor = Color.Blue,
        backgroundColor = Color.Blue.copy(alpha = 0.4f)
    )
}
```

## Debugging your theme

Unstyled will throw an exception when you try to access a token that is not present in the current theme.

To make it simpler to debug such scenarios, it is highly recommended to name your themes when you create them.

By doing so, Unstyled will provide descriptive error messages when you try to access a token that does not exist during
runtime.

```kotlin
val LightTheme = buildTheme {
    name = "LightTheme"
}
```
