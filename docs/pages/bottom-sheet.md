---
title: Bottom Sheet
description: A draggable bottom sheet with custom detents.
---

<UnstyledDemo id="bottom-sheet" />

## Features

- Custom detents
- Soft-keyboard support
- Dynamic content sizing
- Scrollable content without fixed height

## Installation

```kotlin
implementation("com.composables:composeunstyled-bottom-sheet")
```

## Anatomy

```kotlin
val sheetState = rememberBottomSheetState(
  initialDetent = SheetDetent.Hidden,
)

UnstyledBottomSheet(state = sheetState) {
  Sheet {
    DragIndication()
  }
}
```

## Concepts

- `SheetDetent` defines a height where the sheet can rest.
- `UnstyledBottomSheet` represents the draggable container the `Sheet` is dragged in.
- `Sheet` is the rendered bit of the sheet.
- `DragIndication` adds an interactive handle for expand, collapse, and dismiss actions.

## Accessibility

Use the `DragIndication` component when your sheet can move between multiple detents. It provides semantic expand,
collapse, and dismiss actions so users can control the sheet without dragging.

## Code Examples

### Showing and hiding the sheet

Use the `targetDetent` property to animate the sheet to a new detent:

```kotlin
val sheetState = rememberBottomSheetState(
  initialDetent = SheetDetent.Hidden,
)

BasicText(
  text = "Show sheet",
  modifier = Modifier.clickable {
    sheetState.targetDetent = SheetDetent.FullyExpanded
  }
)

UnstyledBottomSheet(state = sheetState) {
  Sheet {
    BasicText(
      text = "Hide sheet",
      modifier = Modifier.clickable {
        sheetState.targetDetent = SheetDetent.Hidden
      }
    )
  }
}
```

### Waiting for the sheet animation

Use the suspend `animateTo()` function to wait until the sheet animation is done:

```kotlin
val scope = rememberCoroutineScope()

BasicText(
  text = "Show sheet",
  modifier = Modifier.clickable {
    scope.launch {
      sheetState.animateTo(SheetDetent.FullyExpanded)
    }
  }
)
```

### Moving the sheet instantly

Use the `jumpTo()` function to move to a detent without animation:

```kotlin
BasicText(
  text = "Open immediately",
  modifier = Modifier.clickable {
    sheetState.jumpTo(SheetDetent.FullyExpanded)
  }
)
```

### Creating sheets with custom detents

Use the `SheetDetent` constructor to create a new detent. Pass a unique identifier and a function
that calculates the detent height. The calculated height cannot be smaller than `0.dp`, taller than
the container, or taller than the sheet content.

Keep this calculation fast. It runs during sheet measurement.

Make sure to pass your new detent when creating your bottom sheet state:

```kotlin
val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
  containerHeight * 0.6f
}

val sheetState = rememberBottomSheetState(
  initialDetent = Peek,
  detents = listOf(SheetDetent.Hidden, Peek, SheetDetent.FullyExpanded),
)

UnstyledBottomSheet(state = sheetState) {
  Sheet {
    DragIndication()
  }
}
```

### Updating detents after the state is created

Use the `invalidateDetents()` function to recalculate sheet detents. This is useful when a custom
detent reads a measured value that can change, such as a header height:

```kotlin
val peekHeight = remember { mutableStateOf(96.dp) }
val Peek = remember {
  SheetDetent("peek") { _, _ -> peekHeight.value }
}

val sheetState = rememberBottomSheetState(
  initialDetent = Peek,
  detents = listOf(Peek, SheetDetent.FullyExpanded),
)

LaunchedEffect(peekHeight.value) {
  sheetState.invalidateDetents()
}
```

### Using scrollable sheet content

Use a scrollable layout inside the `Sheet` component to make content scroll within the current detent height:

<UnstyledDemo id="bottom-sheet-scrollable-content" />

### Working with the soft keyboard

Use the `offsetForIme` parameter to automatically move the sheet above the soft keyboard:

```kotlin
var value by remember { mutableStateOf("") }

UnstyledBottomSheet(
  state = sheetState,
  offsetForIme = true,
) {
  Sheet {
    BasicTextField(
      value = value,
      onValueChange = { value = it },
    )
  }
}
```

### Customizing sheet animation between detents

Use the `animationSpec` parameter to customize the default animation between detents:

```kotlin
val sheetState = rememberBottomSheetState(
  initialDetent = SheetDetent.Hidden,
  animationSpec = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow,
  ),
)
```

<ApiReference id="bottom-sheet" />
