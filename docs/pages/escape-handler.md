---
title: Escape Handler
description: A multiplatform handler for dismissing UI from Escape or Back interactions.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-escape-handler")
```

## Basic Example

Use `EscapeHandler` when a visible surface should close from Escape or Back interactions.

```kotlin
var visible by remember { mutableStateOf(false) }

if (visible) {
    EscapeHandler {
        visible = false
    }
}
```

## Usage

`EscapeHandler` is useful for dismissible surfaces such as dialogs, modals, menus, and custom overlays.
