---
title: Unstyled Jetpack Compose Separator components
description: Horizontal and Vertical separator component for visually separating content.
---

# Separator

Separator component that render a horizontal or vertical line to separate other components.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../dividers-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.11.2")
}
```

## Basic Example

Basic example showing a horizontal divider:

```kotlin
HorizontalDivider(color = Color(0xFF9E9E9E))

VerticalDivider(color = Color(0xFF9E9E9E))
```

<style>
.parameter {
    white-space: nowrap
}
</style>

## Parameters

### VerticalDivider / HorizontalDivider

| Parameter                                | Description                                      |
|------------------------------------------|--------------------------------------------------|
| <div class='parameter'>`color`</div>     | the `Color` of the divider.                      |
| <div class='parameter'>`thickness`</div> | a `Dp` of how thick the divider should rendered. |
| <div class='parameter'>`modifier` </div> | the `Modifier` to be used to this divider.       |

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>