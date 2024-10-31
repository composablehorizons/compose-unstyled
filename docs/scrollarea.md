---
title: Scroll Area (with Scrollbars)
description: Scroll Area can be used to add Scrollbars to any scrollable components.
---

# Scroll Area (Scrollbars)

Scroll Area can be used to add Scrollbars to any scrollable components.

Comes with foundational, renderless vertical and horizontal `Scrollbar` components with tons of customization options.

Supports scrollbars for `Column`, `Row`, `LazyColumn`, `LazyRow`, `LazyVerticalGrid` and `LazyHorizontalGrid`, fully
customizable styling and custom overflow effects.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../scrollarea-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
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

A scroll area consists of the following components: `ScrollArea`, `VerticalScrollbar`, `HorizontalScrollbar`
and `Thumb`.

The `ScrollArea` wraps a scrollable component (i.e. a `Column`, a `LazyRow` or `LazyGrid`)
and maintains information about their scrolled position.

The `VerticalScrollbar`/`HorizontalScrollbar` components represent the track of the scrollbar and accept a thumb
component.

The `Thumb` represents the thumb of a scrollbar and will automatically sync their position and size according to the
scrolled position of the `ScrollArea`.

Here is a simple example of adding vertical scrollbar to a `LazyColumn`:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollAreaState(lazyListState)

ScrollArea(state = state) {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.TopEnd)
            .fillMaxHeight()
            .width(4.dp)
    ) {
        Thumb(Modifier.background(Color.LightGray))
    }
}
```

## Code Examples

### Add scrollbars to LazyLists

Wrap your `LazyColumn` or `LazyList` with a `ScrollArea` and pass its state to the scroll area's state:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollAreaState(lazyListState)

ScrollArea(state = state) {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.TopEnd)
            .fillMaxHeight()
            .width(4.dp)
    ) {
        Thumb(Modifier.background(Color.LightGray))
    }
}
```

### Add scrollbars to LazyGrids

Wrap your `LazyVerticalGrid` or `LazyHorizontalGrid` with a `ScrollArea` and pass its state to the scroll area's state:

```kotlin
val lazyGridState = rememberLazyGridState()
val state = rememberScrollAreaState(lazyGridState)

ScrollArea(state = state) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = lazyGridState,
        modifier = Modifier.fillMaxSize()
    ) {
        repeat(50) { i ->
            item {
                Box(
                    Modifier.padding(4.dp).size(48.dp).background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(i.toString())
                }
            }
        }
    }
    VerticalScrollbar(modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()) {
        Thumb(
            modifier = Modifier.background(Color.Black.copy(0.3f), RoundedCornerShape(100)),
        )
    }
}
```

### Add scrollbars to Column and Row

Wrap your `Column` or `Row` with a `ScrollArea` and pass the scroll state you used in your `vertical` state to the
scroll area's state:

```kotlin
val scrollState = rememberScrollState()
val state = rememberScrollAreaState(scrollState)

ScrollArea(state = state) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        repeat(50) { i ->
            BasicText(i.toString())
        }
    }
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()
    ) {
        Thumb(
            modifier = Modifier.background(
                Color.Black.copy(0.3f), RoundedCornerShape(100)
            ),
        )
    }
}
```

### Styling the scrollbar and thumb

Pass any styling you need to the `Modifier` of your `Scrollbar` component.

You can customize the looks of your thumb using the modifier of the `Thumb` component.

Here is an example of a semi-transparent scrollbar, with a fully rounded, semi-transparent thumb:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollAreaState(lazyListState)

ScrollArea(state = state) {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.TopEnd)
            .background(Color.Black.copy(0.1f))
            .fillMaxHeight()
            .width(12.dp)
            .padding(2.dp)
    ) {
        Thumb(Modifier.background(Color.Black.copy(0.3f), RoundedCornerShape(100)))
    }
}
```

### Automatically hide the scrollbar

By default, the scrollbar will always remain visible on the screen.

To modify this behavior, pass the customization you need using the `thumbVisibility` parameter of the `Thumb` component.

Here is an example of automatically hiding the scrollbar while idling for 0.5 seconds:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollAreaState(lazyListState)

ScrollArea(state = state) {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    VerticalScrollbar(modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()) {
        Thumb(
            modifier = Modifier.background(Color.Black.copy(0.3f), RoundedCornerShape(100)),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
                enter = fadeIn(),
                exit = fadeOut(),
                hideDelay = 0.5.seconds
            )
        )
    }
}
```

### Customize the overscroll effect

By default, the scroll area will use the platform's default `OverscrollEffect`.

To remove it or provide your own custom effect, pass a new one via the `overscrollEffect` parameter:

```kotlin
val lazyListState = rememberLazyListState()

ScrollArea(
    overscrollEffect = null,
    state = rememberScrollAreaState(lazyListState),
) {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.TopEnd)
            .fillMaxHeight()
            .width(4.dp)
    ) {
        Thumb(Modifier.background(Color.LightGray))
    }
}
```

### Disable thumb dragging

By default, pressing on the thumb will causes the contents of the scroll area to scroll (fast scroll).

To disable this behavior, pass `enabled = false` to the `Thumb` component:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollAreaState(lazyListState)

ScrollArea(state = state) {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    VerticalScrollbar(modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()) {
        Thumb(
            modifier = Modifier.background(Color.Black.copy(0.3f), RoundedCornerShape(100)),
            enabled = false
        )
    }
}
```

<style>
.parameter {
    white-space: nowrap
}
</style>

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>