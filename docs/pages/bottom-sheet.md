---
title: Bottom Sheet
description: A draggable bottom sheet with custom detents.
---

> **Deprecated:** Bottom Sheet will be removed in Compose Unstyled 3.0. Use [Drawer](drawer.md) with `DrawerPresentation.InPlace` instead. See [Migrating to Drawer](#migrating-to-drawer).

<UnstyledDemo id="bottom-sheet" />

## Features

- Custom detents
- Soft-keyboard support
- Dynamic content sizing
- Scrollable content without fixed height

## Installation

```kotlin
implementation("com.composables:composeunstyled-bottom-sheet:{{compose_unstyled_version}}")
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

```kotlin expandable
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

```kotlin expandable
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

```kotlin expandable
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

```kotlin expandable
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

```kotlin expandable
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

### Working with the soft keyboard

Use the `offsetForIme` parameter to automatically move the sheet above the soft keyboard:

```kotlin expandable
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

```kotlin expandable
val sheetState = rememberBottomSheetState(
  initialDetent = SheetDetent.Hidden,
  animationSpec = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow,
  ),
)
```

## Migrating to Drawer

An in-place `UnstyledDrawer` placed at the bottom replaces `UnstyledBottomSheet`. Replace the
dependency with `composeunstyled-drawer`:

```kotlin
implementation("com.composables:composeunstyled-drawer:{{compose_unstyled_version}}")
```

| Bottom Sheet | Drawer |
| --- | --- |
| `UnstyledBottomSheet(state)` | `UnstyledDrawer(state, presentation = DrawerPresentation.InPlace)` |
| `rememberBottomSheetState(initialDetent, detents)` | `remember { UnstyledDrawerState(initialValue, snapPoints) }` |
| `SheetDetent` | Your own value type, mapped in `DrawerSnapPoints { }` |
| `SheetDetent.Hidden` | `DrawerSnapPoint.Zero` |
| `SheetDetent.FullyExpanded` | `DrawerSnapPoint.ContentSize` |
| `SheetDetent("id") { containerHeight, sheetHeight -> }` | `DrawerSnapPoint { viewportSize, contentSize -> }` |
| `Sheet { }` | `Viewport { Panel { } }` |
| `DragIndication()` | `DragHandle()` inside `Panel` |
| `enabled` | `gesturesEnabled` |
| `offsetForIme = true` | `Viewport(windowInsets = WindowInsets.ime)` |
| `currentDetent` / `targetDetent` | `currentValue` / `targetValue` |
| `animateTo()` / `jumpTo()` | `animateTo()` / `jumpTo()` |
| `invalidateDetents()` | `invalidateSnapPoints()` |
| `confirmDetentChange: (SheetDetent) -> Boolean` | `confirmValueChange: (DrawerValueChange<T>) -> Boolean` |
| `velocityThreshold`, `positionalThreshold`, `decayAnimationSpec` | No equivalent |

An in-place drawer also closes on Back, Escape, and taps inside its `Viewport` but outside the
`Panel`. Set `dismissOnNavigateBack` and `dismissOnClickOutside` to `false` to keep the bottom sheet's
behavior.

Before:

```kotlin expandable
val Peek = SheetDetent("peek") { containerHeight, _ -> containerHeight * 0.6f }

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

After:

```kotlin expandable
enum class SheetValue { Hidden, Peek, Expanded }

val drawerState = remember {
  UnstyledDrawerState(
    initialValue = SheetValue.Peek,
    snapPoints = DrawerSnapPoints {
      SheetValue.Hidden at DrawerSnapPoint.Zero
      SheetValue.Peek at DrawerSnapPoint { viewportSize, _ -> viewportSize * 0.6f }
      SheetValue.Expanded at DrawerSnapPoint.ContentSize
    },
  )
}

UnstyledDrawer(
  state = drawerState,
  presentation = DrawerPresentation.InPlace,
  dismissOnNavigateBack = false,
  dismissOnClickOutside = false,
) {
  Viewport {
    Panel {
      DragHandle()
    }
  }
}
```

## API Reference

<ApiReference declaration="com.composeunstyled.rememberBottomSheetState" />
<ApiReference declaration="com.composeunstyled.BottomSheetState" />
<ApiReference declaration="com.composeunstyled.UnstyledBottomSheet" title="BottomSheet" />
<ApiReference declaration="com.composeunstyled.BottomSheetScope.Sheet" />
<ApiReference declaration="com.composeunstyled.BottomSheetScope.DragIndication" />
