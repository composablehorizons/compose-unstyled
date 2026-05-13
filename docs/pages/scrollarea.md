---
title: Scrollbars
description: Foundational, renderless vertical and horizontal scrollbar components with customizable thumbs. Supports Column, Row, LazyColumn, LazyRow, LazyVerticalGrid and LazyHorizontalGrid.
---

<UnstyledDemo id="scrollbars" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-scrollbars")
```

## Basic Example

Scrollbars consist of the following components: `rememberScrollbarState`, `UnstyledVerticalScrollbar`, `UnstyledHorizontalScrollbar`, and `Thumb`.

`rememberScrollbarState` adapts a `ScrollState`, `LazyListState`, or `LazyGridState` into the state used by scrollbar primitives.

The `UnstyledVerticalScrollbar`/`UnstyledHorizontalScrollbar` components represent the track of the scrollbar and accept a thumb
component.

The `Thumb` represents the thumb of a scrollbar and will automatically sync their position and size according to the
scrolled position of the connected scrollbar state.

Here is a simple example of adding vertical scrollbar to a `LazyColumn`:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollbarState(lazyListState)

Box {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    UnstyledVerticalScrollbar(
        scrollbarState = state,
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

Create a scrollbar state from your `LazyColumn` or `LazyList` state and pass it to the scrollbar:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollbarState(lazyListState)

Box {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    UnstyledVerticalScrollbar(
        scrollbarState = state,
        modifier = Modifier.align(Alignment.TopEnd)
            .fillMaxHeight()
            .width(4.dp)
    ) {
        Thumb(Modifier.background(Color.LightGray))
    }
}
```

### Add scrollbars to LazyGrids

Create a scrollbar state from your `LazyVerticalGrid` or `LazyHorizontalGrid` state and pass it to the scrollbar:

```kotlin
val lazyGridState = rememberLazyGridState()
val state = rememberScrollbarState(lazyGridState)

Box {
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
    UnstyledVerticalScrollbar(
        scrollbarState = state,
        modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()
    ) {
        Thumb(
            modifier = Modifier.background(Color.Black.copy(0.3f), RoundedCornerShape(100)),
        )
    }
}
```

### Add scrollbars to Column and Row

Create a scrollbar state from the scroll state you pass to `verticalScroll` or `horizontalScroll`:

```kotlin
val scrollState = rememberScrollState()
val state = rememberScrollbarState(scrollState)

Box {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        repeat(50) { i ->
            BasicText(i.toString())
        }
    }
    UnstyledVerticalScrollbar(
        scrollbarState = state,
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
val state = rememberScrollbarState(lazyListState)

Box {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    UnstyledVerticalScrollbar(
        scrollbarState = state,
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
val state = rememberScrollbarState(lazyListState)

Box {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    UnstyledVerticalScrollbar(
        scrollbarState = state,
        modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()
    ) {
        Thumb(
            modifier = Modifier.background(Color.Black.copy(0.3f), RoundedCornerShape(100)),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
                enter = fadeIn(),
                exit = fadeOut(),
                hideDelay = 2.seconds
            )
        )
    }
}
```

### Overscroll behavior

Scrollbars no longer own scrolling layout or overscroll behavior. Apply overscroll behavior to the scrollable component itself using Compose Foundation's scroll APIs.

### Disable thumb dragging

By default, pressing on the thumb causes the connected content to scroll.

To disable this behavior, pass `enabled = false` to the `Thumb` component:

```kotlin
val lazyListState = rememberLazyListState()
val state = rememberScrollbarState(lazyListState)

Box {
    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        repeat(50) { i ->
            item {
                BasicText("Item #${i}")
            }
        }
    }
    UnstyledVerticalScrollbar(
        scrollbarState = state,
        modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()
    ) {
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
