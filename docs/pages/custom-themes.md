---
title: Custom Themes
seoTitle: Create custom Jetpack Compose Themes
description: Learn how to create fully custom themes and how to use them to maintain consistent styling in your Jetpack Compose apps.
---

## Installation

Include the Theming module in your app's dependencies:

```kotlin
implementation("com.composables:composeunstyled-theming:2.9.2")
```

## Create a theme

To create a theme, use the `buildThemeV2 { }` function.

<aside class="docs-theme-compatibility-note">
  <code>buildTheme {}</code> also exists for compatibility with existing themes. <code>buildThemeV2 {}</code> adds color-scheme support and is
  recommended for new themes.
</aside>

```kotlin
val MyTheme = buildThemeV2 {
    name = "MyTheme"
}
```

It returns a theme that you can invoke as a composable to wrap your app with:

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

To define light, dark, or custom variants of this theme, see [Color Schemes](color-schemes.md).

The theme makes the values you define available to its content.
Content can access those values using the `Theme` object. Those are usually **colors**, **typography**, **shapes**
and anything you need to style your apps with.

But we haven't defined any, so let's do that next:

## Define theme values

Let's define some colors. To do this, let's create a 'colors' **theme property**.

Theme Properties hold a `Map` of **theme tokens**. This links the tokens to the actual values.

```kotlin
val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("on_background")

val MyTheme = buildThemeV2 {
    properties[colors] = mapOf(
        background to Color(0xFFFAFAFA),
        onBackground to Color(0XFF0C0A09),
    )
}
```

Compose Unstyled does not force the structure of your themes and does not come with default styling options that you
will end up removing afterwards.

You can create any kind of properties you need that fit your design needs.

> Even though `buildThemeV2` is not a `@Composable` function, the scope it provides for defining your properties is. This
> is handy for when you need to prepare properties asynchronously without blocking the UI thread (such as loading fonts)
> and creating [dynamic themes](dynamic-themes.md).

## Reading theme values

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

To set defaults for theme content or override values for a subtree, see [Theme Values](theme-values.md).

## Debugging your theme

Unstyled will throw an exception when you try to access a token that is not present in the current theme.

To make it simpler to debug such scenarios, it is highly recommended to name your themes when you create them.

By doing so, Unstyled will provide descriptive error messages when you try to access a token that does not exist during
runtime.

```kotlin
val LightTheme = buildThemeV2 {
    name = "LightTheme"
}
```
