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
    implementation("com.composables:core:1.30.0")
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
