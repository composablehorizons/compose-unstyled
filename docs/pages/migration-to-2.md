---
title: Migration to 2.0
description: How to migrate Compose Unstyled projects to 2.0.
---


Compose Unstyled 2.0 removes the deprecated 1.x APIs and makes primitives more strictly unstyled.
The last 1.x release is `1.49.9`, published on 2025-04-24. The first 2.0 release is `2.0.0`,
published on 2026-05-11. Migrate to `1.49.9` first, apply the available IDE `ReplaceWith()` fixes,
then upgrade to `2.0.0`.

## Need help?

Check the [Compose Unstyled community](community.md) to ask questions about your migration.

## What changed

2.0 is a breaking release focused on three changes:

- The old aggregate `composeunstyled` artifact was removed.
- The old `com.composables.core` package was removed.
- Primitives no longer apply styling, layout, or sizing opinions for you.

Most migrations are either dependency changes or moving visual parameters into your own layout and
modifiers.

## Update dependencies

If you want the same broad API surface as 1.x, replace the old aggregate artifact with
`composeunstyled-primitives`:

```kotlin
implementation("com.composables:composeunstyled-primitives")
```

For smaller dependency graphs, depend only on the modules you use:

```kotlin
implementation("com.composables:composeunstyled-button")
implementation("com.composables:composeunstyled-dropdown-menu")
implementation("com.composables:composeunstyled-text-field")
implementation("com.composables:composeunstyled-theming")
```

Primitives and theming are now separate. Add `composeunstyled-theming` when you use `Text`,
`LocalContentColor`, `LocalTextStyle`, `ProvideContentColor`, `ProvideTextStyle`, themes, or minimum
interactive size helpers.

## Update imports

Remove imports from `com.composables.core`. Bottom Sheet and Modal Bottom Sheet APIs now live in
`com.composeunstyled`.

```kotlin
// 1.x
import com.composables.core.BottomSheet
import com.composables.core.ModalBottomSheet

// 2.0
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.UnstyledModalBottomSheet
```

The theming APIs moved under `com.composeunstyled.theme`:

```kotlin
import com.composeunstyled.theme.LocalContentColor
import com.composeunstyled.theme.Text
```

## Move styling to your code

2.0 primitives expose behavior and slots. Visual parameters such as `shape`, `backgroundColor`,
`contentColor`, `borderColor`, layout arrangements, and many padding/layout parameters were removed
from component APIs.

Move those decisions into modifiers, wrappers, or your design-system components:

```kotlin
// 1.x
UnstyledButton(
  onClick = onClick,
  shape = RoundedCornerShape(8.dp),
  backgroundColor = Color.Black,
  contentColor = Color.White,
  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
) {
  Text("Save")
}

// 2.0
UnstyledButton(
  onClick = onClick,
  modifier = Modifier
    .clip(RoundedCornerShape(8.dp))
    .background(Color.Black),
  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
) {
  Text("Save", color = Color.White)
}
```

This applies across primitives: arrange slots with `Row`, `Column`, `Box`, or your own component
wrappers instead of relying on the primitive to create internal layout.

## Replace removed deprecated names

All deprecated 1.x APIs were removed. Prefer this order:

1. Upgrade to `1.49.9`.
2. Build the project and use IDE quick fixes for deprecated APIs with `ReplaceWith()`.
3. Upgrade to `2.0.0`.
4. Fix the remaining behavioral API changes below.

Common replacements:

| 1.x | 2.0 |
| --- | --- |
| `Button` | `UnstyledButton` |
| `DropdownMenu` | `UnstyledDropdownMenu` |
| `DropdownMenuPanel` | `DropdownMenuPanel` scoped inside `UnstyledDropdownMenu` |
| `TabGroup`, `TabList`, `Tab`, `TabPanel` | `UnstyledTabGroup` with scoped `TabList`, `Tab`, and `TabPanel` |
| `ScrollArea` | Your own scroll container plus `UnstyledVerticalScrollbar` or `UnstyledHorizontalScrollbar` |
| `ScrollAreaState` | `ScrollbarState` from `rememberScrollbarState(...)` |
| `ModalBottomSheet` | `UnstyledModalBottomSheet` |

## Component-specific changes

### Bottom Sheet

`BottomSheet` was split into a container and panel:

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

Use `sheetState.targetDetent`, `animateTo(...)`, or `jumpTo(...)` to move the sheet. Modal Bottom
Sheet now reuses the same `Sheet` and `DragIndication` model.

### Modal Bottom Sheet

Pass dimming UI through the new `overlay` slot. The sheet is IME-aware by default through
`ModalBottomSheetProperties.offsetForIme`.

```kotlin
val sheetState = rememberModalBottomSheetState(
  initialDetent = SheetDetent.Hidden,
)

UnstyledModalBottomSheet(
  state = sheetState,
  overlay = {
    Scrim()
  },
) {
  Sheet {
    DragIndication()
  }
}
```

### Dialog

`UnstyledDialog` is controlled with `visible`. Put the rendered dialog content in `DialogPanel` and
use `paneTitle` when the dialog needs an accessible pane title.

```kotlin
var visible by remember { mutableStateOf(false) }

UnstyledDialog(
  visible = visible,
  onDismissRequest = { visible = false },
) {
  DialogPanel(paneTitle = "Settings") {
    Text("Settings")
  }
}
```

### Disclosure

`UnstyledDisclosure` is now controlled:

```kotlin
var expanded by remember { mutableStateOf(false) }

UnstyledDisclosure(
  expanded = expanded,
  onExpandedChange = { expanded = it },
) {
  DisclosureButton {
    Text("Details")
  }
  DisclosedContent {
    Text("More information")
  }
}
```

### Dropdown Menu

Menu anchor and panel content are now slots on `UnstyledDropdownMenu`. Use `DropdownMenuPanel`
and `UnstyledDropdownMenuItem`.

Dropdown Menu also has new anchor placement parameters: `side`, `alignment`, `sideOffset`, and
`alignmentOffset`. Use them instead of the old `DropdownPanelAnchor` values:

```kotlin
var expanded by remember { mutableStateOf(false) }

UnstyledDropdownMenu(
  expanded = expanded,
  onExpandedChange = { expanded = it },
  side = AnchorSide.Bottom,
  alignment = AnchorAlignment.End,
  sideOffset = 8.dp,
  alignmentOffset = 0.dp,
  panel = {
    DropdownMenuPanel {
      UnstyledDropdownMenuItem(onClick = { expanded = false }) {
        Text("Item")
      }
    }
  },
  anchor = {
    Text("Open")
  },
)
```

### Tooltip

Tooltip placement is now controlled with the same anchor placement model as Dropdown Menu. Use
`side`, `alignment`, `sideOffset`, and `alignmentOffset` on `UnstyledTooltip`.

`TooltipPanel` is scoped inside `UnstyledTooltip`, and its content receives `TooltipPlacement` so
custom visuals can react to the resolved placement:

```kotlin
UnstyledTooltip(
  side = AnchorSide.Top,
  alignment = AnchorAlignment.Center,
  sideOffset = 8.dp,
  alignmentOffset = 0.dp,
  panel = {
    TooltipPanel { placement ->
      Text("Placed on ${placement.side}")
    }
  },
  anchor = {
    Text("Help")
  },
)
```

### Text Field

`UnstyledTextField` now uses Compose's state-based text field API. Store text in a `TextFieldState`
and render the actual editable text through the scoped `TextInput` slot.

Text Field does not provide leading or trailing icon slots. Place icons in your own layout around
`TextInput`:

```kotlin
val state = rememberTextFieldState()

UnstyledTextField(state = state) {
  Row {
    SearchIcon()
    TextInput(
      placeholder = {
        Text("Email")
      },
    )
  }
}
```

### Slider

`UnstyledSlider` now exposes `track` and `thumb` slots that receive a `SliderState`:

```kotlin
var value by remember { mutableStateOf(0f) }

UnstyledSlider(
  value = value,
  onValueChange = { value = it },
  modifier = Modifier.fillMaxWidth(),
  track = { state ->
    Box(
      Modifier
        .fillMaxWidth()
        .height(8.dp)
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(100.dp)),
    ) {
      Box(
        Modifier
          .fillMaxHeight()
          .fillMaxWidth()
          .background(Color(0xFFCACACA)),
      )
      Box(
        Modifier
          .fillMaxHeight()
          .fillMaxWidth(state.fraction)
          .background(Color.Black),
      )
    }
  },
  thumb = { state ->
    val thumbColor = if (state.isDragging) Color.DarkGray else Color.Black

    Box(
      modifier = Modifier
        .size(18.dp)
        .clip(CircleShape)
        .background(thumbColor),
    )
  },
)
```

### Checkbox, TriState Checkbox, and Radio Group

Indicators are scoped child APIs so they can receive the primitive interaction source:

```kotlin
var checked by remember { mutableStateOf(false) }

UnstyledCheckbox(
  checked = checked,
  onCheckedChange = { checked = it },
) {
  CheckedIndicator()
}
```

Radio groups also support generic values and scope `RadioButton` to `RadioGroupScope`.

### Toggle Switch

Switch behavior and thumb placement are split. Put the visual thumb in `SwitchThumb`:

```kotlin
var checked by remember { mutableStateOf(false) }

UnstyledSwitch(
  checked = checked,
  onCheckedChange = { checked = it },
) {
  SwitchThumb()
}
```

### Scrollbars

`ScrollArea` was removed. Build the scrollable layout yourself and connect scrollbars with
`rememberScrollbarState(...)`:

```kotlin
val scrollState = rememberScrollState()
val scrollbarState = rememberScrollbarState(scrollState)

Box {
  Column(Modifier.verticalScroll(scrollState)) {
    // content
  }

  UnstyledVerticalScrollbar(scrollbarState) {
    Thumb()
  }
}
```

## Review behavior changes

After the project compiles, review the screens that use migrated primitives:

- Add your own size constraints if a primitive used to fill or align content for you.
- Add your own background, clipping, border, text color, and content color propagation.
- Check modal, dialog, sheet, tooltip, and menu dismissal paths.
- Check keyboard navigation for dropdown menus, tab groups, radio groups, sliders, and sheets.
- Check scrollable content with scrollbars and bottom sheets.

2.0 keeps accessibility and behavior in the primitives, but your app now owns the visual and layout
contract around them.
