---
title: Modal Bottom Sheet
description: A dismissible modal bottom sheet with custom detents.
---

<UnstyledDemo id="modal-bottom-sheet" />

## Features

- Custom detents
- Custom overlay content
- Back press and outside click dismissal
- Dynamic content sizing
- Soft-keyboard support
- Scrollable content without fixed height

## Installation

```kotlin
implementation("com.composables:composeunstyled-modal-bottom-sheet")
```

## Anatomy

```kotlin
val sheetState = rememberModalBottomSheetState(
  initialDetent = SheetDetent.Hidden,
)

UnstyledModalBottomSheet(state = sheetState) {
  Sheet {
    DragIndication()
  }
}
```

## Concepts

- `SheetDetent` defines a height where the sheet can rest.
- `UnstyledModalBottomSheet` represents the modal layer the sheet is rendered in.
- `Sheet` is the rendered bit of the sheet.
- `DragIndication` adds an interactive handle for expand, collapse, and dismiss actions.

## Accessibility

Use `DragIndication` when your sheet can move between multiple detents. It provides semantic expand, collapse, and dismiss actions so users can control the sheet without dragging.

## Code Examples

### Showing and hiding a modal bottom sheet

Use the `targetDetent` property to animate the modal sheet to a new detent:

```kotlin
val sheetState = rememberModalBottomSheetState(
  initialDetent = SheetDetent.Hidden,
)

BasicText(
  text = "Show sheet",
  modifier = Modifier.clickable {
    sheetState.targetDetent = SheetDetent.FullyExpanded
  },
)

UnstyledModalBottomSheet(state = sheetState) {
  Sheet {
    BasicText(
      text = "Hide sheet",
      modifier = Modifier.clickable {
        sheetState.targetDetent = SheetDetent.Hidden
      },
    )
  }
}
```

### Waiting for modal bottom sheet animations

Use the suspend `animateTo()` function to wait until the sheet animation is done:

```kotlin
val scope = rememberCoroutineScope()

BasicText(
  text = "Show sheet",
  modifier = Modifier.clickable {
    scope.launch {
      sheetState.animateTo(SheetDetent.FullyExpanded)
    }
  },
)
```

### Opening a modal bottom sheet instantly

Use the `jumpTo()` function to move to a detent without animation:

```kotlin
BasicText(
  text = "Open immediately",
  modifier = Modifier.clickable {
    sheetState.jumpTo(SheetDetent.FullyExpanded)
  },
)
```

### Adding an overlay behind a modal bottom sheet

Use the `overlay` parameter to render content behind the modal sheet. `Scrim` provides a
ready-made overlay for modal bottom sheets.

```kotlin
UnstyledModalBottomSheet(
  state = sheetState,
  overlay = { Scrim() },
) {
  Sheet {
    BasicText("Sheet content")
  }
}
```

### Creating modal bottom sheets with custom detents

Use the `SheetDetent` constructor to create a new detent. Pass a unique identifier and a function
that calculates the detent height. The calculated height cannot be smaller than `0.dp`, taller than
the container, or taller than the sheet content.

Keep this calculation fast. It runs during sheet measurement.

```kotlin
val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
  minOf(containerHeight * 0.4f, sheetHeight)
}

val sheetState = rememberModalBottomSheetState(
  initialDetent = SheetDetent.Hidden,
  detents = listOf(SheetDetent.Hidden, Peek, SheetDetent.FullyExpanded),
)
```

### Updating modal bottom sheet detents after layout changes

Use the `invalidateDetents()` function to recalculate sheet detents. This is useful when a custom
detent reads a measured value that can change, such as a header height:

```kotlin
var peekHeight by remember { mutableStateOf(120.dp) }

val Peek = remember {
  SheetDetent("peek") { _, _ -> peekHeight }
}

val sheetState = rememberModalBottomSheetState(
  initialDetent = Peek,
  detents = listOf(Peek, SheetDetent.FullyExpanded),
)

LaunchedEffect(peekHeight) {
  sheetState.invalidateDetents()
}
```

### Building modal bottom sheets with scrollable content

Use a scrollable layout inside the `Sheet` component to make content scroll within the current detent height:

<UnstyledDemo id="modal-bottom-sheet-scrollable-content" />

### Moving a modal bottom sheet above the soft keyboard

Use the `offsetForIme` parameter on `ModalBottomSheetProperties` to automatically move the sheet above the soft keyboard:

```kotlin
val textState = rememberTextFieldState()

UnstyledModalBottomSheet(
  state = sheetState,
  properties = ModalBottomSheetProperties(offsetForIme = true),
) {
  Sheet {
    BasicTextField(state = textState)
  }
}
```

### Disabling back press and outside click dismissal

Use the `properties` parameter to control how the modal sheet can be dismissed:

```kotlin
UnstyledModalBottomSheet(
  state = sheetState,
  properties = ModalBottomSheetProperties(
    dismissOnBackPress = false,
    dismissOnClickOutside = false,
  ),
) {
  Sheet {
    BasicText("Sheet content")
  }
}
```

### Reacting to modal bottom sheet dismissal

Use the `onDismiss` parameter to run code when the sheet is dismissed:

```kotlin
UnstyledModalBottomSheet(
  state = sheetState,
  onDismiss = { selectedItem = null },
) {
  Sheet {
    BasicText("Sheet content")
  }
}
```

### Customizing modal bottom sheet animation between detents

Use the `animationSpec` parameter to customize the default animation between detents. Use the
`dismissAnimationSpec` parameter to customize the animation used when the modal sheet is dismissed:

```kotlin
val sheetState = rememberModalBottomSheetState(
  initialDetent = SheetDetent.Hidden,
  animationSpec = tween(durationMillis = 300),
  dismissAnimationSpec = tween(durationMillis = 180),
)
```

<ApiReference id="modal-bottom-sheet" />
