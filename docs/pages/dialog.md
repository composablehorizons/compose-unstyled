---
title: Dialog
description: A modal dialog component with dismiss behavior and panel transitions.
---

<UnstyledDemo id="dialog" />

## Features

- Modal focus behavior
- Outside-click dismiss
- Back and Escape dismiss
- Panel enter and exit transitions

## Installation

```kotlin
implementation("com.composables:composeunstyled-dialog:{{compose_unstyled_version}}")
```

## Anatomy

```kotlin
UnstyledDialog(
  visible = visible,
  onDismissRequest = { visible = false },
) {
  DialogPanel {
  }
}
```

## Concepts

- `UnstyledDialog` renders dialog content in a modal layer.
- `DialogPanel` renders the focusable dialog content.

## Accessibility

Use the `paneTitle` parameter on `DialogPanel` when the dialog has a clear title.

## Code Examples

### Showing and hiding a dialog

Use the `visible` parameter to show the dialog and the `onDismissRequest` callback to update that state:

```kotlin expandable
var visible by remember { mutableStateOf(false) }

BasicText(
  text = "Show dialog",
  modifier = Modifier.clickable { visible = true },
)

UnstyledDialog(
  visible = visible,
  onDismissRequest = { visible = false },
) {
  DialogPanel {
    BasicText("Dialog content")
  }
}
```

### Adding an overlay behind a dialog

Use the `overlay` parameter to render content behind the dialog panel. `Scrim` provides a
ready-made overlay for dialogs.

```kotlin expandable
UnstyledDialog(
  visible = visible,
  onDismissRequest = { visible = false },
  overlay = { Scrim() },
) {
  DialogPanel {
    BasicText("Dialog content")
  }
}
```

### Changing dismiss behavior

Use the `properties` parameter to control how the dialog can be dismissed:

```kotlin expandable
UnstyledDialog(
  visible = visible,
  onDismissRequest = { visible = false },
  properties = DialogProperties(
    dismissOnBackPress = false,
    dismissOnClickOutside = false,
  ),
) {
  DialogPanel {
    BasicText("Dialog content")
  }
}
```

### Animating the dialog panel

Use the `enter` and `exit` parameters on `DialogPanel` to animate the dialog panel:

```kotlin expandable
UnstyledDialog(
  visible = visible,
  onDismissRequest = { visible = false },
) {
  DialogPanel(
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    BasicText("Dialog content")
  }
}
```

## API Reference

<ApiReference declaration="com.composeunstyled.UnstyledDialog" />
<ApiReference declaration="com.composeunstyled.DialogOverlayScope.Scrim" />
<ApiReference declaration="com.composeunstyled.DialogScope.DialogPanel" />
