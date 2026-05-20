---
title: Dropdown Menu
description: An anchored menu component with keyboard navigation and custom placement.
---

<UnstyledDemo id="dropdown-menu" />

## Features

- Anchor-based placement
- Keyboard menu navigation
- Auto-dismiss on outside click
- Panel enter and exit transitions

## Installation

```kotlin
implementation("com.composables:composeunstyled-dropdown-menu")
```

## Anatomy

```kotlin
UnstyledDropdownMenu(
  expanded = expanded,
  onExpandedChange = onExpandedChange,
  panel = {
    DropdownMenuPanel {
      MenuItem(onClick = onClick) {
      }
    }
  },
  anchor = {
  },
)
```

## Concepts

- `UnstyledDropdownMenu` marks the anchor area and renders the floating menu when expanded.
  - The `anchor` slot renders the content the menu is positioned against.
  - The `panel` slot renders the floating menu content.
- `DropdownMenuPanel` renders the menu surface and arranges direct children vertically.
- `MenuItem` renders a focusable item inside `DropdownMenuPanel`. Use direct `MenuItem` children for managed keyboard navigation. Custom nested layouts can be rendered inside the panel, but nested items are not part of the panel-managed menu order.

## Accessibility

Dropdown menu handles keyboard interactions out of the box. Pressing Arrow Down opens the menu and
focuses the first item. Pressing Arrow Down and Arrow Up moves focus to the next and previous item.
Home moves focus to the first item. End moves focus to the last item. Escape closes the menu. Use
`MenuItem` for focusable menu actions.

## Code Examples

### Opening and closing a dropdown menu

Use the `expanded` parameter to show the menu and the `onExpandedChange` callback to update that state:

```kotlin
var expanded by remember { mutableStateOf(false) }

UnstyledDropdownMenu(
  expanded = expanded,
  onExpandedChange = { expanded = it },
  panel = {
    DropdownMenuPanel {
      MenuItem(onClick = { expanded = false }) {
        BasicText("Close")
      }
    }
  },
  anchor = {
    BasicText(
      text = "Open menu",
      modifier = Modifier.clickable { expanded = true },
    )
  },
)
```

### Positioning a dropdown menu

Use the `side`, `alignment`, `sideOffset`, and `alignmentOffset` parameters to place the menu panel relative to the anchor:

```kotlin
UnstyledDropdownMenu(
  expanded = expanded,
  onExpandedChange = { expanded = it },
  side = AnchorSide.Bottom,
  alignment = AnchorAlignment.End,
  sideOffset = 8.dp,
  panel = {
    DropdownMenuPanel {
      MenuItem(onClick = { select() }) {
        BasicText("Item")
      }
    }
  },
  anchor = {
    BasicText("Open menu")
  },
)
```

### Closing the menu after clicking an item

`MenuItem` closes the menu after click by default by calling the dropdown's `onExpandedChange`
callback with `false`:

```kotlin
UnstyledDropdownMenu(
  expanded = expanded,
  onExpandedChange = { expanded = it },
  panel = {
    DropdownMenuPanel {
      MenuItem(onClick = { select() }) {
        BasicText("Item")
      }
    }
  },
  anchor = {
    BasicText("Open menu")
  },
)
```

### Keeping the menu open after clicking an item

Use the `closeOnClick` parameter when a menu item should update state without dismissing the menu:

```kotlin
UnstyledDropdownMenu(
  expanded = expanded,
  onExpandedChange = { expanded = it },
  panel = {
    DropdownMenuPanel {
      MenuItem(
        closeOnClick = false,
        onClick = { enabled = enabled.not() },
      ) {
        BasicText("Toggle option")
      }
    }
  },
  anchor = {
    BasicText("Open menu")
  },
)
```

### Animating the dropdown menu

Use the `enter` and `exit` parameters on `DropdownMenuPanel` to animate the menu panel:

```kotlin
UnstyledDropdownMenu(
  expanded = expanded,
  onExpandedChange = { expanded = it },
  panel = {
    DropdownMenuPanel(
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      MenuItem(onClick = { select() }) {
        BasicText("Item")
      }
    }
  },
  anchor = {
    BasicText("Open menu")
  },
)
```

<ApiReference id="dropdown-menu" />
