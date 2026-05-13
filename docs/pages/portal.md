---
title: Portal
description: Render content into a same-window portal.
---

`PortalHost` and `Portal` let you render content later in the same Compose hierarchy without changing the parent layout.

Use portals for floating or overlay UI that should escape local layout constraints but still stay in the same window, such as custom menus, popovers, teaching bubbles, or non-modal overlays.

## Installation

```kotlin
implementation("com.composables:composeunstyled-portal")
```

## Host content

Wrap the part of your app that should support portals with `PortalHost`.

```kotlin
@Composable
fun App() {
    PortalHost(Modifier.fillMaxSize()) {
        AppContent()
    }
}
```

`PortalHost` lays out its normal `content` first. Any active `Portal` entries are rendered after that content inside a `Box` that matches the host size.

## Render into the portal

Call `Portal` from inside a `PortalHost` subtree.

```kotlin
@Composable
fun PopoverExample(expanded: Boolean) {
    Button(onClick = { /* update expanded */ }) {
        Text("Open")
    }

    if (expanded) {
        Portal {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.24f))
            )
        }
    }
}
```

The portal content is registered while the composable is in the composition and removed automatically when it leaves the composition.

## Missing host

If `Portal` is used outside a `PortalHost`, it returns without rendering content. Add a `PortalHost` around the app surface, screen, or component subtree that owns the floating content.

## Portal vs Modal

Use `Portal` when content should stay in the same window and you only need a rendering layer.

Use `Modal`, `UnstyledDialog`, or `UnstyledModalBottomSheet` when you need modal lifecycle, focus, accessibility, or platform window behavior.
