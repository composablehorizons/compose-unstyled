---
title: Theming
description: Create fully custom themes in Compose UI
---

# Custom Themes

Strongly typed, flexible, dynamic themes with the design tokens of your choosing.

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.38.1")
}
```

## Basic Example

Use the `buildTheme()` function to create a `@Composable` theme function. To use the theme, wrap your app's content with
the created function.

Pass any new theme property into the theme according to your design system tokens.

You can access those properties to style your components using the `Theme` object.

```kotlin
// define the properties you care about
val colors = ThemeProperty<Color>("colors")
val accent = ThemeToken<Color>("accent")

// create a theme and assign values to tokens
val MyTheme = buildTheme {
    properties[colors] = mapOf(
        accent to Color.Black,
    )
}

// use the theme to style your app
@Composable
fun App() {
    MyTheme {
        Text("Hello Beautiful world!", color = Theme[colors][accent])
    }
}
```

## Default Properties

The default properties of your theme are applied automatically when using the theme composable.

These values are used to populate the respective `CompositionLocal` down the hierarchy tree.

```kotlin
val MyTheme = buildTheme {
    defaultContentColor = Color.Black

    defaultTextStyle = TextStyle(fontSize = 22.sp)

    defaultTextSelectionColors = TextSelectionColors(
        handleColor = Color.Red,
        backgroundColor = Color.White
    )
}
```

## Debugging your theme

You can define a name for your theme. This is optional but recommended, especially when using multiple themes in your
app (ie light/dark or nesting themes)

This will include the name of the resolved theme in error logs, when you try to access an attribute or property that
does not exist in the current theme:

```kotlin
val colors = ThemeProperty<Color>("colors")
val accent = ThemeToken<Color>("accent")

val LightTheme = buildTheme {
    name = "LightTheme"
    properties[colors] = mapOf(
        accent to Color.Black,
    )
}
val DarkTheme = buildTheme {
    name = "DarkTheme"
}

@Composable
fun App() {
    val AppTheme = if (isSystemInDarkTheme()) DarkTheme else LightTheme

    AppTheme {
        val color = Theme[colors][accent] // 💥 error while using DarkTheme
    }
}
```

this will throw an error with the following message:
`Tried to access the value of the token called primary, but no tokens with that name are defined within the colors property. You probably forgot to set a primary token in your theme definition. The resolved theme was DarkTheme`

## Code Examples

### Specify custom color pallets

Create multiple colors based of your needs:

```kotlin
val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("onBackground")

val MyTheme = buildTheme {
    properties[colors] = mapOf(
        background to Color.White,
        onBackground to Color.Black
    )
}

// use the theme to style your app
@Composable
fun App() {
    MyTheme {
        Box(Modifier.background(Theme[colors][background])) {
            Text("Hello", color = Theme[colors][onBackground])
        }
    }
}
```

### Specify custom theme properties

Specify the type you need when defining your theme properties:

```kotlin
val transitions = ThemeProperty<Int>("transitions")
val fast = ThemeToken<Int>("fast")
val faster = ThemeToken<Int>("faster")

val MyTheme = buildTheme {
    properties[transitions] = mapOf(
        fast to 200,
        faster to 300,
    )
}

@Composable
fun App() {
    MyTheme {
        val transitionSpeed = Theme[transitions][faster]
        // ..
    }
}
```

### Use custom fonts

The `buildTheme { }` function gives you a `@Composable` scope and will recompose your theme when a value changes.

This is handy when using Compose Multiplatform resources to load custom fonts.

```kotlin
val MyTheme = buildTheme {
    defaultTextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Inter)),
        fontSize = 22.sp
    )
}

@Composable
fun ThemeDemo() {
    MyTheme {
        Column {
            Text("This text will use a custom font")
        }
    }
}
```

### Create a custom theme with Material ripple effect

Use the `defaultIndication` property of your theme to provide the ripple effect.

Note that the ripple effect is part of the Material 3 Compose library:

```kotlin
import androidx.compose.material3.ripple

val MyTheme = buildTheme {
    defaultIndication = ripple()
}

@Composable
fun ThemeDemo() {
    MyTheme {
        Button(onClick = { }, contentPadding = PaddingValues(10.dp)) {
            Text("This button has the ripple click effect")
        }
    }
}
```

### Create dynamic themes

The `buildTheme { }` gives you a `@Composable` scope that will recompose your theme when any of the provided values
changes.

The following code creates a theme that matches the system preferences of light/dark theme. 

When the user updates their system preferences, the theme will be updated automatically:

```kotlin
val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("onBackground")

val MyTheme = buildTheme {
    val isSystemDark = if (isSystemInDarkTheme()) DarkTheme else LightTheme
    
    properties[colors] = if (isSystemDark) {
        mapOf(
            background to Color.Black,
            onBackground to Color.White
        )
    } else {
        mapOf(
            background to Color.White,
            onBackground to Color.Black
        )
    }
}

```

## Theming API

### buildTheme

Creates a theme composable function that can be used to wrap your app's content.

| Parameter      | Description                                                                 |
|----------------|-----------------------------------------------------------------------------|
| `themeAction`  | A composable lambda that configures the theme properties and defaults.      |

**Returns:** A `@Composable` function that takes content and applies the theme.

### ThemeBuilder

The DSL builder class used within the `buildTheme` lambda to configure theme properties.

| Property                    | Type                    | Description                                                                 |
|----------------------------|-------------------------|-----------------------------------------------------------------------------|
| `name`                     | `String`                | The name of the theme, used for debugging and error messages.              |
| `defaultIndication`        | `Indication?`           | The default indication to use for interactive elements.                    |
| `defaultTextStyle`         | `TextStyle`             | The default text style to apply throughout the theme.                      |
| `defaultContentColor`      | `Color`                 | The default content color to use for text and icons.                       |
| `defaultTextSelectionColors` | `TextSelectionColors?` | The default colors for text selection.                                     |
| `properties`               | `MutableThemeProperties` | The container for custom theme properties and their values.                |

### ThemeProperty

A data class that represents a theme property of a specific type.

| Parameter | Type | Description |
|-----------|------|-------------|
| `name`    | `String` | The name of the theme property. |

### ThemeToken

A data class that represents a specific token within a theme property.

| Parameter | Type | Description |
|-----------|------|-------------|
| `name`    | `String` | The name of the theme token. |

### Theme

An object that provides access to theme values within a composable scope.

| Method | Description |
|--------|-------------|
| `get(property: ThemeProperty<T>): ThemeValues<T>` | Retrieves the values for a specific theme property. |

### ThemeValues

A class that provides access to specific token values within a theme property.

| Method | Description |
|--------|-------------|
| `get(token: ThemeToken<T>): T` | Retrieves the value for a specific theme token. |

### MutableThemeProperties

A container class that manages theme properties and their associated values.

| Method | Description |
|--------|-------------|
| `set(property: ThemeProperty<T>, values: Map<ThemeToken<T>, T>)` | Sets the values for a specific theme property. |