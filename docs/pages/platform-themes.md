---
title: Platform Themes
description: Native look and feel on every platform with one line of code. Platform Themes set beautiful styling defaults based on the platform your app is running on.
---

## Installation

Include the Platform Theme module in your app's dependencies:

```kotlin
implementation("com.composables:composeunstyled-platformtheme")
```

## Basic usage

Use the `buildPlatformTheme` function to create your theme. Then wrap your app with the new theme and you are all set.

<UnstyledDemo id="platform-theme" />

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledButton
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.platformtheme.buildPlatformTheme
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.EmojiVariant
import com.composeunstyled.platformtheme.heading5
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.platformtheme.interactiveSizes
import com.composeunstyled.platformtheme.interactiveSize
import com.composeunstyled.platformtheme.roundedFull
import com.composeunstyled.platformtheme.shapes
import com.composeunstyled.platformtheme.sizeDefault
import com.composeunstyled.platformtheme.text8
import com.composeunstyled.platformtheme.textStyles
import com.composeunstyled.platformtheme.WebFontOptions
import com.composeunstyled.theme.Theme
```

```kotlin
val AppTheme = buildPlatformTheme(
    webFontOptions = WebFontOptions(
        emojiVariant = EmojiVariant.Colored
    )
)

@Composable
fun App() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText("🥰✌️🐢🐇", style = Theme[textStyles][text8])
            BasicText(
                text = "Beautiful styling defaults on every platform",
                style = Theme[textStyles][heading5]
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnstyledButton(
                    onClick = { },
                    contentPadding = PaddingValues(
                        horizontal = 16.dp, vertical = 8.dp
                    ),
                    shape = Theme[shapes][roundedFull],
                    backgroundColor = Color(0xFF3B82F6),
                    indication = Theme[indications][dimmed],
                    modifier = Modifier
                        .interactiveSize(Theme[interactiveSizes][sizeDefault])
                ) {
                    BasicText("Get Started", style = TextStyle(color = Color.White))
                }
            }
        }
    }
}
```

## Typography

Platform Themes automatically apply text styles to every [`Text`](typography.md) and [`TextField`](textfield.md) child.

You can also make use of the theme's text style using the `LocalTextStyle` composition local.

### Typography Tokens

We provide the `text` and `heading` typography tokens with 9 different sizes each. Each size is either defined in each platform's design guidelines (for example, Material for Android or HIG for Apple) or is a close approximation of one that is defined.

<UnstyledDemo id="platform-theme" />

This way, you can make sure that the sizing of your app feels cohesive on every platform without sweating the details.

By default, scale number `4` is applied when you use the Theme. If you need to render text
smaller than that, use a smaller scale. If you need larger text, use a bigger number.

| Scale      | Android | iOS  | Desktop | Web  |
|------------|---------|------|---------|------|
| `1`        | 11sp    | 12sp | 10sp    | 10sp |
| `2`        | 12sp    | 13sp | 11sp    | 12sp |
| `3`        | 14sp    | 16sp | 12sp    | 14sp |
| `4` (base) | 16sp    | 17sp | 13sp    | 16sp |
| `5`        | 22sp    | 18sp | 14sp    | 18sp |
| `6`        | 24sp    | 20sp | 15sp    | 20sp |
| `7`        | 28sp    | 22sp | 17sp    | 24sp |
| `8`        | 32sp    | 28sp | 22sp    | 28sp |
| `9`        | 36sp    | 34sp | 26sp    | 35sp |

### Using system fonts

Platform Themes automatically apply system fonts on every platform. This way, you get the default typography on every platform without us having to bundle font files and increase the size of your app.

The exception to this is the Web platform. Browsers do not currently have access to the computer's installed fonts outside of CSS.

Because of this technical limitation, we bundle [Noto Sans](https://fonts.google.com/noto/specimen/Noto+Sans) on Web.

Noto Sans is a global font that comes with variations with pretty much every script out there.

## Displaying non-Latin text on Web

Use `webFontOptions` while building your Platform Theme to specify the scripts that your app needs.

<UnstyledDemo id="platform-theme" />

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.platformtheme.buildPlatformTheme
import com.composeunstyled.platformtheme.heading5
import com.composeunstyled.platformtheme.SpokenLanguage
import com.composeunstyled.platformtheme.textStyles
import com.composeunstyled.platformtheme.WebFontOptions
import com.composeunstyled.theme.Theme
```

```kotlin
val AppTheme = buildPlatformTheme(
    webFontOptions = WebFontOptions(
        supportedLanguages = listOf(SpokenLanguage.Japanese)
    )
)

@Composable
fun App() {
    AppTheme {
        BasicText("海賊王に俺はなる", style = Theme[textStyles][heading5])
    }
}
```

<p class="border-l-4 pl-4 border-orange-500 flex flex-col gap-2 unstyled-platform-warning unstyled-warning">
<strong class="text-orange-800 unstyled-platform-warning-title unstyled-warning-title">⚠️ Use this API with caution</strong>
<span>Compose Web will cause your app to freeze while big sized fonts are being loaded for the first time. They are then cached by the browser. Only use the scripts that you need to reduce unresponsiveness.</span>
</p>

We currently support Japanese, Korean, Chinese Traditional and Chinese Simplified. If there is a script you would like us to support, feel free to request it via a GitHub issue.

## Displaying emojis on Web

Use `webFontOptions` while building your Platform Theme to specify the emoji variant you would like to use.

By default, `Monochrome` is used as it is a good compromise between having emojis and speed:

<UnstyledDemo id="platform-theme" />

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.platformtheme.buildPlatformTheme
import com.composeunstyled.platformtheme.EmojiVariant
import com.composeunstyled.platformtheme.heading8
import com.composeunstyled.platformtheme.textStyles
import com.composeunstyled.platformtheme.WebFontOptions
import com.composeunstyled.theme.Theme
```

```kotlin
val AppTheme = buildPlatformTheme(
    webFontOptions = WebFontOptions(
        emojiVariant = EmojiVariant.Colored
    )
)

@Composable
fun App() {
    AppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            BasicText("🎉 🚀 ❤️ 🌟 🎨", style = Theme[textStyles][heading8])
        }
    }
}
```

## Indications

Platform Themes apply an `indication` to their children according to each platform's look and feel.

We provide two Theme tokens: `bright` and `dimmed`. The default indication is `bright`.

Use `platformIndication` when a component should ask for platform-native interaction feedback directly:

```kotlin
import androidx.compose.ui.graphics.Color
import com.composeunstyled.platformtheme.platformIndication
```

```kotlin
val brightIndication = platformIndication(Color.White.copy(alpha = 0.18f))
val dimmedIndication = platformIndication(Color.Black.copy(alpha = 0.08f))
```

Compose Unstyled applies the provided color to the platform indication where the platform supports
it.

<div class="grid grid-cols-2 gap-8 my-8 unstyled-platform-indications-grid">
  <div class="flex flex-col items-center gap-2 unstyled-platform-indication-item">
    <h4 class="text-center font-semibold unstyled-platform-indication-title">Android</h4>
    <video src="/composeunstyled-v2-assets/android_touch.mp4" playsinline loop autoplay muted ></video>
  </div>
  <div class="flex flex-col items-center gap-2 unstyled-platform-indication-item">
    <h4 class="text-center font-semibold unstyled-platform-indication-title">iOS</h4>
    <video src="/composeunstyled-v2-assets/ios_touch.mp4" playsinline loop autoplay muted ></video>
  </div>
  <div class="flex flex-col items-center gap-2 unstyled-platform-indication-item">
    <h4 class="text-center font-semibold unstyled-platform-indication-title">Desktop</h4>
    <video src="/composeunstyled-v2-assets/desktop_touch.mp4" playsinline loop autoplay muted ></video>
  </div>
  <div class="flex flex-col items-center gap-2 unstyled-platform-indication-item">
    <h4 class="text-center font-semibold unstyled-platform-indication-title">Web</h4>
    <video src="/composeunstyled-v2-assets/web_touch.mp4" playsinline loop autoplay muted ></video>
  </div>
</div>

## Interaction sizes

Platform Themes provide interaction size tokens that ensure your interactive elements meet accessibility standards on every platform. The sizing comes from each platform's design guidelines, ensuring optimal usability whether users are tapping on a touchscreen or clicking with a mouse.

We provide two size tokens: `sizeDefault` and `sizeMinimum`.

| Token         | Android | iOS  | Desktop | Web  |
|---------------|---------|------|---------|------|
| `sizeDefault` | 48dp    | 44dp | 28dp    | 28dp |
| `sizeMinimum` | 32dp    | 28dp | 20dp    | 20dp |

Use the `interactiveSize` modifier to apply these sizes to your interactive elements:

```kotlin
import com.composeunstyled.UnstyledButton
import androidx.compose.foundation.text.BasicText
import com.composeunstyled.platformtheme.interactiveSize
import com.composeunstyled.platformtheme.interactiveSizes
import com.composeunstyled.platformtheme.sizeDefault
import com.composeunstyled.theme.Theme
```

```kotlin
UnstyledButton(
    onClick = { /* ... */ },
    modifier = Modifier.interactiveSize(Theme[interactiveSizes][sizeDefault])
) {
    BasicText("Click me")
}
```

This ensures your buttons, checkboxes, and other interactive elements are always sized appropriately for the platform they're running on.

## Shapes

Shape theme tokens are not platform specific, however they are very handy when building apps.

| Token          | Radius |
|----------------|--------|
| `roundedNone`  | 0dp    |
| `roundedSmall` | 4dp    |
| `roundedMedium`| 6dp    |
| `roundedLarge` | 8dp    |
| `roundedFull`  | 100%   |

```kotlin
val AppTheme = buildPlatformTheme()

AppTheme {
    Box(modifier = Modifier.size(60.dp).background(Color(0xFF3B82F6), Theme[shapes][roundedNone]))
    Box(modifier = Modifier.size(60.dp).background(Color(0xFF3B82F6), Theme[shapes][roundedSmall]))
    Box(modifier = Modifier.size(60.dp).background(Color(0xFF3B82F6), Theme[shapes][roundedMedium]))
    Box(modifier = Modifier.size(60.dp).background(Color(0xFF3B82F6), Theme[shapes][roundedLarge]))
}
```

<UnstyledDemo id="platform-theme" />

<ApiReference id="platform-themes" />
