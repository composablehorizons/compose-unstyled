---
title: Drawer
description: An unstyled, draggable edge-attached panel for drawers, side sheets, and sheets.
---

<UnstyledDemo id="drawer" />

The default demo shows a modal bottom drawer with a draggable handle.

## Features

- Named snap points for closed, peek, and expanded panel states
- Start, end, top, and bottom placement
- Modal, overlay, and in-place presentation without prescribed visuals
- Gesture, outside-click, Back, Escape, and accessibility dismissal support
- Caller-controlled Android system-bar icon appearance while a drawer is presented

## Installation

```kotlin
implementation("com.composables:composeunstyled-drawer:2.9.2")
```

## Composition

```kotlin
DrawerHost {
  UnstyledDrawer {
    Viewport {
      Panel {
        DragHandle()
      }
    }

    SwipeArea()
  }
}
```

## Concepts

- `UnstyledDrawerState` maps your values to visible panel sizes and controls movement between them.
- `DrawerSnapPoints` associates each state value with `Zero`, `ContentSize`, or a custom visible size.
- `UnstyledDrawer` selects the placement, presentation, dismissal behavior, and caller-owned overlay.
- `Viewport` defines the finite area used to measure the panel and resolve snap points.
- `Panel` is the draggable surface. Its modifier and content own all visual and size choices.
- `DrawerHost` provides the portal layer required by `DrawerPresentation.Overlay`.

## Accessibility

Use `DragHandle` when the drawer can move between snap points. It exposes expand, collapse, and
dismiss actions to assistive technology. Modal drawers block outside interaction while open and
expose outside dismissal when a `Zero` snap point and `dismissOnClickOutside` are present.

Give the panel content an appropriate accessible name and use semantic controls for its actions.

## Choosing a presentation

Use `Modal` for a flow that stands apart from the current screen, such as a modal bottom sheet,
account picker, or confirmation panel. It renders in a modal layer and blocks interaction behind
the drawer. It does not need `DrawerHost`.

Use `Overlay` for a drawer that covers the app without taking control away from it, such as a
desktop inspector, navigation panel, or shortcut tray. It renders through `DrawerHost` and leaves
the rest of the app interactive. It is the only presentation that supports `SwipeArea`.

Use `InPlace` when the drawer belongs to one bounded part of the UI, such as a preview pane,
editor, or embedded workflow. It renders where you declare it without a modal layer or portal. Its
panel overlays its `Viewport`; it does not push sibling content.

## Code Examples

### Show and hide a drawer

Assign `targetValue` to animate to a supported value. A value mapped to `DrawerSnapPoint.Zero`
represents a fully closed drawer.

```kotlin expandable
enum class DrawerValue { Closed, Open }

val drawerState = remember {
  UnstyledDrawerState(
    initialValue = DrawerValue.Closed,
    snapPoints = DrawerSnapPoints {
      DrawerValue.Closed at DrawerSnapPoint.Zero
      DrawerValue.Open at DrawerSnapPoint.ContentSize
    },
  )
}

UnstyledButton(
  onClick = { drawerState.targetValue = DrawerValue.Open },
  modifier = Modifier
    .background(Color.White)
    .border(1.dp, Color.Black),
) {
  BasicText("Open drawer")
}

UnstyledDrawer(state = drawerState) {
  Viewport {
    Panel(
      modifier = Modifier
        .background(Color.White)
        .border(1.dp, Color.Black),
    ) {
      UnstyledButton(
        onClick = { drawerState.targetValue = DrawerValue.Closed },
        modifier = Modifier
          .background(Color.White)
          .border(1.dp, Color.Black),
      ) {
        BasicText("Close drawer")
      }
    }
  }
}
```

### Add a peek state

Use a percentage snap point to keep part of the drawer visible.

<UnstyledDemo id="drawer-peek" />

### Set a custom snap point

This drawer's middle state uses half of the viewport height.

<UnstyledDemo id="drawer-custom-snap-point" />

### Build a side sheet

Set the placement to `Start` and give the panel a fixed width.

<UnstyledDemo id="drawer-side-sheet" />

### Open from the screen edge

Add `SwipeArea` to an overlay drawer to open it from the outlined edge target.

<UnstyledDemo id="drawer-swipe-area" />

### Add an overlay

Provide `Overlay` to render caller-owned content behind the drawer.

<UnstyledDemo id="drawer-overlay" />

### Disable gestures

Set `gesturesEnabled` to `false` when the drawer should only move through app controls.

```kotlin expandable
UnstyledDrawer(
  state = drawerState,
  gesturesEnabled = false,
) {
  Viewport {
    Panel {
      Text("Drawer content")
    }
  }
}
```

### Disable outside click dismissal

Set `dismissOnClickOutside` to `false` when a click outside a modal drawer must not close it.

```kotlin expandable
UnstyledDrawer(
  state = drawerState,
  dismissOnClickOutside = false,
) {
  Viewport {
    Panel {
      Text("Drawer content")
    }
  }
}
```

### Disable Back and Escape dismissal

Set `dismissOnNavigateBack` to `false` when Back and Escape must not close the drawer.

```kotlin expandable
UnstyledDrawer(
  state = drawerState,
  dismissOnNavigateBack = false,
) {
  Viewport {
    Panel {
      Text("Drawer content")
    }
  }
}
```

### Prevent a state change

Return `false` from `confirmValueChange` to reject a transition, such as closing an unsaved form.

<UnstyledDemo id="drawer-confirm-value-change" />

### Control Android system-bar icons

Set `systemUi` to match the icon color needed by your drawer. The drawer applies the requested
appearance while it is presented and restores the previous appearance after it closes.

```kotlin expandable
UnstyledDrawer(
  state = drawerState,
  placement = DrawerPlacement.Bottom,
  presentation = DrawerPresentation.Modal,
  systemUi = SystemUi(
    statusBar = SystemUiAppearance.Dark,
    navigationBar = SystemUiAppearance.Dark,
  ),
) {
  Viewport {
    Panel(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .border(1.dp, Color.Black),
    ) {
      BasicText("Drawer with dark system-bar icons")
    }
  }
}
```

### Working with the soft keyboard

Use the IME window inset in `Viewport` to keep the drawer above the soft keyboard.

<UnstyledDemo id="drawer-form" />

### Applying Overscroll Effect

Pass an `OverscrollEffect` to `Panel` to customize its motion beyond a snap point.

<UnstyledDemo id="drawer-overscroll" />

### Add spacing around a drawer

Set `Viewport` window insets to leave space around the drawer.

<UnstyledDemo id="drawer-spacing" />

### Handle dynamic content

Use `DrawerSnapPoint.ContentSize` to track a panel as its content changes.

<UnstyledDemo id="drawer-dynamic-content" />

### Detect why a drawer was dismissed

Read the dismissal reason from the change passed to `onDismissed`.

<UnstyledDemo id="drawer-dismissal-reason" />

## API Reference

<ApiReference declaration="com.composeunstyled.UnstyledDrawer" />
<ApiReference declaration="com.composeunstyled.DrawerSnapPoint" />
<ApiReference declaration="com.composeunstyled.DrawerHost" />
<ApiReference declaration="com.composeunstyled.DrawerScope.SwipeArea" />
<ApiReference declaration="com.composeunstyled.DrawerScope.Viewport" />
<ApiReference declaration="com.composeunstyled.DrawerViewportScope.Panel" />
<ApiReference declaration="com.composeunstyled.DrawerPanelScope.DragHandle" />
<ApiReference declaration="com.composeunstyled.DrawerOverlayScope.Overlay" />
<ApiReference declaration="com.composeunstyled.SystemUi" />
