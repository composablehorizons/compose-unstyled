# Icon

An unstyled Icon component for Jetpack Compose & Composable Multiplatform for rendering iconography tinting of your
choice.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid lightgray;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../icon-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.7.0")
}
```

## Basic Example

Basic example using Icons from the Material Extended Library:

```kotlin
Icon(
    imageVector = Icons.Rounded.Favorite,
    contentDescription = null,
    tint = Color(0xFF9E9E9E),
)
```

## Parameters

### Icon

| Parameter            | Description                                                                                                                                           |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `painter`            | a `Painter`  to draw inside this icon.                                                                                                                |
| or `imageVector`     | a `ImageVector` to draw inside this icon.                                                                                                             |
| or `imageBitmap`     | an `ImageBitmap` to draw inside this icon.                                                                                                            |
| `contentDescription` | text used by accessibility services to describe what this icon represents. This value can be ommited if the icon is used for stylistic purposes only. |
| `modifier`           | the `Modifier` to be used to this icon.                                                                                                               |
| `tint`               | a `Color` that will be used to tint the `painter`. If `Color.Unspecified` is passed, then no tinting will be used.                                    |

## Where to find Icons for your Compose apps

Great apps require great iconography. You can use the [SVG to Compose](https://www.composables.com/svgtocompose) tool to
convert your beautiful SVGs to Compose `ImageVectors`.

We also recommend the following icon sets:

- [Material Compose Symbols](https://www.composables.com/icons)
- [Lucide](https://lucide.dev/)

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>