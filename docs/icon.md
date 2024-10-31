---
title: Icon
description: A component for rendering iconography with the tinting of your choice.
---
# Icon

A component for rendering iconography with the tinting of your choice.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../icon-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.19.1")
}
```

## Basic Example

Basic example using Icons from the Material Extended Library:

```kotlin
Icon(
    imageVector = Icons.Rounded.Favorite,
    contentDescription = "This song is in your favorites",
    tint = Color(0xFF9E9E9E),
)
```

<style>
.parameter {
    white-space: nowrap
}
</style>

## Parameters

### Icon

| Parameter                                          | Description                                                                                                                                           |
|----------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| <div class='parameter'>`painter`            </div> | a `Painter`  to draw inside this icon.                                                                                                                |
| <div class='parameter'>or `imageVector`     </div> | a `ImageVector` to draw inside this icon.                                                                                                             |
| <div class='parameter'>or `imageBitmap`     </div> | an `ImageBitmap` to draw inside this icon.                                                                                                            |
| <div class='parameter'>`contentDescription` </div> | text used by accessibility services to describe what this icon represents. This value can be ommited if the icon is used for stylistic purposes only. |
| <div class='parameter'>`modifier`           </div> | the `Modifier` to be used to this icon.                                                                                                               |
| <div class='parameter'>`tint`               </div> | a `Color` that will be used to tint the `painter`. If `Color.Unspecified` is passed, then no tinting will be used.                                    |

## Where to find icons

Great apps require great iconography. Visit [composeicons.com](https://composeicons.com) for a collection of over 7,000+ icons ready to be used in Jetpack Compose and Compose Multiplatform.

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>