---
title: Use your Android XML themes in Jetpack Compose
description: A step-by-step guide on using your Android XML themes in Jetpack Compose, using Compose Unstyled.
social_image: /og_xml_themes.png
---

This API is handy as you do not need to maintain two sources of truth (one being your XML themes and your Jetpack
Compose themes) during the migration process.

This guide teaches you how to setup your Compose Unstyled theme using your Android XML theme, and use its values in your
composables.

For the following guide, we will use this typical theme as a reference:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="AppTheme" parent="@style/Theme.NoActionBar">
        <item name="color_background">#FFFFFF</item>
        <item name="color_onBackground">#262626</item>
        <item name="color_primary">#3F51B5</item>
        <item name="color_onPrimary">#FFFFFF</item>

        <item name="textStyle_body">@style/Sans</item>

        <item name="spacing_small">4dp</item>
        <item name="spacing_medium">8dp</item>
        <item name="spacing_large">12dp</item>
    </style>

    <style name="Sans">
        <item name="android:textSize">18sp</item>
        <item name="android:fontFamily">@font/inter</item>
    </style>

    <attr name="color_background" format="color"/>
    <attr name="color_onBackground" format="color"/>
    <attr name="color_primary" format="color"/>
    <attr name="color_onPrimary" format="color"/>

    <attr name="textStyle_body" format="reference"/>

    <attr name="spacing_small" format="dimension"/>
    <attr name="spacing_medium" format="dimension"/>
    <attr name="spacing_large" format="dimension"/>

    <!-- Base theme that removes the action bar and makes the activity full screen-->
    <style name="Theme.NoActionBar" parent="">
        <item name="android:windowFullscreen">true</item>
        <item name="android:windowActionBar">false</item>
        <item name="android:windowNoTitle">true</item>
    </style>
</resources>
```

## Create your Compose theme

First off, let's create a Compose theme. It will be 'blank' for now. In the next steps it will be used as the bridge
between XML
and Compose.

Compose Unstyled comes with a theme builder function called `buildTheme {}`. It returns a `@Composable` theme
function that you can use to wrap your application content.

If you are coming from Material Compose, the result of `buildTheme {}` works the same way as Material's [
`MaterialTheme {}`](/docs/androidx.compose.material3/material3/components/MaterialTheme) function.

Let's create a blank theme and use it to wrap the contents of our app:

```kotlin
import com.composeunstyled.UnstyledButton
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.theme.buildTheme

val AppTheme = buildTheme { }

@Composable
fun App() {
    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText("Hello Styled World!")

            UnstyledButton(
                onClick = { },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(100)
            ) {
                BasicText("Click Me")
            }
        }
    }
}
```

![Android XML themed app before connecting colors](/composeunstyled-v2-assets/xml-theme-guide/step_0.png)

Did you notice that we use the [`Text`](typography.md) and [`Button`](button.md) components? These components
are automatically styled
based off your current theme. You are not force to use them, but they make styling a breeze.

## Use your XML colors in Compose

Now let's connect the XML world to the Jetpack Compose world.

Compose Unstyled comes with a `Theme` object, which is how you can reference values from the current theme. This is
similar to Material's `MaterialTheme` object, but in our case it's way more flexible.

Let's create a **colors** `ThemeProperty` and put some color `ThemeTokens` to it. We will use these tokens to populate
our theme and style our app:

```kotlin
val colors = ThemeProperty<Color>("colors")

val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("onBackground")
val primary = ThemeToken<Color>("primary")
val onPrimary = ThemeToken<Color>("onPrimary")
```

We can now use them in our theme function to read the values of our XML theme.

Compose Unstyled comes with `resolveThemeX()` composable functions so that you can read your XML theme values:

```kotlin
val AppTheme = buildTheme {
    // get a reference to the calling (themed) context
    val context = LocalContext.current

    // map your XML colors to Compose
    properties[colors] = mapOf(
        background to resolveThemeColor(context, R.attr.color_background),
        onBackground to resolveThemeColor(context, R.attr.color_onBackground),
        primary to resolveThemeColor(context, R.attr.color_primary),
        onPrimary to resolveThemeColor(context, R.attr.color_onPrimary),
    )
}

```

> **Note:** Compose Unstyled does not inflate any XML themes for you. The `resolveThemeX()` functions map the given
> context's theme attributes
> to Compose's. The `LocalContext` references the context from which you will call `AppTheme` from. For example, if you
> call it from your Activity's `setContent {}` function, it will inherit the `android:theme` of your
_AndroidManifest.xml_
> file.

We can now use our XML theme colors directly in Compose.

To access them, use the `Theme` object like this:

```kotlin
@Composable
fun App() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme[colors][background]),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProvideContentColor(Theme[colors][onBackground]) {
                BasicText("Hello Styled World!")

                UnstyledButton(
                    onClick = {},
                    backgroundColor = Theme[colors][primary],
                    contentColor = Theme[colors][onPrimary],
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(100)
                ) {
                    BasicText("Click Me")
                }
            }
        }
    }
}
```

![Android XML themed app with colors connected](/composeunstyled-v2-assets/xml-theme-guide/step_1.png)


Brief explanation of the above code:

- `Theme[colors][background]` returns the `background` token of the `colors` property. Similarly for `onBackground`,
  `primary` and `onPrimary`.
- The `ProvideContentColor()` function forwards the given `Color` to its children to render their contents with.
- The `Text` composable inherits the content color passed from the `ProvideContentColor` and renders its text using the
  `onBackground` color of our theme.
- We want our button to use the primary/onPrimary combo of the theme, so we use its `backgroundColor` and `contentColor`
  properties.

That's it. Now whenever you update your colors in your XML theme, the changes will be reflected in your composables.

## Use your XML dimens in Compose

Let's create some theme tokens for our spacing theme attributes, like we did for our colors:

```kotlin
val spacing = ThemeProperty<Dp>("spacing")
val small = ThemeToken<Dp>("small")
val medium = ThemeToken<Dp>("medium")
val large = ThemeToken<Dp>("large")
```

and now let's map them to our theme:

```kotlin
val AppTheme = buildTheme {
    // get a reference to the calling (themed) context
    val context = LocalContext.current

    // map your XML colors to Compose
    properties[colors] = mapOf(
        background to resolveThemeColor(context, R.attr.color_background),
        onBackground to resolveThemeColor(context, R.attr.color_onBackground),
        primary to resolveThemeColor(context, R.attr.color_primary),
        onPrimary to resolveThemeColor(context, R.attr.color_onPrimary),
    )

    // map your XML dimens to Compose
    properties[spacing] = mapOf(
        small to resolveThemeDp(context, R.attr.spacing_small),
        medium to resolveThemeDp(context, R.attr.spacing_medium),
        large to resolveThemeDp(context, R.attr.spacing_large),
    )
}

```

We can now use our spacing inside our app, using `Theme[spacing][small]`, `Theme[spacing][medium]` and
`Theme[spacing][large]`.

For our example let's put some spacing between our elements using a `Spacer`:

```kotlin
@Composable
fun App() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme[colors][background]),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProvideContentColor(Theme[colors][onBackground]) {
                BasicText("Hello Styled World!")

                Spacer(Modifier.height(Theme[spacing][large]))

                UnstyledButton(
                    onClick = {},
                    backgroundColor = Theme[colors][primary],
                    contentColor = Theme[colors][onPrimary],
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(100)
                ) {
                    BasicText("Click Me")
                }
            }
        }
    }
}
```

![Android XML themed app with custom colors](/composeunstyled-v2-assets/xml-theme-guide/step_2.png)

## Use your XML typography in Compose

Let's create theme tokens for our text appearance attributes:

```kotlin
val typography = ThemeProperty<TextStyle>("typography")
val body = ThemeToken<TextStyle>("body")
```

Now we can map our XML text appearance to our theme tokens using `resolveThemeTextAppearance`:

```kotlin
val AppTheme = buildTheme {
    // get a reference to the calling (themed) context
    val context = LocalContext.current

    // map your XML colors to Compose
    properties[colors] = mapOf(
        background to resolveThemeColor(context, R.attr.color_background),
        onBackground to resolveThemeColor(context, R.attr.color_onBackground),
        primary to resolveThemeColor(context, R.attr.color_primary),
        onPrimary to resolveThemeColor(context, R.attr.color_onPrimary),
    )

    // map your XML dimens to Compose
    properties[spacing] = mapOf(
        small to resolveThemeDp(context, R.attr.spacing_small),
        medium to resolveThemeDp(context, R.attr.spacing_medium),
        large to resolveThemeDp(context, R.attr.spacing_large),
    )

    // map your XML typography to Compose
    properties[textStyles] = mapOf(
        body to resolveThemeTextAppearance(context, R.attr.textStyle_body),
    )
}

```

Now you can use your XML typography in your composables using the new tokens and the `ProvideTextStyle` composable:

```kotlin
@Composable
fun App() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme[colors][background]),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProvideTextStyle(Theme[textStyles][body]) {
                ProvideContentColor(Theme[colors][onBackground]) {
                    BasicText("Hello Styled World!")

                    Spacer(Modifier.height(Theme[spacing][large]))

                    UnstyledButton(
                        onClick = {},
                        backgroundColor = Theme[colors][primary],
                        contentColor = Theme[colors][onPrimary],
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(100)
                    ) {
                        BasicText("Click Me")
                    }
                }
            }
        }
    }
}
```

![Android XML themed app with typography connected](/composeunstyled-v2-assets/xml-theme-guide/step_3.png)

The `resolveThemeTextAppearance` function automatically resolves:

- Font size (`android:textSize`)
- Font family (`android:fontFamily`) including custom fonts
- Font weight and style (`android:textStyle`)
- Text color (`android:textColor`)
- Text shadows (`android:shadowColor`, `android:shadowDx`, `android:shadowDy`, `android:shadowRadius`)

## Use the Material Ripple effect in Compose

The Material ripple is a signature of Android apps, and we highly recommend using it in your apps for that polished
touch effect.

For this, we provide a Compose Ripple Indication library:

```kotlin
// app/build.gradle.kts
implementation("com.composables:ripple-indication:1.0.0")
```

This introduces the `rememberRippleIndication()` function, that we can use in our compose theme:

```kotlin
val AppTheme = buildTheme {
    // get a reference to the calling (themed) context
    val context = LocalContext.current

    // map your XML colors to Compose
    val primary = resolveThemeColor(context, R.attr.color_primary)

    // create a ripple effect using the primary color
    defaultIndication = rememberRippleIndication(
        color = primary
    )
    properties[colors] = mapOf(
        background to resolveThemeColor(context, R.attr.color_background),
        onBackground to resolveThemeColor(context, R.attr.color_onBackground),
        primary to primary,
        onPrimary to resolveThemeColor(context, R.attr.color_onPrimary),
    )

    // map your XML dimens to Compose
    properties[spacing] = mapOf(
        small to resolveThemeDp(context, R.attr.spacing_small),
        medium to resolveThemeDp(context, R.attr.spacing_medium),
        large to resolveThemeDp(context, R.attr.spacing_large),
    )

    // map your XML typography to Compose
    properties[textStyles] = mapOf(
        body to resolveThemeTextAppearance(context, R.attr.textStyle_body),
    )
}
```

and rerun the app:

<div class="w-full !h-[600px] not-prose bg-neutral-200 p-4 shape-medium mt-4 unstyled-xml-preview">
    <video autoplay muted loop class="h-full mx-auto !bg-transparent shape-medium unstyled-xml-preview-media unstyled-xml-preview-video">
        <source src="/composeunstyled-v2-assets/xml-theme-guide/ripple_effect.mp4" type="video/mp4">
    </video>
</div>

---

<ApiReference id="android-xml-themes" />
