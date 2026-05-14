---
title: Portal
description: A same-window portal utility for rendering content from one place in composition into a shared host.
---

## Installation

```kotlin
implementation("com.composables:composeunstyled-portal")
```

## Anatomy

```kotlin
PortalHost {
  Portal {
    BasicText("Portal content")
  }
}
```

## General Usage

- `PortalHost` provides the destination where portal content is rendered.
- `Portal` sends content to the nearest `PortalHost`, or renders nothing when no host is available.

## Code Examples

### Rendering content in a portal

Place `PortalHost` above the content that needs to render portals:

```kotlin
PortalHost {
  BasicText("Screen content")

  Portal {
    BasicText("Portal content")
  }
}
```

### Showing portal content conditionally

Add or remove the `Portal` from composition to control whether its content is rendered:

```kotlin
var showPortal by remember { mutableStateOf(false) }

PortalHost {
  BasicText(
    text = "Show portal",
    modifier = Modifier.clickable { showPortal = true }
  )

  if (showPortal) {
    Portal {
      BasicText("Portal content")
    }
  }
}
```

### Hosting multiple portals

A single `PortalHost` can render content from multiple `Portal` calls:

```kotlin
PortalHost {
  Portal {
    BasicText("First portal")
  }

  Portal {
    BasicText("Second portal")
  }
}
```

<ApiReference id="portal" />
