---
title: Progress Indicator
description: A foundational component used to display progress in a linear bar format in Jetpack Compose and Compose Multiplatform. Accessible out of the box and fully renderless, so that you can apply any styling you like.
---

<UnstyledDemo id="progressindicator" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-progress")
```

## Basic Example

To create a progress indicator use the `UnstyledProgress` component. It sets up the required accessibility semantics according to the given `progress`.

The `Indicator` child component fills itself to the current progress.

```kotlin
UnstyledProgress(
    progress = 0.5f,
    modifier = Modifier
        .width(400.dp)
        .height(24.dp)
        .dropShadow(
            shape = RoundedCornerShape(100),
            shadow = Shadow(
                radius = 8.dp,
                spread = 0.dp,
                color = Color.Black.copy(alpha = 0.14f),
                offset = DpOffset(x = 0.dp, y = 2.dp)
            )
        )
        .background(Color(0xff176153), RoundedCornerShape(100)),
) {
    Indicator(
        Modifier.background(
            color = Color(0xffb6eabb),
            shape = RoundedCornerShape(100)
        )
    )
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

`UnstyledProgress` owns the accessibility semantics and exposes the current progress to its content.

It is up to you to render the progress track and fill any way you like. Apply track styling to the `UnstyledProgress` modifier and fill styling to `Indicator`.

<ApiReference id="progressindicator" />
