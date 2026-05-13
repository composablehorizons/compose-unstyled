---
title: Icon
description: A component for rendering iconography with the tinting of your choice.
---

<UnstyledDemo id="icon" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-icon")
```

## Basic Example

Basic example using Icons from the Material Extended Library:

```kotlin
UnstyledIcon(
    imageVector = Icons.Rounded.Favorite,
    contentDescription = "This song is in your favorites",
    tint = Color(0xFF9E9E9E),
)
```

<style>
.parameter {
    white-space: nowrap
}
</style>

<ApiReference id="icon" />
