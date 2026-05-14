---
title: Scrollbars
description: Scrollbar components for scroll state, lazy lists, and lazy grids.
---

<UnstyledDemo id="scrollbars" />

## Features

- ScrollState support
- LazyListState support
- LazyGridState support
- Draggable scrollbar thumbs

## Installation

```kotlin
implementation("com.composables:composeunstyled-scrollbars")
```

## Anatomy

```kotlin
val scrollbarState = rememberScrollbarState(scrollState)

UnstyledVerticalScrollbar(scrollbarState) {
  Thumb()
}
```

## Concepts

- `ScrollbarState` represents the scroll position used by a scrollbar.
- `UnstyledVerticalScrollbar` renders a vertical scrollbar.
- `UnstyledHorizontalScrollbar` renders a horizontal scrollbar.
- `Thumb` renders the draggable thumb inside a scrollbar.

## Code Examples

### Adding scrollbars to LazyColumn

Use the `rememberScrollbarState(LazyListState)` function to connect a scrollbar to lazy list scroll position:

```kotlin
val listState = rememberLazyListState()
val scrollbarState = rememberScrollbarState(listState)

LazyColumn(state = listState) {
  items(100) { index ->
    BasicText("Item $index")
  }
}

UnstyledVerticalScrollbar(scrollbarState) {
  Thumb()
}
```

### Adding scrollbars to LazyVerticalGrid

Use the `rememberScrollbarState(LazyGridState)` function to connect a scrollbar to lazy grid scroll position:

```kotlin
val gridState = rememberLazyGridState()
val scrollbarState = rememberScrollbarState(gridState)

LazyVerticalGrid(
  columns = GridCells.Fixed(2),
  state = gridState,
) {
  items(100) { index ->
    BasicText("Item $index")
  }
}

UnstyledVerticalScrollbar(scrollbarState) {
  Thumb()
}
```

### Adding scrollbars to scrollable content

Use the `rememberScrollbarState(ScrollState)` function for content that uses a regular scroll state:

```kotlin
val scrollState = rememberScrollState()
val scrollbarState = rememberScrollbarState(scrollState)

Column(Modifier.verticalScroll(scrollState)) {
  repeat(100) { index ->
    BasicText("Item $index")
  }
}

UnstyledVerticalScrollbar(scrollbarState) {
  Thumb()
}
```

### Hiding the scrollbar while idle

Use the `thumbVisibility` parameter to hide the thumb when the user is not interacting with the scrollable content:

```kotlin
UnstyledVerticalScrollbar(scrollbarState) {
  Thumb(
    thumbVisibility = ThumbVisibility.HideWhileIdle(
      enter = fadeIn(),
      exit = fadeOut(),
      hideDelay = 500.milliseconds,
    ),
  )
}
```

### Supporting reverse layout

Use the `reverseLayout` parameter when the scrollable content uses reverse layout:

```kotlin
UnstyledVerticalScrollbar(
  scrollbarState = scrollbarState,
  reverseLayout = true,
) {
  Thumb()
}
```

<ApiReference id="scrollarea" />
