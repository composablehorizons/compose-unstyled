---
title: Separators
description: Horizontal and Vertical separator component for visually separating content.
---

# Separator

A simple foundational separator component that shows a line to separate other components.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../separators-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
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

Basic example showing a horizontal separator:

```kotlin
HorizontalSeparator(color = Color(0xFF9E9E9E))

VerticalSeparator(color = Color(0xFF9E9E9E))
```

<style>
.parameter {
    white-space: nowrap
}
</style>

## Parameters

### VerticalSeparator / HorizontalSeparator

| Parameter                                | Description                                        |
|------------------------------------------|----------------------------------------------------|
| <div class='parameter'>`color`</div>     | the `Color` of the separator.                      |
| <div class='parameter'>`thickness`</div> | a `Dp` of how thick the separator should rendered. |
| <div class='parameter'>`modifier` </div> | the `Modifier` to be used to this separator.       |

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>