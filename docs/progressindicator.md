---
title: Progress Indicator
description: A foundational component used to display progress in a linear bar format in Compose Multiplatform.
---

# Progress Indicator

A foundational component used to display progress in a linear bar format in Compose Multiplatform.

Accessible out of the box and fully renderless, so that you can apply any styling you like.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../progressindicator-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.30.0")
}
```

## Basic Example

To create a progress indicator use the `ProgressIndicator` component. It sets up the required accessibility semantics according to the given `progress`.

We also provide the `ProgressBar` component which renders the given progress.

```kotlin
ProgressIndicator(
    progress = 0.5f,
    modifier = Modifier.width(400.dp).height(24.dp).shadow(4.dp, RoundedCornerShape(100)),
    shape = RoundedCornerShape(100),
    backgroundColor = Color(0xff176153),
    contentColor = Color(0xffb6eabb),
) {
    Box(Modifier.fillMaxWidth(0.5f).fillMaxHeight().background(color, shape))
}
```

## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (
according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the
order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters or optional components.

### Styling the track and fill

The `ProgressIndicator` is renders the track of the progress bar, and any related styling can be passed as parameters.

It is up to you to render the progress (usually fill) any way you like, by passing a composable as the indicator's content.

The optional `ProgressBar` component can be used as a batteries-included progress bar component if you need a traditional looking progress bar in your app.

## Component API

### ProgressIndicator

| Parameter         | Description                                                     |
|-------------------|-----------------------------------------------------------------|
| `progress`        | (Optional) A float value between 0 and 1 representing the progress. Skipping this value, will treat the progress as intermediate for accessibility purposes.       |
| `modifier`        | Modifier to be applied to the progress bar.                     |
| `shape`           | Shape of the progress bar.                                      |
| `backgroundColor` | Background color of the progress bar.                           |
| `contentColor`    | Color of the content within the progress bar.                   |
| `content`         | A composable function that defines the content of the progress indicator. |

### ProgressBar

| Parameter | Description                                                     |
|-----------|-----------------------------------------------------------------|
| `shape`   | Shape of the progress bar.                                      |
| `color`   | Color of the progress portion of the bar.                       |

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
}
</style>
